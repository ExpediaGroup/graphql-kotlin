import 'apollo-server-env';
export { runHttpQuery, HttpQueryRequest, HttpQueryError } from './runHttpQuery';
export { default as GraphQLOptions, resolveGraphqlOptions, PersistedQueryOptions, } from './graphqlOptions';
export { ApolloError, toApolloError, SyntaxError, ValidationError, AuthenticationError, ForbiddenError, UserInputError, formatApolloErrors, } from 'apollo-server-errors';
export { convertNodeHttpToRequest } from './nodeHttpToRequest';
export { createPlaygroundOptions, PlaygroundConfig, defaultPlaygroundOptions, PlaygroundRenderPageOptions, } from './playground';
export { ApolloServerBase } from './ApolloServer';
export * from './types';
export * from './requestPipelineAPI';
import { DocumentNode } from 'graphql';
export declare const gql: (template: TemplateStringsArray | string, ...substitutions: any[]) => DocumentNode;
import { GraphQLScalarType } from 'graphql';
export { default as processFileUploads } from './processFileUploads';
export declare const GraphQLUpload: GraphQLScalarType | undefined;
//# sourceMappingURL=index.d.ts.map