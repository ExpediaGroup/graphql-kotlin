Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var error_1 = require("graphql/error");
var errors_1 = require("./errors");
var getResponseKeyFromInfo_1 = require("./getResponseKeyFromInfo");
// Resolver that knows how to:
// a) handle aliases for proxied schemas
// b) handle errors from proxied schemas
var defaultMergedResolver = function (parent, args, context, info) {
    if (!parent) {
        return null;
    }
    var responseKey = getResponseKeyFromInfo_1.getResponseKeyFromInfo(info);
    var errorResult = errors_1.getErrorsFromParent(parent, responseKey);
    if (errorResult.kind === 'OWN') {
        throw error_1.locatedError(new Error(errorResult.error.message), info.fieldNodes, graphql_1.responsePathAsArray(info.path));
    }
    var result = parent[responseKey];
    if (result == null) {
        result = parent[info.fieldName];
    }
    // subscription result mapping
    if (!result && parent.data && parent.data[responseKey]) {
        result = parent.data[responseKey];
    }
    if (errorResult.errors) {
        result = errors_1.annotateWithChildrenErrors(result, errorResult.errors);
    }
    return result;
};
exports.default = defaultMergedResolver;
//# sourceMappingURL=defaultMergedResolver.js.map