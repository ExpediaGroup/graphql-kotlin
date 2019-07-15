Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var resolveFromParentTypename_1 = require("./resolveFromParentTypename");
var backcompatOptions = { commentDescriptions: true };
function typeFromAST(node) {
    switch (node.kind) {
        case graphql_1.Kind.OBJECT_TYPE_DEFINITION:
            return makeObjectType(node);
        case graphql_1.Kind.INTERFACE_TYPE_DEFINITION:
            return makeInterfaceType(node);
        case graphql_1.Kind.ENUM_TYPE_DEFINITION:
            return makeEnumType(node);
        case graphql_1.Kind.UNION_TYPE_DEFINITION:
            return makeUnionType(node);
        case graphql_1.Kind.SCALAR_TYPE_DEFINITION:
            return makeScalarType(node);
        case graphql_1.Kind.INPUT_OBJECT_TYPE_DEFINITION:
            return makeInputObjectType(node);
        case graphql_1.Kind.DIRECTIVE_DEFINITION:
            return makeDirective(node);
        default:
            return null;
    }
}
exports.default = typeFromAST;
function makeObjectType(node) {
    return new graphql_1.GraphQLObjectType({
        name: node.name.value,
        fields: function () { return makeFields(node.fields); },
        interfaces: function () {
            return node.interfaces.map(function (iface) { return createNamedStub(iface.name.value, 'interface'); });
        },
        description: graphql_1.getDescription(node, backcompatOptions),
    });
}
function makeInterfaceType(node) {
    return new graphql_1.GraphQLInterfaceType({
        name: node.name.value,
        fields: function () { return makeFields(node.fields); },
        description: graphql_1.getDescription(node, backcompatOptions),
        resolveType: function (parent, context, info) {
            return resolveFromParentTypename_1.default(parent, info.schema);
        },
    });
}
function makeEnumType(node) {
    var values = {};
    node.values.forEach(function (value) {
        values[value.name.value] = {
            description: graphql_1.getDescription(value, backcompatOptions),
        };
    });
    return new graphql_1.GraphQLEnumType({
        name: node.name.value,
        values: values,
        description: graphql_1.getDescription(node, backcompatOptions),
    });
}
function makeUnionType(node) {
    return new graphql_1.GraphQLUnionType({
        name: node.name.value,
        types: function () {
            return node.types.map(function (type) { return resolveType(type, 'object'); });
        },
        description: graphql_1.getDescription(node, backcompatOptions),
        resolveType: function (parent, context, info) {
            return resolveFromParentTypename_1.default(parent, info.schema);
        },
    });
}
function makeScalarType(node) {
    return new graphql_1.GraphQLScalarType({
        name: node.name.value,
        description: graphql_1.getDescription(node, backcompatOptions),
        serialize: function () { return null; },
        // Note: validation calls the parse functions to determine if a
        // literal value is correct. Returning null would cause use of custom
        // scalars to always fail validation. Returning false causes them to
        // always pass validation.
        parseValue: function () { return false; },
        parseLiteral: function () { return false; },
    });
}
function makeInputObjectType(node) {
    return new graphql_1.GraphQLInputObjectType({
        name: node.name.value,
        fields: function () { return makeValues(node.fields); },
        description: graphql_1.getDescription(node, backcompatOptions),
    });
}
function makeFields(nodes) {
    var result = {};
    nodes.forEach(function (node) {
        var deprecatedDirective = node.directives.find(function (directive) {
            return directive && directive.name && directive.name.value === 'deprecated';
        });
        var deprecatedArgument = deprecatedDirective &&
            deprecatedDirective.arguments &&
            deprecatedDirective.arguments.find(function (arg) { return arg && arg.name && arg.name.value === 'reason'; });
        var deprecationReason = deprecatedArgument &&
            deprecatedArgument.value &&
            deprecatedArgument.value.value;
        result[node.name.value] = {
            type: resolveType(node.type, 'object'),
            args: makeValues(node.arguments),
            description: graphql_1.getDescription(node, backcompatOptions),
            deprecationReason: deprecationReason,
        };
    });
    return result;
}
function makeValues(nodes) {
    var result = {};
    nodes.forEach(function (node) {
        var type = resolveType(node.type, 'input');
        result[node.name.value] = {
            type: type,
            defaultValue: graphql_1.valueFromAST(node.defaultValue, type),
            description: graphql_1.getDescription(node, backcompatOptions),
        };
    });
    return result;
}
function resolveType(node, type) {
    switch (node.kind) {
        case graphql_1.Kind.LIST_TYPE:
            return new graphql_1.GraphQLList(resolveType(node.type, type));
        case graphql_1.Kind.NON_NULL_TYPE:
            return new graphql_1.GraphQLNonNull(resolveType(node.type, type));
        default:
            return createNamedStub(node.name.value, type);
    }
}
function createNamedStub(name, type) {
    var constructor;
    if (type === 'object') {
        constructor = graphql_1.GraphQLObjectType;
    }
    else if (type === 'interface') {
        constructor = graphql_1.GraphQLInterfaceType;
    }
    else {
        constructor = graphql_1.GraphQLInputObjectType;
    }
    return new constructor({
        name: name,
        fields: {
            __fake: {
                type: graphql_1.GraphQLString,
            },
        },
    });
}
function makeDirective(node) {
    var locations = [];
    node.locations.forEach(function (location) {
        if (location.value in graphql_1.DirectiveLocation) {
            locations.push(location.value);
        }
    });
    return new graphql_1.GraphQLDirective({
        name: node.name.value,
        description: node.description ? node.description.value : null,
        args: makeValues(node.arguments),
        locations: locations,
    });
}
//# sourceMappingURL=typeFromAST.js.map