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
Object.defineProperty(exports, "__esModule", { value: true });
const apollo_server_env_1 = require("apollo-server-env");
const graphql_1 = require("graphql");
const apollo_engine_reporting_protobuf_1 = require("apollo-engine-reporting-protobuf");
const deepMerge_1 = require("./utilities/deepMerge");
const graphql_2 = require("./utilities/graphql");
function executeQueryPlan(queryPlan, serviceMap, requestContext, operationContext) {
    return __awaiter(this, void 0, void 0, function* () {
        const errors = [];
        const context = {
            queryPlan,
            operationContext,
            serviceMap,
            requestContext,
            errors,
        };
        let data = Object.create(null);
        const captureTraces = !!(requestContext.metrics && requestContext.metrics.captureTraces);
        if (queryPlan.node) {
            const traceNode = yield executeNode(context, queryPlan.node, data, [], captureTraces);
            if (captureTraces) {
                requestContext.metrics.queryPlanTrace = traceNode;
            }
        }
        try {
            ({ data } = yield graphql_1.execute({
                schema: operationContext.schema,
                document: {
                    kind: graphql_1.Kind.DOCUMENT,
                    definitions: [
                        operationContext.operation,
                        ...Object.values(operationContext.fragments),
                    ],
                },
                rootValue: data,
                variableValues: requestContext.request.variables,
                fieldResolver: exports.defaultFieldResolverWithAliasSupport,
            }));
        }
        catch (error) {
            return { errors: [error] };
        }
        return errors.length === 0 ? { data } : { errors, data };
    });
}
exports.executeQueryPlan = executeQueryPlan;
function executeNode(context, node, results, path, captureTraces) {
    return __awaiter(this, void 0, void 0, function* () {
        if (!results) {
            return new apollo_engine_reporting_protobuf_1.Trace.QueryPlanNode();
        }
        switch (node.kind) {
            case 'Sequence': {
                const traceNode = new apollo_engine_reporting_protobuf_1.Trace.QueryPlanNode.SequenceNode();
                for (const childNode of node.nodes) {
                    const childTraceNode = yield executeNode(context, childNode, results, path, captureTraces);
                    traceNode.nodes.push(childTraceNode);
                }
                return new apollo_engine_reporting_protobuf_1.Trace.QueryPlanNode({ sequence: traceNode });
            }
            case 'Parallel': {
                const childTraceNodes = yield Promise.all(node.nodes.map((childNode) => __awaiter(this, void 0, void 0, function* () { return executeNode(context, childNode, results, path, captureTraces); })));
                return new apollo_engine_reporting_protobuf_1.Trace.QueryPlanNode({
                    parallel: new apollo_engine_reporting_protobuf_1.Trace.QueryPlanNode.ParallelNode({
                        nodes: childTraceNodes,
                    }),
                });
            }
            case 'Flatten': {
                return new apollo_engine_reporting_protobuf_1.Trace.QueryPlanNode({
                    flatten: new apollo_engine_reporting_protobuf_1.Trace.QueryPlanNode.FlattenNode({
                        responsePath: node.path.map(id => new apollo_engine_reporting_protobuf_1.Trace.QueryPlanNode.ResponsePathElement(typeof id === 'string' ? { fieldName: id } : { index: id })),
                        node: yield executeNode(context, node.node, flattenResultsAtPath(results, node.path), [...path, ...node.path], captureTraces),
                    }),
                });
            }
            case 'Fetch': {
                const traceNode = new apollo_engine_reporting_protobuf_1.Trace.QueryPlanNode.FetchNode({
                    serviceName: node.serviceName,
                });
                try {
                    yield executeFetch(context, node, results, path, captureTraces ? traceNode : null);
                }
                catch (error) {
                    context.errors.push(error);
                }
                return new apollo_engine_reporting_protobuf_1.Trace.QueryPlanNode({ fetch: traceNode });
            }
        }
    });
}
function executeFetch(context, fetch, results, _path, traceNode) {
    return __awaiter(this, void 0, void 0, function* () {
        const service = context.serviceMap[fetch.serviceName];
        if (!service) {
            throw new Error(`Couldn't find service with name "${fetch.serviceName}"`);
        }
        const operationType = context.operationContext.operation.operation;
        const entities = Array.isArray(results) ? results : [results];
        if (entities.length < 1)
            return;
        let variables = Object.create(null);
        if (fetch.variableUsages) {
            for (const variableName of Object.keys(fetch.variableUsages)) {
                const providedVariables = context.requestContext.request.variables;
                if (providedVariables &&
                    typeof providedVariables[variableName] !== 'undefined') {
                    variables[variableName] = providedVariables[variableName];
                }
            }
        }
        if (!fetch.requires) {
            const dataReceivedFromService = yield sendOperation(context, operationForRootFetch(fetch, operationType), variables);
            for (const entity of entities) {
                deepMerge_1.deepMerge(entity, dataReceivedFromService);
            }
        }
        else {
            const requires = fetch.requires;
            const representations = [];
            const representationToEntity = [];
            entities.forEach((entity, index) => {
                const representation = executeSelectionSet(entity, requires);
                if (representation && representation[graphql_1.TypeNameMetaFieldDef.name]) {
                    representations.push(representation);
                    representationToEntity.push(index);
                }
            });
            if ('representations' in variables) {
                throw new Error(`Variables cannot contain key "representations"`);
            }
            const dataReceivedFromService = yield sendOperation(context, operationForEntitiesFetch(fetch), Object.assign(Object.assign({}, variables), { representations }));
            if (!dataReceivedFromService) {
                return;
            }
            if (!(dataReceivedFromService._entities &&
                Array.isArray(dataReceivedFromService._entities))) {
                throw new Error(`Expected "data._entities" in response to be an array`);
            }
            const receivedEntities = dataReceivedFromService._entities;
            if (receivedEntities.length !== representations.length) {
                throw new Error(`Expected "data._entities" to contain ${representations.length} elements`);
            }
            for (let i = 0; i < entities.length; i++) {
                deepMerge_1.deepMerge(entities[representationToEntity[i]], receivedEntities[i]);
            }
        }
        function sendOperation(context, operation, variables) {
            return __awaiter(this, void 0, void 0, function* () {
                const source = graphql_1.print(operation);
                let http;
                if (traceNode) {
                    http = {
                        headers: new apollo_server_env_1.Headers({ 'apollo-federation-include-trace': 'ftv1' }),
                    };
                    if (context.requestContext.metrics &&
                        context.requestContext.metrics.startHrTime) {
                        traceNode.sentTimeOffset = durationHrTimeToNanos(process.hrtime(context.requestContext.metrics.startHrTime));
                    }
                    traceNode.sentTime = dateToProtoTimestamp(new Date());
                }
                const response = yield service.process({
                    request: {
                        query: source,
                        variables,
                        http,
                    },
                    context: context.requestContext.context,
                });
                if (response.errors) {
                    const errors = response.errors.map(error => downstreamServiceError(error.message, fetch.serviceName, source, variables, error.extensions, error.path));
                    context.errors.push(...errors);
                }
                if (traceNode) {
                    traceNode.receivedTime = dateToProtoTimestamp(new Date());
                    if (response.extensions && response.extensions.ftv1) {
                        const traceBase64 = response.extensions.ftv1;
                        let traceBuffer;
                        let traceParsingFailed = false;
                        try {
                            traceBuffer = Buffer.from(traceBase64, 'base64');
                        }
                        catch (err) {
                            console.error(`error decoding base64 for federated trace from ${fetch.serviceName}: ${err}`);
                            traceParsingFailed = true;
                        }
                        if (traceBuffer) {
                            try {
                                const trace = apollo_engine_reporting_protobuf_1.Trace.decode(traceBuffer);
                                traceNode.trace = trace;
                            }
                            catch (err) {
                                console.error(`error decoding protobuf for federated trace from ${fetch.serviceName}: ${err}`);
                                traceParsingFailed = true;
                            }
                        }
                        traceNode.traceParsingFailed = traceParsingFailed;
                    }
                }
                return response.data;
            });
        }
    });
}
function executeSelectionSet(source, selectionSet) {
    const result = Object.create(null);
    for (const selection of selectionSet.selections) {
        switch (selection.kind) {
            case graphql_1.Kind.FIELD:
                const responseName = graphql_2.getResponseName(selection);
                const selectionSet = selection.selectionSet;
                if (source === null) {
                    result[responseName] = null;
                    break;
                }
                if (typeof source[responseName] === 'undefined') {
                    throw new Error(`Field "${responseName}" was not found in response.`);
                }
                if (Array.isArray(source[responseName])) {
                    result[responseName] = source[responseName].map((value) => selectionSet ? executeSelectionSet(value, selectionSet) : value);
                }
                else if (selectionSet) {
                    result[responseName] = executeSelectionSet(source[responseName], selectionSet);
                }
                else {
                    result[responseName] = source[responseName];
                }
                break;
            case graphql_1.Kind.INLINE_FRAGMENT:
                if (!selection.typeCondition)
                    continue;
                const typename = source && source['__typename'];
                if (!typename)
                    continue;
                if (typename === selection.typeCondition.name.value) {
                    deepMerge_1.deepMerge(result, executeSelectionSet(source, selection.selectionSet));
                }
                break;
        }
    }
    return result;
}
function flattenResultsAtPath(value, path) {
    if (path.length === 0)
        return value;
    if (value === undefined || value === null)
        return value;
    const [current, ...rest] = path;
    if (current === '@') {
        return value.flatMap((element) => flattenResultsAtPath(element, rest));
    }
    else {
        return flattenResultsAtPath(value[current], rest);
    }
}
function downstreamServiceError(message, serviceName, query, variables, extensions, path) {
    if (!message) {
        message = `Error while fetching subquery from service "${serviceName}"`;
    }
    extensions = Object.assign({ code: 'DOWNSTREAM_SERVICE_ERROR', serviceName,
        query,
        variables }, extensions);
    return new graphql_1.GraphQLError(message, undefined, undefined, undefined, path, undefined, extensions);
}
function mapFetchNodeToVariableDefinitions(node) {
    return node.variableUsages ? Object.values(node.variableUsages) : [];
}
function operationForRootFetch(fetch, operation = 'query') {
    return {
        kind: graphql_1.Kind.OPERATION_DEFINITION,
        operation,
        selectionSet: fetch.selectionSet,
        variableDefinitions: mapFetchNodeToVariableDefinitions(fetch),
    };
}
function operationForEntitiesFetch(fetch) {
    const representationsVariable = {
        kind: graphql_1.Kind.VARIABLE,
        name: { kind: graphql_1.Kind.NAME, value: 'representations' },
    };
    return {
        kind: graphql_1.Kind.OPERATION_DEFINITION,
        operation: 'query',
        variableDefinitions: [
            {
                kind: graphql_1.Kind.VARIABLE_DEFINITION,
                variable: representationsVariable,
                type: {
                    kind: graphql_1.Kind.NON_NULL_TYPE,
                    type: {
                        kind: graphql_1.Kind.LIST_TYPE,
                        type: {
                            kind: graphql_1.Kind.NON_NULL_TYPE,
                            type: {
                                kind: graphql_1.Kind.NAMED_TYPE,
                                name: { kind: graphql_1.Kind.NAME, value: '_Any' },
                            },
                        },
                    },
                },
            },
        ].concat(mapFetchNodeToVariableDefinitions(fetch)),
        selectionSet: {
            kind: graphql_1.Kind.SELECTION_SET,
            selections: [
                {
                    kind: graphql_1.Kind.FIELD,
                    name: { kind: graphql_1.Kind.NAME, value: '_entities' },
                    arguments: [
                        {
                            kind: graphql_1.Kind.ARGUMENT,
                            name: {
                                kind: graphql_1.Kind.NAME,
                                value: representationsVariable.name.value,
                            },
                            value: representationsVariable,
                        },
                    ],
                    selectionSet: fetch.selectionSet,
                },
            ],
        },
    };
}
exports.defaultFieldResolverWithAliasSupport = function (source, args, contextValue, info) {
    if (typeof source === 'object' || typeof source === 'function') {
        const property = source[info.path.key];
        if (typeof property === 'function') {
            return source[info.fieldName](args, contextValue, info);
        }
        return property;
    }
};
function durationHrTimeToNanos(hrtime) {
    return hrtime[0] * 1e9 + hrtime[1];
}
function dateToProtoTimestamp(date) {
    const totalMillis = +date;
    const millis = totalMillis % 1000;
    return new apollo_engine_reporting_protobuf_1.google.protobuf.Timestamp({
        seconds: (totalMillis - millis) / 1000,
        nanos: millis * 1e6,
    });
}
//# sourceMappingURL=executeQueryPlan.js.map