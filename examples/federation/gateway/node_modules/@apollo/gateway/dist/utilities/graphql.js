"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const graphql_1 = require("graphql");
function getFieldDef(schema, parentType, fieldName) {
    if (fieldName === graphql_1.SchemaMetaFieldDef.name &&
        schema.getQueryType() === parentType) {
        return graphql_1.SchemaMetaFieldDef;
    }
    if (fieldName === graphql_1.TypeMetaFieldDef.name &&
        schema.getQueryType() === parentType) {
        return graphql_1.TypeMetaFieldDef;
    }
    if (fieldName === graphql_1.TypeNameMetaFieldDef.name &&
        (parentType instanceof graphql_1.GraphQLObjectType ||
            parentType instanceof graphql_1.GraphQLInterfaceType ||
            parentType instanceof graphql_1.GraphQLUnionType)) {
        return graphql_1.TypeNameMetaFieldDef;
    }
    if (parentType instanceof graphql_1.GraphQLObjectType ||
        parentType instanceof graphql_1.GraphQLInterfaceType) {
        return parentType.getFields()[fieldName];
    }
    return undefined;
}
exports.getFieldDef = getFieldDef;
function getResponseName(node) {
    return node.alias ? node.alias.value : node.name.value;
}
exports.getResponseName = getResponseName;
function allNodesAreOfSameKind(firstNode, remainingNodes) {
    return !remainingNodes.some(node => node.kind !== firstNode.kind);
}
exports.allNodesAreOfSameKind = allNodesAreOfSameKind;
function astFromType(type) {
    if (graphql_1.isListType(type)) {
        return { kind: graphql_1.Kind.LIST_TYPE, type: astFromType(type.ofType) };
    }
    else if (graphql_1.isNonNullType(type)) {
        return { kind: graphql_1.Kind.NON_NULL_TYPE, type: astFromType(type.ofType) };
    }
    else {
        return {
            kind: graphql_1.Kind.NAMED_TYPE,
            name: { kind: graphql_1.Kind.NAME, value: type.name },
        };
    }
}
exports.astFromType = astFromType;
function printWithReducedWhitespace(ast) {
    return graphql_1.print(ast)
        .replace(/\s+/g, ' ')
        .trim();
}
exports.printWithReducedWhitespace = printWithReducedWhitespace;
function parseSelections(source) {
    return graphql_1.parse(`query { ${source} }`)
        .definitions[0].selectionSet.selections;
}
exports.parseSelections = parseSelections;
//# sourceMappingURL=graphql.js.map