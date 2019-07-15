"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const isNodeLike_1 = __importDefault(require("./isNodeLike"));
const runtimeSupportsUploads = (() => {
    if (isNodeLike_1.default) {
        const [nodeMajor, nodeMinor] = process.versions.node
            .split('.', 2)
            .map(segment => parseInt(segment, 10));
        if (nodeMajor < 8 || (nodeMajor === 8 && nodeMinor < 5)) {
            return false;
        }
        return true;
    }
    return false;
})();
exports.default = runtimeSupportsUploads;
//# sourceMappingURL=runtimeSupportsUploads.js.map