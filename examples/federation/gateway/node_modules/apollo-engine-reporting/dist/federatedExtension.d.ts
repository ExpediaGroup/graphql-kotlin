import { GraphQLResolveInfo, GraphQLError } from 'graphql';
import { GraphQLExtension } from 'graphql-extensions';
import { GraphQLRequestContext } from 'apollo-server-types';
export declare class EngineFederatedTracingExtension<TContext = any> implements GraphQLExtension<TContext> {
    private enabled;
    private done;
    private treeBuilder;
    constructor(options: {
        rewriteError?: (err: GraphQLError) => GraphQLError | null;
    });
    requestDidStart(o: {
        requestContext: GraphQLRequestContext<TContext>;
    }): void;
    willResolveField(_source: any, _args: {
        [argName: string]: any;
    }, _context: TContext, info: GraphQLResolveInfo): ((error: Error | null, result: any) => void) | void;
    didEncounterErrors(errors: GraphQLError[]): void;
    format(): [string, string] | undefined;
}
//# sourceMappingURL=federatedExtension.d.ts.map