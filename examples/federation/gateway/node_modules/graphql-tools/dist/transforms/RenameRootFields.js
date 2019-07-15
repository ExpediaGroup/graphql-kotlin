Object.defineProperty(exports, "__esModule", { value: true });
var schemaRecreation_1 = require("../stitching/schemaRecreation");
var TransformRootFields_1 = require("./TransformRootFields");
var RenameRootFields = /** @class */ (function () {
    function RenameRootFields(renamer) {
        var resolveType = schemaRecreation_1.createResolveType(function (name, type) { return type; });
        this.transformer = new TransformRootFields_1.default(function (operation, fieldName, field) {
            return {
                name: renamer(operation, fieldName, field),
                field: schemaRecreation_1.fieldToFieldConfig(field, resolveType, true),
            };
        });
    }
    RenameRootFields.prototype.transformSchema = function (originalSchema) {
        return this.transformer.transformSchema(originalSchema);
    };
    return RenameRootFields;
}());
exports.default = RenameRootFields;
//# sourceMappingURL=RenameRootFields.js.map