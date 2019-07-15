import crypto from "crypto";
import fs from "fs";
import os from "os";
import path from "path";

export class ReadAfterDestroyedError extends Error {}

export class ReadStream extends fs.ReadStream {
  constructor(writeStream, name) {
    super("", {});
    this.name = name;

    this._writeStream = writeStream;

    // Persist terminating events.
    this.error = this._writeStream.error;
    this.addListener("error", error => {
      this.error = error;
    });

    this.open();
  }

  get ended() {
    return this._readableState.ended;
  }

  _read(n) {
    if (typeof this.fd !== "number")
      return this.once("open", function() {
        this._read(n);
      });

    // The writer has finished, so the reader can continue uninterupted.
    if (this._writeStream.finished || this._writeStream.closed)
      return super._read(n);

    // Make sure there's something to read.
    const unread = this._writeStream.bytesWritten - this.bytesRead;
    if (unread === 0) {
      const retry = () => {
        this._writeStream.removeListener("finish", retry);
        this._writeStream.removeListener("write", retry);
        this._read(n);
      };

      this._writeStream.addListener("finish", retry);
      this._writeStream.addListener("write", retry);
      return;
    }

    // Make sure we don't get ahead of our writer.
    return super._read(Math.min(n, unread));
  }

  _destroy(error, callback) {
    if (typeof this.fd !== "number") {
      this.once("open", this._destroy.bind(this, error, callback));
      return;
    }

    fs.close(this.fd, closeError => {
      callback(closeError || error);
      this.fd = null;
      this.closed = true;
      this.emit("close");
    });
  }

  open() {
    if (!this._writeStream) return;
    if (typeof this._writeStream.fd !== "number") {
      this._writeStream.once("open", () => this.open());
      return;
    }

    this.path = this._writeStream.path;
    super.open();
  }
}

export class WriteStream extends fs.WriteStream {
  constructor() {
    super("", {
      autoClose: false
    });

    this._readStreams = new Set();
    this.error = null;

    this._cleanupSync = () => {
      process.removeListener("exit", this._cleanupSync);
      process.removeListener("SIGINT", this._cleanupSync);

      if (typeof this.fd === "number")
        try {
          fs.closeSync(this.fd);
        } catch (error) {
          // An error here probably means the fd was already closed, but we can
          // still try to unlink the file.
        }

      try {
        fs.unlinkSync(this.path);
      } catch (error) {
        // If we are unable to unlink the file, the operating system will clean up
        //  on next restart, since we use store thes in `os.tmpdir()`
      }
    };
  }

  get finished() {
    return this._writableState.finished;
  }

  open() {
    // generage a random tmp path
    crypto.randomBytes(16, (error, buffer) => {
      if (error) {
        this.destroy(error);
        return;
      }

      this.path = path.join(
        os.tmpdir(),
        `capacitor-${buffer.toString("hex")}.tmp`
      );

      // create the file
      fs.open(this.path, "wx", this.mode, (error, fd) => {
        if (error) {
          this.destroy(error);
          return;
        }

        // cleanup when our stream closes or when the process exits
        process.addListener("exit", this._cleanupSync);
        process.addListener("SIGINT", this._cleanupSync);

        this.fd = fd;
        this.emit("open", fd);
        this.emit("ready");
      });
    });
  }

  _write(chunk, encoding, callback) {
    super._write(chunk, encoding, error => {
      if (!error) this.emit("write");
      callback(error);
    });
  }

  _destroy(error, callback) {
    if (typeof this.fd !== "number") {
      this.once("open", this._destroy.bind(this, error, callback));
      return;
    }

    process.removeListener("exit", this._cleanupSync);
    process.removeListener("SIGINT", this._cleanupSync);

    const unlink = error => {
      fs.unlink(this.path, unlinkError => {
        // If we are unable to unlink the file, the operating system will
        // clean up on next restart, since we use store thes in `os.tmpdir()`
        callback(unlinkError || error);
        this.fd = null;
        this.closed = true;
        this.emit("close");
      });
    };

    if (typeof this.fd === "number") {
      fs.close(this.fd, closeError => {
        // An error here probably means the fd was already closed, but we can
        // still try to unlink the file.

        unlink(closeError || error);
      });

      return;
    }

    unlink(error);
  }

  destroy(error, callback) {
    if (error) this.error = error;

    // This is already destroyed.
    if (this.destroyed) return super.destroy(error, callback);

    // Call the callback once destroyed.
    if (typeof callback === "function")
      this.once("close", callback.bind(this, error));

    // All read streams have terminated, so we can destroy this.
    if (this._readStreams.size === 0) {
      super.destroy(error, callback);
      return;
    }

    // Wait until all read streams have terminated before destroying this.
    this._destroyPending = true;

    // If there is an error, destroy all read streams with the error.
    if (error)
      for (let readStream of this._readStreams) readStream.destroy(error);
  }

  createReadStream(name) {
    if (this.destroyed)
      throw new ReadAfterDestroyedError(
        "A ReadStream cannot be created from a destroyed WriteStream."
      );

    const readStream = new ReadStream(this, name);
    this._readStreams.add(readStream);

    const remove = () => {
      this._deleteReadStream(readStream);
      readStream.removeListener("end", remove);
      readStream.removeListener("close", remove);
    };

    readStream.addListener("end", remove);
    readStream.addListener("close", remove);

    return readStream;
  }

  _deleteReadStream(readStream) {
    if (this._readStreams.delete(readStream) && this._destroyPending)
      this.destroy();
  }
}

export default WriteStream;
