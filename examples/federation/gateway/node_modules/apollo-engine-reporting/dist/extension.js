"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const apollo_engine_reporting_protobuf_1 = require("apollo-engine-reporting-protobuf");
const treeBuilder_1 = require("./treeBuilder");
const clientNameHeaderKey = 'apollographql-client-name';
const clientReferenceIdHeaderKey = 'apollographql-client-reference-id';
const clientVersionHeaderKey = 'apollographql-client-version';
class EngineReportingExtension {
    constructor(options, addTrace, schemaHash) {
        this.schemaHash = schemaHash;
        this.options = Object.assign({}, options);
        this.addTrace = addTrace;
        this.generateClientInfo =
            options.generateClientInfo || defaultGenerateClientInfo;
        this.treeBuilder = new treeBuilder_1.EngineReportingTreeBuilder({
            rewriteError: options.rewriteError,
        });
    }
    requestDidStart(o) {
        this.treeBuilder.startTiming();
        o.requestContext.metrics.startHrTime = this.treeBuilder.startHrTime;
        const queryHash = o.requestContext.queryHash;
        this.queryString = o.queryString;
        this.documentAST = o.parsedQuery;
        this.treeBuilder.trace.http = new apollo_engine_reporting_protobuf_1.Trace.HTTP({
            method: apollo_engine_reporting_protobuf_1.Trace.HTTP.Method[o.request.method] ||
                apollo_engine_reporting_protobuf_1.Trace.HTTP.Method.UNKNOWN,
            host: null,
            path: null,
        });
        if (this.options.sendHeaders) {
            makeHTTPRequestHeaders(this.treeBuilder.trace.http, o.request.headers, this.options.sendHeaders);
            if (o.requestContext.metrics.persistedQueryHit) {
                this.treeBuilder.trace.persistedQueryHit = true;
            }
            if (o.requestContext.metrics.persistedQueryRegister) {
                this.treeBuilder.trace.persistedQueryRegister = true;
            }
        }
        if (o.variables) {
            this.treeBuilder.trace.details = makeTraceDetails(o.variables, this.options.sendVariableValues, o.queryString);
        }
        const clientInfo = this.generateClientInfo(o.requestContext);
        if (clientInfo) {
            const { clientName, clientVersion, clientReferenceId } = clientInfo;
            this.treeBuilder.trace.clientVersion = clientVersion || '';
            this.treeBuilder.trace.clientReferenceId = clientReferenceId || '';
            this.treeBuilder.trace.clientName = clientName || '';
        }
        return () => {
            this.treeBuilder.stopTiming();
            this.treeBuilder.trace.fullQueryCacheHit = !!o.requestContext.metrics
                .responseCacheHit;
            this.treeBuilder.trace.forbiddenOperation = !!o.requestContext.metrics
                .forbiddenOperation;
            this.treeBuilder.trace.registeredOperation = !!o.requestContext.metrics
                .registeredOperation;
            const operationName = this.explicitOperationName || o.requestContext.operationName || '';
            const documentAST = this.documentAST || o.requestContext.document;
            if (o.requestContext.metrics.queryPlanTrace) {
                this.treeBuilder.trace.queryPlan =
                    o.requestContext.metrics.queryPlanTrace;
            }
            this.addTrace({
                operationName,
                queryHash,
                documentAST,
                queryString: this.queryString || '',
                trace: this.treeBuilder.trace,
                schemaHash: this.schemaHash,
            });
        };
    }
    executionDidStart(o) {
        if (o.executionArgs.operationName) {
            this.explicitOperationName = o.executionArgs.operationName;
        }
        this.documentAST = o.executionArgs.document;
    }
    willResolveField(_source, _args, _context, info) {
        return this.treeBuilder.willResolveField(info);
    }
    didEncounterErrors(errors) {
        this.treeBuilder.didEncounterErrors(errors);
    }
}
exports.EngineReportingExtension = EngineReportingExtension;
function defaultGenerateClientInfo({ request }) {
    if (request.http &&
        request.http.headers &&
        (request.http.headers.get(clientNameHeaderKey) ||
            request.http.headers.get(clientVersionHeaderKey) ||
            request.http.headers.get(clientReferenceIdHeaderKey))) {
        return {
            clientName: request.http.headers.get(clientNameHeaderKey),
            clientVersion: request.http.headers.get(clientVersionHeaderKey),
            clientReferenceId: request.http.headers.get(clientReferenceIdHeaderKey),
        };
    }
    else if (request.extensions && request.extensions.clientInfo) {
        return request.extensions.clientInfo;
    }
    else {
        return {};
    }
}
function makeTraceDetails(variables, sendVariableValues, operationString) {
    const details = new apollo_engine_reporting_protobuf_1.Trace.Details();
    const variablesToRecord = (() => {
        if (sendVariableValues && 'transform' in sendVariableValues) {
            const originalKeys = Object.keys(variables);
            try {
                const modifiedVariables = sendVariableValues.transform({
                    variables: variables,
                    operationString: operationString,
                });
                return cleanModifiedVariables(originalKeys, modifiedVariables);
            }
            catch (e) {
                return handleVariableValueTransformError(originalKeys);
            }
        }
        else {
            return variables;
        }
    })();
    Object.keys(variablesToRecord).forEach(name => {
        if (!sendVariableValues ||
            ('none' in sendVariableValues && sendVariableValues.none) ||
            ('all' in sendVariableValues && !sendVariableValues.all) ||
            ('exceptNames' in sendVariableValues &&
                sendVariableValues.exceptNames.includes(name)) ||
            ('onlyNames' in sendVariableValues &&
                !sendVariableValues.onlyNames.includes(name))) {
            details.variablesJson[name] = '';
        }
        else {
            try {
                details.variablesJson[name] =
                    typeof variablesToRecord[name] === 'undefined'
                        ? ''
                        : JSON.stringify(variablesToRecord[name]);
            }
            catch (e) {
                details.variablesJson[name] = JSON.stringify('[Unable to convert value to JSON]');
            }
        }
    });
    return details;
}
exports.makeTraceDetails = makeTraceDetails;
function handleVariableValueTransformError(variableNames) {
    const modifiedVariables = Object.create(null);
    variableNames.forEach(name => {
        modifiedVariables[name] = '[PREDICATE_FUNCTION_ERROR]';
    });
    return modifiedVariables;
}
function cleanModifiedVariables(originalKeys, modifiedVariables) {
    const cleanedVariables = Object.create(null);
    originalKeys.forEach(name => {
        cleanedVariables[name] = modifiedVariables[name];
    });
    return cleanedVariables;
}
function makeHTTPRequestHeaders(http, headers, sendHeaders) {
    if (!sendHeaders ||
        ('none' in sendHeaders && sendHeaders.none) ||
        ('all' in sendHeaders && !sendHeaders.all)) {
        return;
    }
    for (const [key, value] of headers) {
        const lowerCaseKey = key.toLowerCase();
        if (('exceptNames' in sendHeaders &&
            sendHeaders.exceptNames.some(exceptHeader => {
                return exceptHeader.toLowerCase() === lowerCaseKey;
            })) ||
            ('onlyNames' in sendHeaders &&
                !sendHeaders.onlyNames.some(header => {
                    return header.toLowerCase() === lowerCaseKey;
                }))) {
            continue;
        }
        switch (key) {
            case 'authorization':
            case 'cookie':
            case 'set-cookie':
                break;
            default:
                http.requestHeaders[key] = new apollo_engine_reporting_protobuf_1.Trace.HTTP.Values({
                    value: [value],
                });
        }
    }
}
exports.makeHTTPRequestHeaders = makeHTTPRequestHeaders;
//# sourceMappingURL=extension.js.map