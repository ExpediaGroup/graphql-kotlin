"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class MultiMap extends Map {
    add(key, value) {
        let values = this.get(key);
        if (values) {
            values.push(value);
        }
        else {
            this.set(key, (values = [value]));
        }
        return this;
    }
}
exports.MultiMap = MultiMap;
//# sourceMappingURL=MultiMap.js.map