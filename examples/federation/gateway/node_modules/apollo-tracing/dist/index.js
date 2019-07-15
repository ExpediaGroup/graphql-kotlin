"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const graphql_1 = require("graphql");
class TracingExtension {
    constructor() {
        this.resolverCalls = [];
    }
    requestDidStart() {
        this.startWallTime = new Date();
        this.startHrTime = process.hrtime();
    }
    executionDidStart() {
        return () => {
            this.duration = process.hrtime(this.startHrTime);
            this.endWallTime = new Date();
        };
    }
    willResolveField(_source, _args, _context, info) {
        const resolverCall = {
            path: info.path,
            fieldName: info.fieldName,
            parentType: info.parentType,
            returnType: info.returnType,
            startOffset: process.hrtime(this.startHrTime),
        };
        this.resolverCalls.push(resolverCall);
        return () => {
            resolverCall.endOffset = process.hrtime(this.startHrTime);
        };
    }
    format() {
        if (typeof this.startWallTime === 'undefined' ||
            typeof this.endWallTime === 'undefined' ||
            typeof this.duration === 'undefined') {
            return;
        }
        return [
            'tracing',
            {
                version: 1,
                startTime: this.startWallTime.toISOString(),
                endTime: this.endWallTime.toISOString(),
                duration: durationHrTimeToNanos(this.duration),
                execution: {
                    resolvers: this.resolverCalls.map(resolverCall => {
                        const startOffset = durationHrTimeToNanos(resolverCall.startOffset);
                        const duration = resolverCall.endOffset
                            ? durationHrTimeToNanos(resolverCall.endOffset) - startOffset
                            : 0;
                        return {
                            path: [...graphql_1.responsePathAsArray(resolverCall.path)],
                            parentType: resolverCall.parentType.toString(),
                            fieldName: resolverCall.fieldName,
                            returnType: resolverCall.returnType.toString(),
                            startOffset,
                            duration,
                        };
                    }),
                },
            },
        ];
    }
}
exports.TracingExtension = TracingExtension;
function durationHrTimeToNanos(hrtime) {
    return hrtime[0] * 1e9 + hrtime[1];
}
//# sourceMappingURL=index.js.map