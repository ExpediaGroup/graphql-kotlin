import { GraphQLRequestContext, WithRequired } from 'apollo-server-types';
import { Request, Headers } from 'apollo-server-env';
import { GraphQLResolveInfo, DocumentNode, ExecutionArgs, GraphQLError } from 'graphql';
import { GraphQLExtension, EndHandler } from 'graphql-extensions';
import { Trace } from 'apollo-engine-reporting-protobuf';
import { EngineReportingOptions, AddTraceArgs, VariableValueOptions, SendValuesBaseOptions } from './agent';
export declare class EngineReportingExtension<TContext = any> implements GraphQLExtension<TContext> {
    private schemaHash;
    private treeBuilder;
    private explicitOperationName?;
    private queryString?;
    private documentAST?;
    private options;
    private addTrace;
    private generateClientInfo;
    constructor(options: EngineReportingOptions<TContext>, addTrace: (args: AddTraceArgs) => Promise<void>, schemaHash: string);
    requestDidStart(o: {
        request: Request;
        queryString?: string;
        parsedQuery?: DocumentNode;
        variables?: Record<string, any>;
        context: TContext;
        extensions?: Record<string, any>;
        requestContext: WithRequired<GraphQLRequestContext<TContext>, 'metrics' | 'queryHash'>;
    }): EndHandler;
    executionDidStart(o: {
        executionArgs: ExecutionArgs;
    }): void;
    willResolveField(_source: any, _args: {
        [argName: string]: any;
    }, _context: TContext, info: GraphQLResolveInfo): ((error: Error | null, result: any) => void) | void;
    didEncounterErrors(errors: GraphQLError[]): void;
}
export declare function makeTraceDetails(variables: Record<string, any>, sendVariableValues?: VariableValueOptions, operationString?: string): Trace.Details;
export declare function makeHTTPRequestHeaders(http: Trace.IHTTP, headers: Headers, sendHeaders?: SendValuesBaseOptions): void;
//# sourceMappingURL=extension.d.ts.map