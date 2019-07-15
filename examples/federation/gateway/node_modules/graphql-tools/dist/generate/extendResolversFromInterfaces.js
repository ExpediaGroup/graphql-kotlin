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
function extendResolversFromInterfaces(schema, resolvers) {
    var typeNames = Object.keys(__assign({}, schema.getTypeMap(), resolvers));
    var extendedResolvers = {};
    typeNames.forEach(function (typeName) {
        var typeResolvers = resolvers[typeName];
        var type = schema.getType(typeName);
        if (type instanceof graphql_1.GraphQLObjectType) {
            var interfaceResolvers = type
                .getInterfaces()
                .map(function (iFace) { return resolvers[iFace.name]; });
            extendedResolvers[typeName] = Object.assign.apply(Object, [{}].concat(interfaceResolvers, [typeResolvers]));
        }
        else {
            if (typeResolvers) {
                extendedResolvers[typeName] = typeResolvers;
            }
        }
    });
    return extendedResolvers;
}
exports.default = extendResolversFromInterfaces;
//# sourceMappingURL=extendResolversFromInterfaces.js.map