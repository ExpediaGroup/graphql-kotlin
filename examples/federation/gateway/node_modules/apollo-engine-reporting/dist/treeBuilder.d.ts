import { GraphQLResolveInfo, GraphQLError } from 'graphql';
import { Trace } from 'apollo-engine-reporting-protobuf';
export declare class EngineReportingTreeBuilder {
    private rootNode;
    trace: Trace;
    startHrTime?: [number, number];
    private stopped;
    private nodes;
    private rewriteError?;
    constructor(options: {
        rewriteError?: (err: GraphQLError) => GraphQLError | null;
    });
    startTiming(): void;
    stopTiming(): void;
    willResolveField(info: GraphQLResolveInfo): () => void;
    didEncounterErrors(errors: GraphQLError[]): void;
    private addProtobufError;
    private newNode;
    private ensureParentNode;
    private rewriteAndNormalizeError;
}
//# sourceMappingURL=treeBuilder.d.ts.map