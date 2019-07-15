import { Request, Response } from 'apollo-server-env';
import { GraphQLSchema, ValidationContext, ASTVisitor, GraphQLFormattedError, OperationDefinitionNode, DocumentNode, GraphQLError } from 'graphql';
import { KeyValueCache } from 'apollo-server-caching';
import { Trace } from 'apollo-engine-reporting-protobuf';
export declare type ValueOrPromise<T> = T | Promise<T>;
export declare type WithRequired<T, K extends keyof T> = T & Required<Pick<T, K>>;
declare type Mutable<T> = {
    -readonly [P in keyof T]: T[P];
};
export interface GraphQLServiceContext {
    schema: GraphQLSchema;
    schemaHash: string;
    engine: {
        serviceID?: string;
        apiKeyHash?: string;
    };
    persistedQueries?: {
        cache: KeyValueCache;
    };
}
export interface GraphQLRequest {
    query?: string;
    operationName?: string;
    variables?: VariableValues;
    extensions?: Record<string, any>;
    http?: Pick<Request, 'url' | 'method' | 'headers'>;
}
export declare type VariableValues = {
    [name: string]: any;
};
export interface GraphQLResponse {
    data?: Record<string, any> | null;
    errors?: ReadonlyArray<GraphQLFormattedError>;
    extensions?: Record<string, any>;
    http?: Pick<Response, 'headers'> & Partial<Pick<Mutable<Response>, 'status'>>;
}
export interface GraphQLRequestMetrics {
    captureTraces?: boolean;
    persistedQueryHit?: boolean;
    persistedQueryRegister?: boolean;
    responseCacheHit?: boolean;
    forbiddenOperation?: boolean;
    registeredOperation?: boolean;
    startHrTime?: [number, number];
    queryPlanTrace?: Trace.QueryPlanNode;
}
export interface GraphQLRequestContext<TContext = Record<string, any>> {
    readonly request: GraphQLRequest;
    readonly response?: GraphQLResponse;
    readonly context: TContext;
    readonly cache: KeyValueCache;
    readonly queryHash?: string;
    readonly document?: DocumentNode;
    readonly source?: string;
    readonly operationName?: string | null;
    readonly operation?: OperationDefinitionNode;
    readonly errors?: ReadonlyArray<GraphQLError>;
    readonly metrics?: GraphQLRequestMetrics;
    debug?: boolean;
}
export declare type ValidationRule = (context: ValidationContext) => ASTVisitor;
export declare class InvalidGraphQLRequestError extends Error {
}
export declare type GraphQLExecutor<TContext = Record<string, any>> = (requestContext: WithRequired<GraphQLRequestContext<TContext>, 'document' | 'operationName' | 'operation' | 'queryHash'>) => ValueOrPromise<GraphQLExecutionResult>;
export declare type GraphQLExecutionResult = {
    data?: Record<string, any> | null;
    errors?: ReadonlyArray<GraphQLError>;
    extensions?: Record<string, any>;
};
export {};
//# sourceMappingURL=index.d.ts.map