var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var makeExecutableSchema_1 = require("../makeExecutableSchema");
var schemaRecreation_1 = require("./schemaRecreation");
var delegateToSchema_1 = require("./delegateToSchema");
var typeFromAST_1 = require("./typeFromAST");
var transforms_1 = require("../transforms");
var mergeDeep_1 = require("../mergeDeep");
var schemaVisitor_1 = require("../schemaVisitor");
function mergeSchemas(_a) {
    var schemas = _a.schemas, onTypeConflict = _a.onTypeConflict, resolvers = _a.resolvers, schemaDirectives = _a.schemaDirectives, inheritResolversFromInterfaces = _a.inheritResolversFromInterfaces, mergeDirectives = _a.mergeDirectives;
    return mergeSchemasImplementation({
        schemas: schemas,
        resolvers: resolvers,
        schemaDirectives: schemaDirectives,
        inheritResolversFromInterfaces: inheritResolversFromInterfaces,
        mergeDirectives: mergeDirectives,
    });
}
exports.default = mergeSchemas;
function mergeSchemasImplementation(_a) {
    var schemas = _a.schemas, resolvers = _a.resolvers, schemaDirectives = _a.schemaDirectives, inheritResolversFromInterfaces = _a.inheritResolversFromInterfaces, mergeDirectives = _a.mergeDirectives;
    var allSchemas = [];
    var typeCandidates = {};
    var types = {};
    var extensions = [];
    var directives = [];
    var fragments = [];
    var resolveType = schemaRecreation_1.createResolveType(function (name) {
        if (types[name] === undefined) {
            throw new Error("Can't find type " + name + ".");
        }
        return types[name];
    });
    schemas.forEach(function (schema) {
        if (schema instanceof graphql_1.GraphQLSchema) {
            allSchemas.push(schema);
            var queryType_1 = schema.getQueryType();
            var mutationType_1 = schema.getMutationType();
            var subscriptionType_1 = schema.getSubscriptionType();
            if (queryType_1) {
                addTypeCandidate(typeCandidates, 'Query', {
                    schema: schema,
                    type: queryType_1,
                });
            }
            if (mutationType_1) {
                addTypeCandidate(typeCandidates, 'Mutation', {
                    schema: schema,
                    type: mutationType_1,
                });
            }
            if (subscriptionType_1) {
                addTypeCandidate(typeCandidates, 'Subscription', {
                    schema: schema,
                    type: subscriptionType_1,
                });
            }
            if (mergeDirectives) {
                var directiveInstances = schema.getDirectives();
                directiveInstances.forEach(function (directive) {
                    directives.push(directive);
                });
            }
            var typeMap_1 = schema.getTypeMap();
            Object.keys(typeMap_1).forEach(function (typeName) {
                var type = typeMap_1[typeName];
                if (graphql_1.isNamedType(type) &&
                    graphql_1.getNamedType(type).name.slice(0, 2) !== '__' &&
                    type !== queryType_1 &&
                    type !== mutationType_1 &&
                    type !== subscriptionType_1) {
                    addTypeCandidate(typeCandidates, type.name, {
                        schema: schema,
                        type: type,
                    });
                }
            });
        }
        else if (typeof schema === 'string' ||
            (schema && schema.kind === graphql_1.Kind.DOCUMENT)) {
            var parsedSchemaDocument = typeof schema === 'string' ? graphql_1.parse(schema) : schema;
            parsedSchemaDocument.definitions.forEach(function (def) {
                var type = typeFromAST_1.default(def);
                if (type instanceof graphql_1.GraphQLDirective && mergeDirectives) {
                    directives.push(type);
                }
                else if (type && !(type instanceof graphql_1.GraphQLDirective)) {
                    addTypeCandidate(typeCandidates, type.name, {
                        type: type,
                    });
                }
            });
            var extensionsDocument = makeExecutableSchema_1.extractExtensionDefinitions(parsedSchemaDocument);
            if (extensionsDocument.definitions.length > 0) {
                extensions.push(extensionsDocument);
            }
        }
        else if (Array.isArray(schema)) {
            schema.forEach(function (type) {
                addTypeCandidate(typeCandidates, type.name, {
                    type: type,
                });
            });
        }
        else {
            throw new Error("Invalid schema passed");
        }
    });
    var mergeInfo = createMergeInfo(allSchemas, fragments);
    if (!resolvers) {
        resolvers = {};
    }
    else if (typeof resolvers === 'function') {
        console.warn('Passing functions as resolver parameter is deprecated. Use `info.mergeInfo` instead.');
        resolvers = resolvers(mergeInfo);
    }
    else if (Array.isArray(resolvers)) {
        resolvers = resolvers.reduce(function (left, right) {
            if (typeof right === 'function') {
                console.warn('Passing functions as resolver parameter is deprecated. Use `info.mergeInfo` instead.');
                right = right(mergeInfo);
            }
            return mergeDeep_1.default(left, right);
        }, {});
    }
    var generatedResolvers = {};
    Object.keys(typeCandidates).forEach(function (typeName) {
        var resultType = defaultVisitType(typeName, typeCandidates[typeName]);
        if (resultType === null) {
            types[typeName] = null;
        }
        else {
            var type = void 0;
            var typeResolvers = void 0;
            if (graphql_1.isNamedType(resultType)) {
                type = resultType;
            }
            else if (resultType.type) {
                type = resultType.type;
                typeResolvers = resultType.resolvers;
            }
            else {
                throw new Error("Invalid visitType result for type " + typeName);
            }
            types[typeName] = schemaRecreation_1.recreateType(type, resolveType, false);
            if (typeResolvers) {
                generatedResolvers[typeName] = typeResolvers;
            }
        }
    });
    var mergedSchema = new graphql_1.GraphQLSchema({
        query: types.Query,
        mutation: types.Mutation,
        subscription: types.Subscription,
        types: Object.keys(types).map(function (key) { return types[key]; }),
        directives: directives.map(function (directive) { return schemaRecreation_1.recreateDirective(directive, resolveType); })
    });
    extensions.forEach(function (extension) {
        mergedSchema = graphql_1.extendSchema(mergedSchema, extension, {
            commentDescriptions: true,
        });
    });
    if (!resolvers) {
        resolvers = {};
    }
    else if (Array.isArray(resolvers)) {
        resolvers = resolvers.reduce(mergeDeep_1.default, {});
    }
    Object.keys(resolvers).forEach(function (typeName) {
        var type = resolvers[typeName];
        if (type instanceof graphql_1.GraphQLScalarType) {
            return;
        }
        Object.keys(type).forEach(function (fieldName) {
            var field = type[fieldName];
            if (field.fragment) {
                fragments.push({
                    field: fieldName,
                    fragment: field.fragment,
                });
            }
        });
    });
    mergedSchema = makeExecutableSchema_1.addResolveFunctionsToSchema({
        schema: mergedSchema,
        resolvers: mergeDeep_1.default(generatedResolvers, resolvers),
        inheritResolversFromInterfaces: inheritResolversFromInterfaces
    });
    forEachField(mergedSchema, function (field) {
        if (field.resolve) {
            var fieldResolver_1 = field.resolve;
            field.resolve = function (parent, args, context, info) {
                var newInfo = __assign({}, info, { mergeInfo: mergeInfo });
                return fieldResolver_1(parent, args, context, newInfo);
            };
        }
        if (field.subscribe) {
            var fieldResolver_2 = field.subscribe;
            field.subscribe = function (parent, args, context, info) {
                var newInfo = __assign({}, info, { mergeInfo: mergeInfo });
                return fieldResolver_2(parent, args, context, newInfo);
            };
        }
    });
    if (schemaDirectives) {
        schemaVisitor_1.SchemaDirectiveVisitor.visitSchemaDirectives(mergedSchema, schemaDirectives);
    }
    return mergedSchema;
}
function createMergeInfo(allSchemas, fragments) {
    return {
        delegate: function (operation, fieldName, args, context, info, transforms) {
            console.warn('`mergeInfo.delegate` is deprecated. ' +
                'Use `mergeInfo.delegateToSchema and pass explicit schema instances.');
            var schema = guessSchemaByRootField(allSchemas, operation, fieldName);
            var expandTransforms = new transforms_1.ExpandAbstractTypes(info.schema, schema);
            var fragmentTransform = new transforms_1.ReplaceFieldWithFragment(schema, fragments);
            return delegateToSchema_1.default({
                schema: schema,
                operation: operation,
                fieldName: fieldName,
                args: args,
                context: context,
                info: info,
                transforms: (transforms || []).concat([
                    expandTransforms,
                    fragmentTransform,
                ]),
            });
        },
        delegateToSchema: function (options) {
            return delegateToSchema_1.default(__assign({}, options, { transforms: options.transforms }));
        },
        fragments: fragments
    };
}
function guessSchemaByRootField(schemas, operation, fieldName) {
    for (var _i = 0, schemas_1 = schemas; _i < schemas_1.length; _i++) {
        var schema = schemas_1[_i];
        var rootObject = void 0;
        if (operation === 'subscription') {
            rootObject = schema.getSubscriptionType();
        }
        else if (operation === 'mutation') {
            rootObject = schema.getMutationType();
        }
        else {
            rootObject = schema.getQueryType();
        }
        if (rootObject) {
            var fields = rootObject.getFields();
            if (fields[fieldName]) {
                return schema;
            }
        }
    }
    throw new Error("Could not find subschema with field `" + operation + "." + fieldName + "`");
}
function createDelegatingResolver(schema, operation, fieldName) {
    return function (root, args, context, info) {
        return info.mergeInfo.delegateToSchema({
            schema: schema,
            operation: operation,
            fieldName: fieldName,
            args: args,
            context: context,
            info: info,
        });
    };
}
function forEachField(schema, fn) {
    var typeMap = schema.getTypeMap();
    Object.keys(typeMap).forEach(function (typeName) {
        var type = typeMap[typeName];
        if (!graphql_1.getNamedType(type).name.startsWith('__') &&
            type instanceof graphql_1.GraphQLObjectType) {
            var fields_1 = type.getFields();
            Object.keys(fields_1).forEach(function (fieldName) {
                var field = fields_1[fieldName];
                fn(field, typeName, fieldName);
            });
        }
    });
}
function addTypeCandidate(typeCandidates, name, typeCandidate) {
    if (!typeCandidates[name]) {
        typeCandidates[name] = [];
    }
    typeCandidates[name].push(typeCandidate);
}
function defaultVisitType(name, candidates, candidateSelector) {
    if (!candidateSelector) {
        candidateSelector = function (cands) { return cands[cands.length - 1]; };
    }
    var resolveType = schemaRecreation_1.createResolveType(function (_, type) { return type; });
    if (name === 'Query' || name === 'Mutation' || name === 'Subscription') {
        var fields_2 = {};
        var operationName_1;
        switch (name) {
            case 'Query':
                operationName_1 = 'query';
                break;
            case 'Mutation':
                operationName_1 = 'mutation';
                break;
            case 'Subscription':
                operationName_1 = 'subscription';
                break;
            default:
                break;
        }
        var resolvers_1 = {};
        var resolverKey_1 = operationName_1 === 'subscription' ? 'subscribe' : 'resolve';
        candidates.forEach(function (_a) {
            var candidateType = _a.type, schema = _a.schema;
            var candidateFields = candidateType.getFields();
            fields_2 = __assign({}, fields_2, candidateFields);
            Object.keys(candidateFields).forEach(function (fieldName) {
                var _a;
                resolvers_1[fieldName] = (_a = {},
                    _a[resolverKey_1] = createDelegatingResolver(schema, operationName_1, fieldName),
                    _a);
            });
        });
        var type = new graphql_1.GraphQLObjectType({
            name: name,
            fields: schemaRecreation_1.fieldMapToFieldConfigMap(fields_2, resolveType, false),
        });
        return {
            type: type,
            resolvers: resolvers_1,
        };
    }
    else {
        var candidate = candidateSelector(candidates);
        return candidate.type;
    }
}
//# sourceMappingURL=mergeSchemas.js.map