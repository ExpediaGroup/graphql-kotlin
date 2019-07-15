Object.defineProperty(exports, "__esModule", { value: true });
var TransformRootFields_1 = require("./TransformRootFields");
var FilterRootFields = /** @class */ (function () {
    function FilterRootFields(filter) {
        this.transformer = new TransformRootFields_1.default(function (operation, fieldName, field) {
            if (filter(operation, fieldName, field)) {
                return undefined;
            }
            else {
                return null;
            }
        });
    }
    FilterRootFields.prototype.transformSchema = function (originalSchema) {
        return this.transformer.transformSchema(originalSchema);
    };
    return FilterRootFields;
}());
exports.default = FilterRootFields;
//# sourceMappingURL=FilterRootFields.js.map