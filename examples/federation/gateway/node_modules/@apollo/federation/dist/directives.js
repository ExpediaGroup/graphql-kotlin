"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const graphql_1 = require("graphql");
exports.KeyDirective = new graphql_1.GraphQLDirective({
    name: 'key',
    description: '',
    locations: [graphql_1.DirectiveLocation.OBJECT, graphql_1.DirectiveLocation.INTERFACE],
    args: {
        fields: {
            type: graphql_1.GraphQLNonNull(graphql_1.GraphQLString),
            description: '',
        },
    },
});
exports.ExtendsDirective = new graphql_1.GraphQLDirective({
    name: 'extends',
    description: '',
    locations: [graphql_1.DirectiveLocation.OBJECT, graphql_1.DirectiveLocation.INTERFACE],
});
exports.ExternalDirective = new graphql_1.GraphQLDirective({
    name: 'external',
    description: '',
    locations: [graphql_1.DirectiveLocation.OBJECT, graphql_1.DirectiveLocation.FIELD_DEFINITION],
});
exports.RequiresDirective = new graphql_1.GraphQLDirective({
    name: 'requires',
    description: '',
    locations: [graphql_1.DirectiveLocation.FIELD_DEFINITION],
    args: {
        fields: {
            type: graphql_1.GraphQLNonNull(graphql_1.GraphQLString),
            description: '',
        },
    },
});
exports.ProvidesDirective = new graphql_1.GraphQLDirective({
    name: 'provides',
    description: '',
    locations: [graphql_1.DirectiveLocation.FIELD_DEFINITION],
    args: {
        fields: {
            type: graphql_1.GraphQLNonNull(graphql_1.GraphQLString),
            description: '',
        },
    },
});
exports.federationDirectives = [
    exports.KeyDirective,
    exports.ExtendsDirective,
    exports.ExternalDirective,
    exports.RequiresDirective,
    exports.ProvidesDirective,
];
exports.default = exports.federationDirectives;
function hasDirectives(node) {
    return Boolean('directives' in node && node.directives);
}
function gatherDirectives(type) {
    let directives = [];
    if ('extensionASTNodes' in type && type.extensionASTNodes) {
        for (const node of type.extensionASTNodes) {
            if (hasDirectives(node)) {
                directives = directives.concat(node.directives);
            }
        }
    }
    if (type.astNode && hasDirectives(type.astNode))
        directives = directives.concat(type.astNode.directives);
    return directives;
}
exports.gatherDirectives = gatherDirectives;
function typeIncludesDirective(type, directiveName) {
    if (graphql_1.isInputObjectType(type))
        return false;
    const directives = gatherDirectives(type);
    return directives.some(directive => directive.name.value === directiveName);
}
exports.typeIncludesDirective = typeIncludesDirective;
//# sourceMappingURL=directives.js.map