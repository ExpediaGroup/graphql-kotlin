Object.defineProperty(exports, "__esModule", { value: true });
var delegateToSchema_1 = require("./delegateToSchema");
function generateProxyingResolvers(targetSchema, transforms, mapping) {
    var result = {};
    Object.keys(mapping).forEach(function (name) {
        result[name] = {};
        var innerMapping = mapping[name];
        Object.keys(innerMapping).forEach(function (from) {
            var _a;
            var to = innerMapping[from];
            var resolverType = to.operation === 'subscription' ? 'subscribe' : 'resolve';
            result[name][from] = (_a = {},
                _a[resolverType] = createProxyingResolver(targetSchema, to.operation, to.name, transforms),
                _a);
        });
    });
    return result;
}
exports.generateProxyingResolvers = generateProxyingResolvers;
function generateSimpleMapping(targetSchema) {
    var query = targetSchema.getQueryType();
    var mutation = targetSchema.getMutationType();
    var subscription = targetSchema.getSubscriptionType();
    var result = {};
    if (query) {
        result[query.name] = generateMappingFromObjectType(query, 'query');
    }
    if (mutation) {
        result[mutation.name] = generateMappingFromObjectType(mutation, 'mutation');
    }
    if (subscription) {
        result[subscription.name] = generateMappingFromObjectType(subscription, 'subscription');
    }
    return result;
}
exports.generateSimpleMapping = generateSimpleMapping;
function generateMappingFromObjectType(type, operation) {
    var result = {};
    var fields = type.getFields();
    Object.keys(fields).forEach(function (fieldName) {
        result[fieldName] = {
            name: fieldName,
            operation: operation,
        };
    });
    return result;
}
exports.generateMappingFromObjectType = generateMappingFromObjectType;
function createProxyingResolver(schema, operation, fieldName, transforms) {
    return function (parent, args, context, info) { return delegateToSchema_1.default({
        schema: schema,
        operation: operation,
        fieldName: fieldName,
        args: {},
        context: context,
        info: info,
        transforms: transforms,
    }); };
}
//# sourceMappingURL=resolvers.js.map