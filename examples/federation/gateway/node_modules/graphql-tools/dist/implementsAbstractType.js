Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
function implementsAbstractType(schema, typeA, typeB) {
    if (typeA === typeB) {
        return true;
    }
    else if (graphql_1.isCompositeType(typeA) && graphql_1.isCompositeType(typeB)) {
        return graphql_1.doTypesOverlap(schema, typeA, typeB);
    }
    else {
        return false;
    }
}
exports.default = implementsAbstractType;
//# sourceMappingURL=implementsAbstractType.js.map