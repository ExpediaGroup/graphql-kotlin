Object.defineProperty(exports, "__esModule", { value: true });
var ConvertEnumResponse = /** @class */ (function () {
    function ConvertEnumResponse(enumNode) {
        this.enumNode = enumNode;
    }
    ConvertEnumResponse.prototype.transformResult = function (result) {
        var value = this.enumNode.getValue(result);
        if (value) {
            return value.value;
        }
        return result;
    };
    return ConvertEnumResponse;
}());
exports.default = ConvertEnumResponse;
//# sourceMappingURL=ConvertEnumResponse.js.map