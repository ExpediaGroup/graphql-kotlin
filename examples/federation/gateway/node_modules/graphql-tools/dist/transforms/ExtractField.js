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
var ExtractField = /** @class */ (function () {
    function ExtractField(_a) {
        var from = _a.from, to = _a.to;
        this.from = from;
        this.to = to;
    }
    ExtractField.prototype.transformRequest = function (originalRequest) {
        var _a, _b;
        var fromSelection;
        var ourPathFrom = JSON.stringify(this.from);
        var ourPathTo = JSON.stringify(this.to);
        var fieldPath = [];
        graphql_1.visit(originalRequest.document, (_a = {},
            _a[graphql_1.Kind.FIELD] = {
                enter: function (node) {
                    fieldPath.push(node.name.value);
                    if (ourPathFrom === JSON.stringify(fieldPath)) {
                        fromSelection = node.selectionSet;
                        return graphql_1.BREAK;
                    }
                },
                leave: function (node) {
                    fieldPath.pop();
                },
            },
            _a));
        fieldPath = [];
        var newDocument = graphql_1.visit(originalRequest.document, (_b = {},
            _b[graphql_1.Kind.FIELD] = {
                enter: function (node) {
                    fieldPath.push(node.name.value);
                    if (ourPathTo === JSON.stringify(fieldPath) && fromSelection) {
                        return __assign({}, node, { selectionSet: fromSelection });
                    }
                },
                leave: function (node) {
                    fieldPath.pop();
                },
            },
            _b));
        return __assign({}, originalRequest, { document: newDocument });
    };
    return ExtractField;
}());
exports.default = ExtractField;
//# sourceMappingURL=ExtractField.js.map