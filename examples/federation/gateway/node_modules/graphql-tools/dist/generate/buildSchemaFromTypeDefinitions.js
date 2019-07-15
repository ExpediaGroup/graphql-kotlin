Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var _1 = require(".");
function buildSchemaFromTypeDefinitions(typeDefinitions, parseOptions) {
    // TODO: accept only array here, otherwise interfaces get confusing.
    var myDefinitions = typeDefinitions;
    var astDocument;
    if (isDocumentNode(typeDefinitions)) {
        astDocument = typeDefinitions;
    }
    else if (typeof myDefinitions !== 'string') {
        if (!Array.isArray(myDefinitions)) {
            var type = typeof myDefinitions;
            throw new _1.SchemaError("typeDefs must be a string, array or schema AST, got " + type);
        }
        myDefinitions = _1.concatenateTypeDefs(myDefinitions);
    }
    if (typeof myDefinitions === 'string') {
        astDocument = graphql_1.parse(myDefinitions, parseOptions);
    }
    var backcompatOptions = { commentDescriptions: true };
    // TODO fix types https://github.com/apollographql/graphql-tools/issues/542
    var schema = graphql_1.buildASTSchema(astDocument, backcompatOptions);
    var extensionsAst = _1.extractExtensionDefinitions(astDocument);
    if (extensionsAst.definitions.length > 0) {
        // TODO fix types https://github.com/apollographql/graphql-tools/issues/542
        schema = graphql_1.extendSchema(schema, extensionsAst, backcompatOptions);
    }
    return schema;
}
function isDocumentNode(typeDefinitions) {
    return typeDefinitions.kind !== undefined;
}
exports.default = buildSchemaFromTypeDefinitions;
//# sourceMappingURL=buildSchemaFromTypeDefinitions.js.map