import { GraphQLResolveInfo } from 'graphql';
import { GraphQLExtension } from 'graphql-extensions';
export interface TracingFormat {
    version: 1;
    startTime: string;
    endTime: string;
    duration: number;
    execution: {
        resolvers: {
            path: (string | number)[];
            parentType: string;
            fieldName: string;
            returnType: string;
            startOffset: number;
            duration: number;
        }[];
    };
}
export declare class TracingExtension<TContext = any> implements GraphQLExtension<TContext> {
    private startWallTime?;
    private endWallTime?;
    private startHrTime?;
    private duration?;
    private resolverCalls;
    requestDidStart(): void;
    executionDidStart(): () => void;
    willResolveField(_source: any, _args: {
        [argName: string]: any;
    }, _context: TContext, info: GraphQLResolveInfo): () => void;
    format(): [string, TracingFormat] | undefined;
}
//# sourceMappingURL=index.d.ts.map