var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    }
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
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
var error_1 = require("graphql/error");
var getResponseKeyFromInfo_1 = require("./getResponseKeyFromInfo");
if ((typeof global !== 'undefined' && 'Symbol' in global) ||
    (typeof window !== 'undefined' && 'Symbol' in window)) {
    exports.ERROR_SYMBOL = Symbol('subSchemaErrors');
}
else {
    exports.ERROR_SYMBOL = '@@__subSchemaErrors';
}
function annotateWithChildrenErrors(object, childrenErrors) {
    var _a;
    if (!childrenErrors || childrenErrors.length === 0) {
        // Nothing to see here, move along
        return object;
    }
    if (Array.isArray(object)) {
        var byIndex_1 = {};
        childrenErrors.forEach(function (error) {
            if (!error.path) {
                return;
            }
            var index = error.path[1];
            var current = byIndex_1[index] || [];
            current.push(__assign({}, error, { path: error.path.slice(1) }));
            byIndex_1[index] = current;
        });
        return object.map(function (item, index) { return annotateWithChildrenErrors(item, byIndex_1[index]); });
    }
    return __assign({}, object, (_a = {}, _a[exports.ERROR_SYMBOL] = childrenErrors.map(function (error) { return (__assign({}, error, (error.path ? { path: error.path.slice(1) } : {}))); }), _a));
}
exports.annotateWithChildrenErrors = annotateWithChildrenErrors;
function getErrorsFromParent(object, fieldName) {
    var errors = (object && object[exports.ERROR_SYMBOL]) || [];
    var childrenErrors = [];
    for (var _i = 0, errors_1 = errors; _i < errors_1.length; _i++) {
        var error = errors_1[_i];
        if (!error.path || (error.path.length === 1 && error.path[0] === fieldName)) {
            return {
                kind: 'OWN',
                error: error
            };
        }
        else if (error.path[0] === fieldName) {
            childrenErrors.push(error);
        }
    }
    return {
        kind: 'CHILDREN',
        errors: childrenErrors
    };
}
exports.getErrorsFromParent = getErrorsFromParent;
var CombinedError = /** @class */ (function (_super) {
    __extends(CombinedError, _super);
    function CombinedError(message, errors) {
        var _this = _super.call(this, message) || this;
        _this.errors = errors;
        return _this;
    }
    return CombinedError;
}(Error));
function checkResultAndHandleErrors(result, info, responseKey) {
    if (!responseKey) {
        responseKey = getResponseKeyFromInfo_1.getResponseKeyFromInfo(info);
    }
    if (result.errors && (!result.data || result.data[responseKey] == null)) {
        // apollo-link-http & http-link-dataloader need the
        // result property to be passed through for better error handling.
        // If there is only one error, which contains a result property, pass the error through
        var newError = result.errors.length === 1 && hasResult(result.errors[0])
            ? result.errors[0]
            : new CombinedError(concatErrors(result.errors), result.errors);
        throw error_1.locatedError(newError, info.fieldNodes, graphql_1.responsePathAsArray(info.path));
    }
    var resultObject = result.data[responseKey];
    if (result.errors) {
        resultObject = annotateWithChildrenErrors(resultObject, result.errors);
    }
    return resultObject;
}
exports.checkResultAndHandleErrors = checkResultAndHandleErrors;
function concatErrors(errors) {
    return errors.map(function (error) { return error.message; }).join('\n');
}
function hasResult(error) {
    return error.result || error.extensions || (error.originalError && error.originalError.result);
}
//# sourceMappingURL=errors.js.map