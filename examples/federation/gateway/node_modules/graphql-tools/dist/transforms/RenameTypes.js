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
var isSpecifiedScalarType_1 = require("../isSpecifiedScalarType");
var visitSchema_1 = require("../transforms/visitSchema");
var RenameTypes = /** @class */ (function () {
    function RenameTypes(renamer, options) {
        this.renamer = renamer;
        this.reverseMap = {};
        var _a = options || {}, _b = _a.renameBuiltins, renameBuiltins = _b === void 0 ? false : _b, _c = _a.renameScalars, renameScalars = _c === void 0 ? true : _c;
        this.renameBuiltins = renameBuiltins;
        this.renameScalars = renameScalars;
    }
    RenameTypes.prototype.transformSchema = function (originalSchema) {
        var _this = this;
        var _a;
        return visitSchema_1.visitSchema(originalSchema, (_a = {},
            _a[visitSchema_1.VisitSchemaKind.TYPE] = function (type) {
                if (isSpecifiedScalarType_1.default(type) && !_this.renameBuiltins) {
                    return undefined;
                }
                if (type instanceof graphql_1.GraphQLScalarType && !_this.renameScalars) {
                    return undefined;
                }
                var newName = _this.renamer(type.name);
                if (newName && newName !== type.name) {
                    _this.reverseMap[newName] = type.name;
                    var newType = Object.assign(Object.create(type), type);
                    newType.name = newName;
                    return newType;
                }
            },
            _a[visitSchema_1.VisitSchemaKind.ROOT_OBJECT] = function (type) {
                return undefined;
            },
            _a));
    };
    RenameTypes.prototype.transformRequest = function (originalRequest) {
        var _this = this;
        var _a;
        var newDocument = graphql_1.visit(originalRequest.document, (_a = {},
            _a[graphql_1.Kind.NAMED_TYPE] = function (node) {
                var name = node.name.value;
                if (name in _this.reverseMap) {
                    return __assign({}, node, { name: {
                            kind: graphql_1.Kind.NAME,
                            value: _this.reverseMap[name],
                        } });
                }
            },
            _a));
        return {
            document: newDocument,
            variables: originalRequest.variables,
        };
    };
    RenameTypes.prototype.transformResult = function (result) {
        if (result.data) {
            var data = this.renameTypes(result.data, 'data');
            if (data !== result.data) {
                return __assign({}, result, { data: data });
            }
        }
        return result;
    };
    RenameTypes.prototype.renameTypes = function (value, name) {
        var _this = this;
        if (name === '__typename') {
            return this.renamer(value);
        }
        if (value && typeof value === 'object') {
            var newValue_1 = Array.isArray(value) ? []
                // Create a new object with the same prototype.
                : Object.create(Object.getPrototypeOf(value));
            var returnNewValue_1 = false;
            Object.keys(value).forEach(function (key) {
                var oldChild = value[key];
                var newChild = _this.renameTypes(oldChild, key);
                newValue_1[key] = newChild;
                if (newChild !== oldChild) {
                    returnNewValue_1 = true;
                }
            });
            if (returnNewValue_1) {
                return newValue_1;
            }
        }
        return value;
    };
    return RenameTypes;
}());
exports.default = RenameTypes;
//# sourceMappingURL=RenameTypes.js.map