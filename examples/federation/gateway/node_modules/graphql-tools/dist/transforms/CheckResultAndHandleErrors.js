Object.defineProperty(exports, "__esModule", { value: true });
var errors_1 = require("../stitching/errors");
var CheckResultAndHandleErrors = /** @class */ (function () {
    function CheckResultAndHandleErrors(info, fieldName) {
        this.info = info;
        this.fieldName = fieldName;
    }
    CheckResultAndHandleErrors.prototype.transformResult = function (result) {
        return errors_1.checkResultAndHandleErrors(result, this.info, this.fieldName);
    };
    return CheckResultAndHandleErrors;
}());
exports.default = CheckResultAndHandleErrors;
//# sourceMappingURL=CheckResultAndHandleErrors.js.map