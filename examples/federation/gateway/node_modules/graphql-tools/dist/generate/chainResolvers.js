Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
function chainResolvers(resolvers) {
    return function (root, args, ctx, info) {
        return resolvers.reduce(function (prev, curResolver) {
            if (curResolver) {
                return curResolver(prev, args, ctx, info);
            }
            return graphql_1.defaultFieldResolver(prev, args, ctx, info);
        }, root);
    };
}
exports.chainResolvers = chainResolvers;
//# sourceMappingURL=chainResolvers.js.map