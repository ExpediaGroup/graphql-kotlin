"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const snapshotSerializers_1 = require("./snapshotSerializers");
const pretty_format_1 = __importDefault(require("pretty-format"));
function serializeQueryPlan(queryPlan) {
    return pretty_format_1.default(queryPlan, {
        plugins: [snapshotSerializers_1.queryPlanSerializer, snapshotSerializers_1.astSerializer],
    });
}
exports.serializeQueryPlan = serializeQueryPlan;
//# sourceMappingURL=QueryPlan.js.map