"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const apollo_env_1 = require("apollo-env");
function compactMap(array, callbackfn) {
    return array.reduce((accumulator, element, index, array) => {
        const result = callbackfn(element, index, array);
        if (apollo_env_1.isNotNullOrUndefined(result)) {
            accumulator.push(result);
        }
        return accumulator;
    }, []);
}
exports.compactMap = compactMap;
function partition(array, predicate) {
    array.map;
    return array.reduce((accumulator, element, index) => {
        return (predicate(element, index, array)
            ? accumulator[0].push(element)
            : accumulator[1].push(element),
            accumulator);
    }, [[], []]);
}
exports.partition = partition;
function findAndExtract(array, predicate) {
    const index = array.findIndex(predicate);
    if (index === -1)
        return [undefined, array];
    let remaining = array.slice(0, index);
    if (index < array.length - 1) {
        remaining.push(...array.slice(index + 1));
    }
    return [array[index], remaining];
}
exports.findAndExtract = findAndExtract;
//# sourceMappingURL=array.js.map