"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const apollo_engine_reporting_protobuf_1 = require("apollo-engine-reporting-protobuf");
const treeBuilder_1 = require("./treeBuilder");
class EngineFederatedTracingExtension {
    constructor(options) {
        this.enabled = false;
        this.done = false;
        this.treeBuilder = new treeBuilder_1.EngineReportingTreeBuilder({
            rewriteError: options.rewriteError,
        });
    }
    requestDidStart(o) {
        const http = o.requestContext.request.http;
        if (http &&
            http.headers.get('apollo-federation-include-trace') === 'ftv1') {
            this.enabled = true;
        }
        if (this.enabled) {
            this.treeBuilder.startTiming();
        }
    }
    willResolveField(_source, _args, _context, info) {
        if (this.enabled) {
            return this.treeBuilder.willResolveField(info);
        }
    }
    didEncounterErrors(errors) {
        if (this.enabled) {
            this.treeBuilder.didEncounterErrors(errors);
        }
    }
    format() {
        if (!this.enabled) {
            return;
        }
        if (this.done) {
            throw Error('format called twice?');
        }
        this.treeBuilder.stopTiming();
        this.done = true;
        const encodedUint8Array = apollo_engine_reporting_protobuf_1.Trace.encode(this.treeBuilder.trace).finish();
        const encodedBuffer = Buffer.from(encodedUint8Array, encodedUint8Array.byteOffset, encodedUint8Array.byteLength);
        return ['ftv1', encodedBuffer.toString('base64')];
    }
}
exports.EngineFederatedTracingExtension = EngineFederatedTracingExtension;
//# sourceMappingURL=federatedExtension.js.map