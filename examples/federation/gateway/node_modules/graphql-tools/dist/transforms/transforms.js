Object.defineProperty(exports, "__esModule", { value: true });
function applySchemaTransforms(originalSchema, transforms) {
    return transforms.reduce(function (schema, transform) {
        return transform.transformSchema ? transform.transformSchema(schema) : schema;
    }, originalSchema);
}
exports.applySchemaTransforms = applySchemaTransforms;
function applyRequestTransforms(originalRequest, transforms) {
    return transforms.reduce(function (request, transform) {
        return transform.transformRequest
            ? transform.transformRequest(request)
            : request;
    }, originalRequest);
}
exports.applyRequestTransforms = applyRequestTransforms;
function applyResultTransforms(originalResult, transforms) {
    return transforms.reduce(function (result, transform) {
        return transform.transformResult ? transform.transformResult(result) : result;
    }, originalResult);
}
exports.applyResultTransforms = applyResultTransforms;
function composeTransforms() {
    var transforms = [];
    for (var _i = 0; _i < arguments.length; _i++) {
        transforms[_i] = arguments[_i];
    }
    var reverseTransforms = transforms.slice().reverse();
    return {
        transformSchema: function (originalSchema) {
            return applySchemaTransforms(originalSchema, transforms);
        },
        transformRequest: function (originalRequest) {
            return applyRequestTransforms(originalRequest, reverseTransforms);
        },
        transformResult: function (result) {
            return applyResultTransforms(result, reverseTransforms);
        },
    };
}
exports.composeTransforms = composeTransforms;
//# sourceMappingURL=transforms.js.map