Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
// FIXME: Replace with https://github.com/graphql/graphql-js/blob/master/src/type/scalars.js#L139
exports.specifiedScalarTypes = [
    graphql_1.GraphQLString,
    graphql_1.GraphQLInt,
    graphql_1.GraphQLFloat,
    graphql_1.GraphQLBoolean,
    graphql_1.GraphQLID,
];
function isSpecifiedScalarType(type) {
    return (graphql_1.isNamedType(type) &&
        // Would prefer to use specifiedScalarTypes.some(), however %checks needs
        // a simple expression.
        (type.name === graphql_1.GraphQLString.name ||
            type.name === graphql_1.GraphQLInt.name ||
            type.name === graphql_1.GraphQLFloat.name ||
            type.name === graphql_1.GraphQLBoolean.name ||
            type.name === graphql_1.GraphQLID.name));
}
exports.default = isSpecifiedScalarType;
//# sourceMappingURL=isSpecifiedScalarType.js.map