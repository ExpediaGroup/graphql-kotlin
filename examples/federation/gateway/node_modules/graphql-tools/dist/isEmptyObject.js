Object.defineProperty(exports, "__esModule", { value: true });
function isEmptyObject(obj) {
    if (!obj) {
        return true;
    }
    for (var key in obj) {
        if (Object.hasOwnProperty.call(obj, key)) {
            return false;
        }
    }
    return true;
}
exports.default = isEmptyObject;
//# sourceMappingURL=isEmptyObject.js.map