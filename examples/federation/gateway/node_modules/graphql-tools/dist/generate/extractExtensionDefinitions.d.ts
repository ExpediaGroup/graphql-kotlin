import { DocumentNode, DefinitionNode } from 'graphql';
export default function extractExtensionDefinitions(ast: DocumentNode): DocumentNode & {
    definitions: DefinitionNode[];
};
