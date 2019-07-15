Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
function resolveFromParentTypename(parent, schema) {
    var parentTypename = parent['__typename'];
    if (!parentTypename) {
        throw new Error('Did not fetch typename for object, unable to resolve interface.');
    }
    var resolvedType = schema.getType(parentTypename);
    if (!(resolvedType instanceof graphql_1.GraphQLObjectType)) {
        throw new Error('__typename did not match an object type: ' + parentTypename);
    }
    return resolvedType;
}
exports.default = resolveFromParentTypename;
//# sourceMappingURL=resolveFromParentTypename.js.map