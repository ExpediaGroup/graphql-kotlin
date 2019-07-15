"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const language_1 = require("graphql/language");
const __1 = require("../");
exports.isDirectiveDefined = (typeDefs, directiveName) => {
    typeDefs = Array.isArray(typeDefs) ? typeDefs : [typeDefs];
    return typeDefs.some(typeDef => {
        if (typeof typeDef === 'string') {
            typeDef = __1.gql(typeDef);
        }
        return typeDef.definitions.some(definition => definition.kind === language_1.Kind.DIRECTIVE_DEFINITION &&
            definition.name.value === directiveName);
    });
};
//# sourceMappingURL=isDirectiveDefined.js.map