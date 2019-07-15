Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var _1 = require(".");
// If we have any union or interface types throw if no there is no resolveType or isTypeOf resolvers
function checkForResolveTypeResolver(schema, requireResolversForResolveType) {
    Object.keys(schema.getTypeMap())
        .map(function (typeName) { return schema.getType(typeName); })
        .forEach(function (type) {
        if (!(type instanceof graphql_1.GraphQLUnionType ||
            type instanceof graphql_1.GraphQLInterfaceType)) {
            return;
        }
        if (!type.resolveType) {
            if (requireResolversForResolveType === false) {
                return;
            }
            if (requireResolversForResolveType === true) {
                throw new _1.SchemaError("Type \"" + type.name + "\" is missing a \"resolveType\" resolver");
            }
            // tslint:disable-next-line:max-line-length
            console.warn("Type \"" + type.name + "\" is missing a \"__resolveType\" resolver. Pass false into " +
                "\"resolverValidationOptions.requireResolversForResolveType\" to disable this warning.");
        }
    });
}
exports.default = checkForResolveTypeResolver;
//# sourceMappingURL=checkForResolveTypeResolver.js.map