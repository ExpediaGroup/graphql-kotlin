"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const runtimeSupportsUploads_1 = __importDefault(require("./utils/runtimeSupportsUploads"));
const processFileUploads = (() => {
    if (runtimeSupportsUploads_1.default) {
        return require('graphql-upload')
            .processRequest;
    }
    return undefined;
})();
exports.default = processFileUploads;
//# sourceMappingURL=processFileUploads.js.map