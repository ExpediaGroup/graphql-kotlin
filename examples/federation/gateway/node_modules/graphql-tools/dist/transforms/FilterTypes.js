/* tslint:disable:no-unused-expression */
Object.defineProperty(exports, "__esModule", { value: true });
var visitSchema_1 = require("../transforms/visitSchema");
var FilterTypes = /** @class */ (function () {
    function FilterTypes(filter) {
        this.filter = filter;
    }
    FilterTypes.prototype.transformSchema = function (schema) {
        var _this = this;
        var _a;
        return visitSchema_1.visitSchema(schema, (_a = {},
            _a[visitSchema_1.VisitSchemaKind.TYPE] = function (type) {
                if (_this.filter(type)) {
                    return undefined;
                }
                else {
                    return null;
                }
            },
            _a));
    };
    return FilterTypes;
}());
exports.default = FilterTypes;
//# sourceMappingURL=FilterTypes.js.map