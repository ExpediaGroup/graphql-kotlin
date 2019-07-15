import { GraphQLSchema, GraphQLField, GraphQLFieldResolver, GraphQLResolveInfo, ExecutionArgs, DocumentNode, GraphQLError } from 'graphql';
import { Request } from 'apollo-server-env';
import { GraphQLResponse, GraphQLRequestContext } from 'apollo-server-types';
export { GraphQLResponse };
export declare type EndHandler = (...errors: Array<Error>) => void;
export declare class GraphQLExtension<TContext = any> {
    requestDidStart?(o: {
        request: Pick<Request, 'url' | 'method' | 'headers'>;
        queryString?: string;
        parsedQuery?: DocumentNode;
        operationName?: string;
        variables?: {
            [key: string]: any;
        };
        persistedQueryHit?: boolean;
        persistedQueryRegister?: boolean;
        context: TContext;
        requestContext: GraphQLRequestContext<TContext>;
    }): EndHandler | void;
    parsingDidStart?(o: {
        queryString: string;
    }): EndHandler | void;
    validationDidStart?(): EndHandler | void;
    executionDidStart?(o: {
        executionArgs: ExecutionArgs;
    }): EndHandler | void;
    didEncounterErrors?(errors: ReadonlyArray<GraphQLError>): void;
    willSendResponse?(o: {
        graphqlResponse: GraphQLResponse;
        context: TContext;
    }): void | {
        graphqlResponse: GraphQLResponse;
        context: TContext;
    };
    willResolveField?(source: any, args: {
        [argName: string]: any;
    }, context: TContext, info: GraphQLResolveInfo): ((error: Error | null, result?: any) => void) | void;
    format?(): [string, any] | undefined;
}
export declare class GraphQLExtensionStack<TContext = any> {
    fieldResolver?: GraphQLFieldResolver<any, any>;
    private extensions;
    constructor(extensions: GraphQLExtension<TContext>[]);
    requestDidStart(o: {
        request: Pick<Request, 'url' | 'method' | 'headers'>;
        queryString?: string;
        parsedQuery?: DocumentNode;
        operationName?: string;
        variables?: {
            [key: string]: any;
        };
        persistedQueryHit?: boolean;
        persistedQueryRegister?: boolean;
        context: TContext;
        extensions?: Record<string, any>;
        requestContext: GraphQLRequestContext<TContext>;
    }): EndHandler;
    parsingDidStart(o: {
        queryString: string;
    }): EndHandler;
    validationDidStart(): EndHandler;
    executionDidStart(o: {
        executionArgs: ExecutionArgs;
    }): EndHandler;
    didEncounterErrors(errors: ReadonlyArray<GraphQLError>): void;
    willSendResponse(o: {
        graphqlResponse: GraphQLResponse;
        context: TContext;
    }): {
        graphqlResponse: GraphQLResponse;
        context: TContext;
    };
    willResolveField(source: any, args: {
        [argName: string]: any;
    }, context: TContext, info: GraphQLResolveInfo): (error: Error | null, result?: any) => void;
    format(): {};
    private handleDidStart;
}
export declare function enableGraphQLExtensions(schema: GraphQLSchema & {
    _extensionsEnabled?: boolean;
}): GraphQLSchema & {
    _extensionsEnabled?: boolean | undefined;
};
export declare type FieldIteratorFn = (fieldDef: GraphQLField<any, any>, typeName: string, fieldName: string) => void;
//# sourceMappingURL=index.d.ts.map