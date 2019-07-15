Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var schemaRecreation_1 = require("../stitching/schemaRecreation");
var VisitSchemaKind;
(function (VisitSchemaKind) {
    VisitSchemaKind["TYPE"] = "VisitSchemaKind.TYPE";
    VisitSchemaKind["SCALAR_TYPE"] = "VisitSchemaKind.SCALAR_TYPE";
    VisitSchemaKind["ENUM_TYPE"] = "VisitSchemaKind.ENUM_TYPE";
    VisitSchemaKind["COMPOSITE_TYPE"] = "VisitSchemaKind.COMPOSITE_TYPE";
    VisitSchemaKind["OBJECT_TYPE"] = "VisitSchemaKind.OBJECT_TYPE";
    VisitSchemaKind["INPUT_OBJECT_TYPE"] = "VisitSchemaKind.INPUT_OBJECT_TYPE";
    VisitSchemaKind["ABSTRACT_TYPE"] = "VisitSchemaKind.ABSTRACT_TYPE";
    VisitSchemaKind["UNION_TYPE"] = "VisitSchemaKind.UNION_TYPE";
    VisitSchemaKind["INTERFACE_TYPE"] = "VisitSchemaKind.INTERFACE_TYPE";
    VisitSchemaKind["ROOT_OBJECT"] = "VisitSchemaKind.ROOT_OBJECT";
    VisitSchemaKind["QUERY"] = "VisitSchemaKind.QUERY";
    VisitSchemaKind["MUTATION"] = "VisitSchemaKind.MUTATION";
    VisitSchemaKind["SUBSCRIPTION"] = "VisitSchemaKind.SUBSCRIPTION";
})(VisitSchemaKind = exports.VisitSchemaKind || (exports.VisitSchemaKind = {}));
function visitSchema(schema, visitor, stripResolvers) {
    var types = {};
    var resolveType = schemaRecreation_1.createResolveType(function (name) {
        if (typeof types[name] === 'undefined') {
            throw new Error("Can't find type " + name + ".");
        }
        return types[name];
    });
    var queryType = schema.getQueryType();
    var mutationType = schema.getMutationType();
    var subscriptionType = schema.getSubscriptionType();
    var typeMap = schema.getTypeMap();
    Object.keys(typeMap).map(function (typeName) {
        var type = typeMap[typeName];
        if (graphql_1.isNamedType(type) && graphql_1.getNamedType(type).name.slice(0, 2) !== '__') {
            var specifiers = getTypeSpecifiers(type, schema);
            var typeVisitor = getVisitor(visitor, specifiers);
            if (typeVisitor) {
                var result = typeVisitor(type, schema);
                if (typeof result === 'undefined') {
                    types[typeName] = schemaRecreation_1.recreateType(type, resolveType, !stripResolvers);
                }
                else if (result === null) {
                    types[typeName] = null;
                }
                else {
                    types[typeName] = schemaRecreation_1.recreateType(result, resolveType, !stripResolvers);
                }
            }
            else {
                types[typeName] = schemaRecreation_1.recreateType(type, resolveType, !stripResolvers);
            }
        }
    });
    return new graphql_1.GraphQLSchema({
        query: queryType ? types[queryType.name] : null,
        mutation: mutationType
            ? types[mutationType.name]
            : null,
        subscription: subscriptionType
            ? types[subscriptionType.name]
            : null,
        types: Object.keys(types).map(function (name) { return types[name]; }),
    });
}
exports.visitSchema = visitSchema;
function getTypeSpecifiers(type, schema) {
    var specifiers = [VisitSchemaKind.TYPE];
    if (type instanceof graphql_1.GraphQLObjectType) {
        specifiers.unshift(VisitSchemaKind.COMPOSITE_TYPE, VisitSchemaKind.OBJECT_TYPE);
        var query = schema.getQueryType();
        var mutation = schema.getMutationType();
        var subscription = schema.getSubscriptionType();
        if (type === query) {
            specifiers.push(VisitSchemaKind.ROOT_OBJECT, VisitSchemaKind.QUERY);
        }
        else if (type === mutation) {
            specifiers.push(VisitSchemaKind.ROOT_OBJECT, VisitSchemaKind.MUTATION);
        }
        else if (type === subscription) {
            specifiers.push(VisitSchemaKind.ROOT_OBJECT, VisitSchemaKind.SUBSCRIPTION);
        }
    }
    else if (type instanceof graphql_1.GraphQLInputObjectType) {
        specifiers.push(VisitSchemaKind.INPUT_OBJECT_TYPE);
    }
    else if (type instanceof graphql_1.GraphQLInterfaceType) {
        specifiers.push(VisitSchemaKind.COMPOSITE_TYPE, VisitSchemaKind.ABSTRACT_TYPE, VisitSchemaKind.INTERFACE_TYPE);
    }
    else if (type instanceof graphql_1.GraphQLUnionType) {
        specifiers.push(VisitSchemaKind.COMPOSITE_TYPE, VisitSchemaKind.ABSTRACT_TYPE, VisitSchemaKind.UNION_TYPE);
    }
    else if (type instanceof graphql_1.GraphQLEnumType) {
        specifiers.push(VisitSchemaKind.ENUM_TYPE);
    }
    else if (type instanceof graphql_1.GraphQLScalarType) {
        specifiers.push(VisitSchemaKind.SCALAR_TYPE);
    }
    return specifiers;
}
function getVisitor(visitor, specifiers) {
    var typeVisitor = null;
    var stack = specifiers.slice();
    while (!typeVisitor && stack.length > 0) {
        var next = stack.pop();
        typeVisitor = visitor[next];
    }
    return typeVisitor;
}
//# sourceMappingURL=visitSchema.js.map