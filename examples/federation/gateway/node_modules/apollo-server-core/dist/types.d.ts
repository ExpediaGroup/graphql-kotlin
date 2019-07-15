import { GraphQLSchema, DocumentNode } from 'graphql';
import { SchemaDirectiveVisitor, IResolvers, IMocks, GraphQLParseOptions } from 'graphql-tools';
import { ValueOrPromise, GraphQLExecutor } from 'apollo-server-types';
import { ConnectionContext } from 'subscriptions-transport-ws';
import WebSocket = require('ws');
import { GraphQLExtension } from 'graphql-extensions';
export { GraphQLExtension } from 'graphql-extensions';
import { EngineReportingOptions } from 'apollo-engine-reporting';
import { PlaygroundConfig } from './playground';
export { PlaygroundConfig, PlaygroundRenderPageOptions } from './playground';
import { GraphQLServerOptions as GraphQLOptions, PersistedQueryOptions } from './graphqlOptions';
import { CacheControlExtensionOptions } from 'apollo-cache-control';
import { ApolloServerPlugin } from 'apollo-server-plugin-base';
import { GraphQLSchemaModule } from '@apollographql/apollo-tools';
export { GraphQLSchemaModule };
export { KeyValueCache } from 'apollo-server-caching';
export declare type Context<T = object> = T;
export declare type ContextFunction<FunctionParams = any, ProducedContext = object> = (context: FunctionParams) => ValueOrPromise<Context<ProducedContext>>;
export declare type PluginDefinition = ApolloServerPlugin | (() => ApolloServerPlugin);
export interface SubscriptionServerOptions {
    path: string;
    keepAlive?: number;
    onConnect?: (connectionParams: Object, websocket: WebSocket, context: ConnectionContext) => any;
    onDisconnect?: (websocket: WebSocket, context: ConnectionContext) => any;
}
declare type BaseConfig = Pick<GraphQLOptions<Context>, 'formatError' | 'debug' | 'rootValue' | 'validationRules' | 'executor' | 'formatResponse' | 'fieldResolver' | 'tracing' | 'dataSources' | 'cache'>;
export declare type Unsubscriber = () => void;
export declare type SchemaChangeCallback = (schema: GraphQLSchema) => void;
export declare type GraphQLServiceConfig = {
    schema: GraphQLSchema;
    executor: GraphQLExecutor;
};
export declare type GraphQLServiceEngineConfig = {
    apiKeyHash: string;
    graphId: string;
    graphVariant?: string;
};
export interface GraphQLService {
    load(options: {
        engine?: GraphQLServiceEngineConfig;
    }): Promise<GraphQLServiceConfig>;
    onSchemaChange(callback: SchemaChangeCallback): Unsubscriber;
}
export interface Config extends BaseConfig {
    modules?: GraphQLSchemaModule[];
    typeDefs?: DocumentNode | Array<DocumentNode> | string | Array<string>;
    parseOptions?: GraphQLParseOptions;
    resolvers?: IResolvers | Array<IResolvers>;
    schema?: GraphQLSchema;
    schemaDirectives?: Record<string, typeof SchemaDirectiveVisitor>;
    context?: Context | ContextFunction;
    introspection?: boolean;
    mocks?: boolean | IMocks;
    mockEntireSchema?: boolean;
    engine?: boolean | EngineReportingOptions<Context>;
    extensions?: Array<() => GraphQLExtension>;
    cacheControl?: CacheControlExtensionOptions | boolean;
    plugins?: PluginDefinition[];
    persistedQueries?: PersistedQueryOptions | false;
    subscriptions?: Partial<SubscriptionServerOptions> | string | false;
    uploads?: boolean | FileUploadOptions;
    playground?: PlaygroundConfig;
    gateway?: GraphQLService;
}
export interface FileUploadOptions {
    maxFieldSize?: number;
    maxFileSize?: number;
    maxFiles?: number;
}
//# sourceMappingURL=types.d.ts.map