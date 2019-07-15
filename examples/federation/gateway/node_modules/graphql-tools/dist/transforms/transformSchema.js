Object.defineProperty(exports, "__esModule", { value: true });
var makeExecutableSchema_1 = require("../makeExecutableSchema");
var visitSchema_1 = require("../transforms/visitSchema");
var transforms_1 = require("../transforms/transforms");
var resolvers_1 = require("../stitching/resolvers");
function transformSchema(targetSchema, transforms) {
    var schema = visitSchema_1.visitSchema(targetSchema, {}, true);
    var mapping = resolvers_1.generateSimpleMapping(targetSchema);
    var resolvers = resolvers_1.generateProxyingResolvers(targetSchema, transforms, mapping);
    schema = makeExecutableSchema_1.addResolveFunctionsToSchema({
        schema: schema,
        resolvers: resolvers,
        resolverValidationOptions: {
            allowResolversNotInSchema: true,
        },
    });
    schema = transforms_1.applySchemaTransforms(schema, transforms);
    schema.transforms = transforms;
    return schema;
}
exports.default = transformSchema;
//# sourceMappingURL=transformSchema.js.map