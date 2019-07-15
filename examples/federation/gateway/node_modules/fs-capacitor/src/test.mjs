import "leaked-handles";

import fs from "fs";
import stream from "stream";
import t from "tap";
import WriteStream, { ReadAfterDestroyedError } from ".";

const streamToString = stream =>
  new Promise((resolve, reject) => {
    let ended = false;
    let data = "";
    stream
      .on("error", reject)
      .on("data", chunk => {
        if (ended) throw new Error("`data` emitted after `end`");
        data += chunk;
      })
      .on("end", () => {
        ended = true;
        resolve(data);
      });
  });

const waitForBytesWritten = (stream, bytes, resolve) => {
  if (stream.bytesWritten >= bytes) {
    setImmediate(resolve);
    return;
  }

  setImmediate(() => waitForBytesWritten(stream, bytes, resolve));
};

t.test("Data from a complete stream.", async t => {
  let data = "";
  const source = new stream.Readable({
    read() {}
  });

  // Add the first chunk of data (without any consumer)
  const chunk1 = "1".repeat(10);
  source.push(chunk1);
  source.push(null);
  data += chunk1;

  // Create a new capacitor
  let capacitor1 = new WriteStream();
  t.strictSame(
    capacitor1._readStreams.size,
    0,
    "should start with 0 read streams"
  );

  // Pipe data to the capacitor
  source.pipe(capacitor1);

  // Attach a read stream
  const capacitor1Stream1 = capacitor1.createReadStream("capacitor1Stream1");
  t.strictSame(
    capacitor1._readStreams.size,
    1,
    "should attach a new read stream before receiving data"
  );

  // Wait until capacitor is finished writing all data
  const result = await streamToString(capacitor1Stream1);
  t.sameStrict(result, data, "should stream all data");
  t.sameStrict(
    capacitor1._readStreams.size,
    0,
    "should no longer have any attacheds read streams"
  );
});

t.test("Data from an open stream, 1 chunk, no read streams.", async t => {
  let data = "";
  const source = new stream.Readable({
    read() {}
  });

  // Create a new capacitor
  let capacitor1 = new WriteStream();
  t.strictSame(
    capacitor1._readStreams.size,
    0,
    "should start with 0 read streams"
  );

  // Pipe data to the capacitor
  source.pipe(capacitor1);

  // Add the first chunk of data (without any read streams)
  const chunk1 = "1".repeat(10);
  source.push(chunk1);
  source.push(null);
  data += chunk1;

  // Attach a read stream
  const capacitor1Stream1 = capacitor1.createReadStream("capacitor1Stream1");
  t.strictSame(
    capacitor1._readStreams.size,
    1,
    "should attach a new read stream before receiving data"
  );

  // Wait until capacitor is finished writing all data
  const result = await streamToString(capacitor1Stream1);
  t.sameStrict(result, data, "should stream all data");
  t.sameStrict(
    capacitor1._readStreams.size,
    0,
    "should no longer have any attacheds read streams"
  );
});

t.test("Data from an open stream, 1 chunk, 1 read stream.", async t => {
  let data = "";
  const source = new stream.Readable({
    read() {}
  });

  // Create a new capacitor
  let capacitor1 = new WriteStream();
  t.strictSame(
    capacitor1._readStreams.size,
    0,
    "should start with 0 read streams"
  );

  // Pipe data to the capacitor
  source.pipe(capacitor1);

  // Attach a read stream
  const capacitor1Stream1 = capacitor1.createReadStream("capacitor1Stream1");
  t.strictSame(
    capacitor1._readStreams.size,
    1,
    "should attach a new read stream before receiving data"
  );

  // Add the first chunk of data (with 1 read stream)
  const chunk1 = "1".repeat(10);
  source.push(chunk1);
  source.push(null);
  data += chunk1;

  // Wait until capacitor is finished writing all data
  const result = await streamToString(capacitor1Stream1);
  t.sameStrict(result, data, "should stream all data");
  t.sameStrict(
    capacitor1._readStreams.size,
    0,
    "should no longer have any attacheds read streams"
  );
});

const withChunkSize = size =>
  t.test(`--- with chunk size: ${size}`, async t => {
    let data = "";
    const source = new stream.Readable({
      read() {}
    });

    // Create a new capacitor and read stream before any data has been written
    let capacitor1;
    let capacitor1Stream1;
    await t.test(
      "can add a read stream before any data has been written",
      async t => {
        capacitor1 = new WriteStream();
        t.strictSame(
          capacitor1._readStreams.size,
          0,
          "should start with 0 read streams"
        );
        capacitor1Stream1 = capacitor1.createReadStream("capacitor1Stream1");
        t.strictSame(
          capacitor1._readStreams.size,
          1,
          "should attach a new read stream before receiving data"
        );

        await t.test("creates a temporary file", async t => {
          t.plan(3);
          await new Promise(resolve => capacitor1.on("open", resolve));
          t.type(
            capacitor1.path,
            "string",
            "capacitor1.path should be a string"
          );
          t.type(capacitor1.fd, "number", "capacitor1.fd should be a number");
          t.ok(fs.existsSync(capacitor1.path), "creates a temp file");
        });
      }
    );

    // Pipe data to the capacitor
    source.pipe(capacitor1);

    // Add the first chunk of data (without any read streams)
    const chunk1 = "1".repeat(size);
    source.push(chunk1);
    data += chunk1;

    // Wait until this chunk has been written to the buffer
    await new Promise(resolve =>
      waitForBytesWritten(capacitor1, size, resolve)
    );

    // Create a new stream after some data has been written
    let capacitor1Stream2;
    t.test("can add a read stream after data has been written", t => {
      capacitor1Stream2 = capacitor1.createReadStream("capacitor1Stream2");
      t.strictSame(
        capacitor1._readStreams.size,
        2,
        "should attach a new read stream after first write"
      );

      t.end();
    });

    const writeEventBytesWritten = new Promise(resolve => {
      capacitor1.once("write", () => {
        resolve(capacitor1.bytesWritten);
      });
    });

    // Add a second chunk of data
    const chunk2 = "2".repeat(size);
    source.push(chunk2);
    data += chunk2;

    // Wait until this chunk has been written to the buffer
    await new Promise(resolve =>
      waitForBytesWritten(capacitor1, 2 * size, resolve)
    );

    // Make sure write event is called after bytes are written to the filesystem
    await t.test("write event emitted after bytes are written", async t => {
      t.strictSame(
        await writeEventBytesWritten,
        2 * size,
        "bytesWritten should include new chunk"
      );
    });

    // End the source & wait until capacitor is finished
    const finished = new Promise(resolve => capacitor1.once("finish", resolve));
    source.push(null);
    await finished;

    // Create a new stream after the source has ended
    let capacitor1Stream3;
    let capacitor1Stream4;
    t.test("can create a read stream after the source has ended", t => {
      capacitor1Stream3 = capacitor1.createReadStream("capacitor1Stream3");
      capacitor1Stream4 = capacitor1.createReadStream("capacitor1Stream4");
      t.strictSame(
        capacitor1._readStreams.size,
        4,
        "should attach new read streams after end"
      );
      t.end();
    });

    // Consume capacitor1Stream2, capacitor1Stream4
    await t.test("streams complete data to a read stream", async t => {
      const result2 = await streamToString(capacitor1Stream2);
      t.strictSame(
        capacitor1Stream2.ended,
        true,
        "should mark read stream as ended"
      );
      t.strictSame(result2, data, "should stream complete data");

      const result4 = await streamToString(capacitor1Stream4);
      t.strictSame(
        capacitor1Stream4.ended,
        true,
        "should mark read stream as ended"
      );
      t.strictSame(result4, data, "should stream complete data");

      t.strictSame(
        capacitor1._readStreams.size,
        2,
        "should detach an ended read stream"
      );
    });

    // Destroy capacitor1Stream1
    await t.test("can destroy a read stream", async t => {
      await new Promise(resolve => {
        capacitor1Stream1.once("error", resolve);
        capacitor1Stream1.destroy(new Error("test"));
      });
      t.strictSame(
        capacitor1Stream1.destroyed,
        true,
        "should mark read stream as destroyed"
      );
      t.type(
        capacitor1Stream1.error,
        Error,
        "should store an error on read stream"
      );
      t.strictSame(
        capacitor1._readStreams.size,
        1,
        "should detach a destroyed read stream"
      );
    });

    // Destroy the capacitor (without an error)
    t.test("can delay destruction of a capacitor", t => {
      capacitor1.destroy(null);

      t.strictSame(
        capacitor1.destroyed,
        false,
        "should not destroy while read streams exist"
      );
      t.strictSame(
        capacitor1._destroyPending,
        true,
        "should mark for future destruction"
      );
      t.end();
    });

    // Destroy capacitor1Stream2
    await t.test("destroys capacitor once no read streams exist", async t => {
      const readStreamDestroyed = new Promise(resolve =>
        capacitor1Stream3.on("close", resolve)
      );
      const capacitorDestroyed = new Promise(resolve =>
        capacitor1.on("close", resolve)
      );
      capacitor1Stream3.destroy(null);
      await readStreamDestroyed;
      t.strictSame(
        capacitor1Stream3.destroyed,
        true,
        "should mark read stream as destroyed"
      );
      t.strictSame(
        capacitor1Stream3.error,
        null,
        "should not store an error on read stream"
      );
      t.strictSame(
        capacitor1._readStreams.size,
        0,
        "should detach a destroyed read stream"
      );
      await capacitorDestroyed;
      t.strictSame(capacitor1.closed, true, "should mark capacitor as closed");
      t.strictSame(capacitor1.fd, null, "should set fd to null");
      t.strictSame(
        capacitor1.destroyed,
        true,
        "should mark capacitor as destroyed"
      );
      t.notOk(fs.existsSync(capacitor1.path), "removes its temp file");
    });

    // Try to create a new read stream
    t.test("cannot create a read stream after destruction", t => {
      try {
        capacitor1.createReadStream();
      } catch (error) {
        t.ok(
          error instanceof ReadAfterDestroyedError,
          "should not create a read stream once destroyed"
        );
        t.end();
      }
    });

    const capacitor2 = new WriteStream();
    const capacitor2Stream1 = capacitor2.createReadStream("capacitor2Stream1");
    const capacitor2Stream2 = capacitor2.createReadStream("capacitor2Stream2");

    const capacitor2ReadStream1Destroyed = new Promise(resolve =>
      capacitor2Stream1.on("close", resolve)
    );
    const capacitor2Destroyed = new Promise(resolve =>
      capacitor2.on("close", resolve)
    );

    capacitor2Stream1.destroy();
    await capacitor2ReadStream1Destroyed;

    await t.test("propagates errors to attached read streams", async t => {
      capacitor2.destroy();
      await new Promise(resolve => setImmediate(resolve));
      t.strictSame(
        capacitor2Stream2.destroyed,
        false,
        "should not immediately mark attached read streams as destroyed"
      );

      capacitor2.destroy(new Error("test"));
      await capacitor2Destroyed;

      t.type(capacitor2.error, Error, "should store an error on capacitor");
      t.strictSame(
        capacitor2.destroyed,
        true,
        "should mark capacitor as destroyed"
      );
      t.type(
        capacitor2Stream2.error,
        Error,
        "should store an error on attached read streams"
      );
      t.strictSame(
        capacitor2Stream2.destroyed,
        true,
        "should mark attached read streams as destroyed"
      );
      t.strictSame(
        capacitor2Stream1.error,
        null,
        "should not store an error on detached read streams"
      );
    });
  });

// Test with small (sub-highWaterMark, 16384) chunks
withChunkSize(10);

// Test with large (above-highWaterMark, 16384) chunks
withChunkSize(100000);
