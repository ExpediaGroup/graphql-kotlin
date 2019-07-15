Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var _1 = require(".");
function concatenateTypeDefs(typeDefinitionsAry, calledFunctionRefs) {
    if (calledFunctionRefs === void 0) { calledFunctionRefs = []; }
    var resolvedTypeDefinitions = [];
    typeDefinitionsAry.forEach(function (typeDef) {
        if (typeDef.kind !== undefined) {
            typeDef = graphql_1.print(typeDef);
        }
        if (typeof typeDef === 'function') {
            if (calledFunctionRefs.indexOf(typeDef) === -1) {
                calledFunctionRefs.push(typeDef);
                resolvedTypeDefinitions = resolvedTypeDefinitions.concat(concatenateTypeDefs(typeDef(), calledFunctionRefs));
            }
        }
        else if (typeof typeDef === 'string') {
            resolvedTypeDefinitions.push(typeDef.trim());
        }
        else {
            var type = typeof typeDef;
            throw new _1.SchemaError("typeDef array must contain only strings and functions, got " + type);
        }
    });
    return uniq(resolvedTypeDefinitions.map(function (x) { return x.trim(); })).join('\n');
}
function uniq(array) {
    return array.reduce(function (accumulator, currentValue) {
        return accumulator.indexOf(currentValue) === -1
            ? accumulator.concat([currentValue]) : accumulator;
    }, []);
}
exports.default = concatenateTypeDefs;
//# sourceMappingURL=concatenateTypeDefs.js.map