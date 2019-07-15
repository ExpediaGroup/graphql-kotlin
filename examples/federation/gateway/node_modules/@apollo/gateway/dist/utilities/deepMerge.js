"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const predicates_1 = require("./predicates");
function deepMerge(target, source) {
    if (source === undefined || source === null)
        return target;
    for (const key of Object.keys(source)) {
        if (source[key] === undefined || key === '__proto__')
            continue;
        if (target[key] && predicates_1.isObject(source[key])) {
            deepMerge(target[key], source[key]);
        }
        else if (Array.isArray(source[key]) &&
            Array.isArray(target[key]) &&
            source[key].length === target[key].length) {
            let i = 0;
            for (; i < source[key].length; i++) {
                if (predicates_1.isObject(target[key][i]) && predicates_1.isObject(source[key][i])) {
                    deepMerge(target[key][i], source[key][i]);
                }
                else {
                    target[key][i] = source[key][i];
                }
            }
        }
        else {
            target[key] = source[key];
        }
    }
    return target;
}
exports.deepMerge = deepMerge;
//# sourceMappingURL=deepMerge.js.map