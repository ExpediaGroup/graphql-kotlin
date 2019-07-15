"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const os_1 = __importDefault(require("os"));
const zlib_1 = require("zlib");
const apollo_engine_reporting_protobuf_1 = require("apollo-engine-reporting-protobuf");
const apollo_server_env_1 = require("apollo-server-env");
const async_retry_1 = __importDefault(require("async-retry"));
const extension_1 = require("./extension");
const apollo_server_caching_1 = require("apollo-server-caching");
const apollo_graphql_1 = require("apollo-graphql");
const serviceHeaderDefaults = {
    hostname: os_1.default.hostname(),
    agentVersion: `apollo-engine-reporting@${require('../package.json').version}`,
    runtimeVersion: `node ${process.version}`,
    uname: `${os_1.default.platform()}, ${os_1.default.type()}, ${os_1.default.release()}, ${os_1.default.arch()})`,
};
class EngineReportingAgent {
    constructor(options = {}) {
        this.reports = Object.create(null);
        this.reportSizes = Object.create(null);
        this.stopped = false;
        this.reportHeaders = Object.create(null);
        this.signalHandlers = new Map();
        this.options = options;
        this.apiKey = options.apiKey || process.env.ENGINE_API_KEY || '';
        if (!this.apiKey) {
            throw new Error('To use EngineReportingAgent, you must specify an API key via the apiKey option or the ENGINE_API_KEY environment variable.');
        }
        this.signatureCache = createSignatureCache();
        this.sendReportsImmediately = options.sendReportsImmediately;
        if (!this.sendReportsImmediately) {
            this.reportTimer = setInterval(() => this.sendAllReportsAndReportErrors(), this.options.reportIntervalMs || 10 * 1000);
        }
        if (this.options.handleSignals !== false) {
            const signals = ['SIGINT', 'SIGTERM'];
            signals.forEach(signal => {
                const handler = () => __awaiter(this, void 0, void 0, function* () {
                    this.stop();
                    yield this.sendAllReportsAndReportErrors();
                    process.kill(process.pid, signal);
                });
                process.once(signal, handler);
                this.signalHandlers.set(signal, handler);
            });
        }
        handleLegacyOptions(this.options);
    }
    newExtension(schemaHash) {
        return new extension_1.EngineReportingExtension(this.options, this.addTrace.bind(this), schemaHash);
    }
    addTrace({ trace, queryHash, documentAST, operationName, queryString, schemaHash, }) {
        return __awaiter(this, void 0, void 0, function* () {
            if (this.stopped) {
                return;
            }
            if (!(schemaHash in this.reports)) {
                this.reportHeaders[schemaHash] = new apollo_engine_reporting_protobuf_1.ReportHeader(Object.assign(Object.assign({}, serviceHeaderDefaults), { schemaHash, schemaTag: this.options.schemaTag || process.env.ENGINE_SCHEMA_TAG || '' }));
                this.resetReport(schemaHash);
            }
            const report = this.reports[schemaHash];
            const protobufError = apollo_engine_reporting_protobuf_1.Trace.verify(trace);
            if (protobufError) {
                throw new Error(`Error encoding trace: ${protobufError}`);
            }
            const encodedTrace = apollo_engine_reporting_protobuf_1.Trace.encode(trace).finish();
            const signature = yield this.getTraceSignature({
                queryHash,
                documentAST,
                queryString,
                operationName,
            });
            const statsReportKey = `# ${operationName || '-'}\n${signature}`;
            if (!report.tracesPerQuery.hasOwnProperty(statsReportKey)) {
                report.tracesPerQuery[statsReportKey] = new apollo_engine_reporting_protobuf_1.Traces();
                report.tracesPerQuery[statsReportKey].encodedTraces = [];
            }
            report.tracesPerQuery[statsReportKey].encodedTraces.push(encodedTrace);
            this.reportSizes[schemaHash] +=
                encodedTrace.length + Buffer.byteLength(statsReportKey);
            if (this.sendReportsImmediately ||
                this.reportSizes[schemaHash] >=
                    (this.options.maxUncompressedReportSize || 4 * 1024 * 1024)) {
                yield this.sendReportAndReportErrors(schemaHash);
            }
        });
    }
    sendAllReports() {
        return __awaiter(this, void 0, void 0, function* () {
            yield Promise.all(Object.keys(this.reports).map(hash => this.sendReport(hash)));
        });
    }
    sendReport(schemaHash) {
        return __awaiter(this, void 0, void 0, function* () {
            const report = this.reports[schemaHash];
            this.resetReport(schemaHash);
            if (Object.keys(report.tracesPerQuery).length === 0) {
                return;
            }
            yield Promise.resolve();
            if (this.options.debugPrintReports) {
                console.log(`Engine sending report: ${JSON.stringify(report.toJSON())}`);
            }
            const protobufError = apollo_engine_reporting_protobuf_1.FullTracesReport.verify(report);
            if (protobufError) {
                throw new Error(`Error encoding report: ${protobufError}`);
            }
            const message = apollo_engine_reporting_protobuf_1.FullTracesReport.encode(report).finish();
            const compressed = yield new Promise((resolve, reject) => {
                const messageBuffer = Buffer.from(message.buffer, message.byteOffset, message.byteLength);
                zlib_1.gzip(messageBuffer, (err, gzipResult) => {
                    if (err) {
                        reject(err);
                    }
                    else {
                        resolve(gzipResult);
                    }
                });
            });
            const endpointUrl = (this.options.endpointUrl || 'https://engine-report.apollodata.com') +
                '/api/ingress/traces';
            const response = yield async_retry_1.default(() => __awaiter(this, void 0, void 0, function* () {
                const curResponse = yield apollo_server_env_1.fetch(endpointUrl, {
                    method: 'POST',
                    headers: {
                        'user-agent': 'apollo-engine-reporting',
                        'x-api-key': this.apiKey,
                        'content-encoding': 'gzip',
                    },
                    body: compressed,
                    agent: this.options.requestAgent,
                });
                if (curResponse.status >= 500 && curResponse.status < 600) {
                    throw new Error(`HTTP status ${curResponse.status}, ${(yield curResponse.text()) ||
                        '(no body)'}`);
                }
                else {
                    return curResponse;
                }
            }), {
                retries: (this.options.maxAttempts || 5) - 1,
                minTimeout: this.options.minimumRetryDelayMs || 100,
                factor: 2,
            }).catch((err) => {
                throw new Error(`Error sending report to Apollo Engine servers: ${err.message}`);
            });
            if (response.status < 200 || response.status >= 300) {
                throw new Error(`Error sending report to Apollo Engine servers: HTTP status ${response.status}, ${(yield response.text()) || '(no body)'}`);
            }
            if (this.options.debugPrintReports) {
                console.log(`Engine report: status ${response.status}`);
            }
        });
    }
    stop() {
        this.signalHandlers.forEach((handler, signal) => {
            process.removeListener(signal, handler);
        });
        if (this.reportTimer) {
            clearInterval(this.reportTimer);
            this.reportTimer = undefined;
        }
        this.stopped = true;
    }
    getTraceSignature({ queryHash, operationName, documentAST, queryString, }) {
        return __awaiter(this, void 0, void 0, function* () {
            if (!documentAST && !queryString) {
                throw new Error('No queryString or parsedQuery?');
            }
            const cacheKey = signatureCacheKey(queryHash, operationName);
            const cachedSignature = yield this.signatureCache.get(cacheKey);
            if (cachedSignature) {
                return cachedSignature;
            }
            if (!documentAST) {
                return queryString;
            }
            const generatedSignature = (this.options.calculateSignature ||
                apollo_graphql_1.defaultEngineReportingSignature)(documentAST, operationName);
            this.signatureCache.set(cacheKey, generatedSignature);
            return generatedSignature;
        });
    }
    sendAllReportsAndReportErrors() {
        return __awaiter(this, void 0, void 0, function* () {
            yield Promise.all(Object.keys(this.reports).map(schemaHash => this.sendReportAndReportErrors(schemaHash)));
        });
    }
    sendReportAndReportErrors(schemaHash) {
        return this.sendReport(schemaHash).catch(err => {
            if (this.options.reportErrorFunction) {
                this.options.reportErrorFunction(err);
            }
            else {
                console.error(err.message);
            }
        });
    }
    resetReport(schemaHash) {
        this.reports[schemaHash] = new apollo_engine_reporting_protobuf_1.FullTracesReport({
            header: this.reportHeaders[schemaHash],
        });
        this.reportSizes[schemaHash] = 0;
    }
}
exports.EngineReportingAgent = EngineReportingAgent;
function createSignatureCache() {
    let lastSignatureCacheWarn;
    let lastSignatureCacheDisposals = 0;
    return new apollo_server_caching_1.InMemoryLRUCache({
        sizeCalculator(obj) {
            return Buffer.byteLength(JSON.stringify(obj), 'utf8');
        },
        maxSize: Math.pow(2, 20) * 3,
        onDispose() {
            lastSignatureCacheDisposals++;
            if (!lastSignatureCacheWarn ||
                new Date().getTime() - lastSignatureCacheWarn.getTime() > 60000) {
                lastSignatureCacheWarn = new Date();
                console.warn([
                    'This server is processing a high number of unique operations.  ',
                    `A total of ${lastSignatureCacheDisposals} records have been `,
                    'ejected from the Engine Reporting signature cache in the past ',
                    'interval.  If you see this warning frequently, please open an ',
                    'issue on the Apollo Server repository.',
                ].join(''));
                lastSignatureCacheDisposals = 0;
            }
        },
    });
}
function signatureCacheKey(queryHash, operationName) {
    return `${queryHash}${operationName && ':' + operationName}`;
}
exports.signatureCacheKey = signatureCacheKey;
function handleLegacyOptions(options) {
    if (typeof options.privateVariables !== 'undefined' &&
        options.sendVariableValues) {
        throw new Error("You have set both the 'sendVariableValues' and the deprecated 'privateVariables' options. Please only set 'sendVariableValues'.");
    }
    else if (typeof options.privateVariables !== 'undefined') {
        if (options.privateVariables !== null) {
            options.sendVariableValues = makeSendValuesBaseOptionsFromLegacy(options.privateVariables);
        }
        delete options.privateVariables;
    }
    if (typeof options.privateHeaders !== 'undefined' && options.sendHeaders) {
        throw new Error("You have set both the 'sendHeaders' and the deprecated 'privateHeaders' options. Please only set 'sendHeaders'.");
    }
    else if (typeof options.privateHeaders !== 'undefined') {
        if (options.privateHeaders !== null) {
            options.sendHeaders = makeSendValuesBaseOptionsFromLegacy(options.privateHeaders);
        }
        delete options.privateHeaders;
    }
}
exports.handleLegacyOptions = handleLegacyOptions;
function makeSendValuesBaseOptionsFromLegacy(legacyPrivateOption) {
    return Array.isArray(legacyPrivateOption)
        ? {
            exceptNames: legacyPrivateOption,
        }
        : legacyPrivateOption
            ? { none: true }
            : { all: true };
}
//# sourceMappingURL=agent.js.map