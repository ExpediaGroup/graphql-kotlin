import { GraphQLRequestContext, GraphQLResponse } from 'apollo-server-types';
import { GraphQLSchema, DocumentNode } from 'graphql';
import { GraphQLDataSource } from './types';
export declare class LocalGraphQLDataSource implements GraphQLDataSource {
    readonly schema: GraphQLSchema;
    constructor(schema: GraphQLSchema);
    process<TContext>({ request, context, }: Pick<GraphQLRequestContext<TContext>, 'request' | 'context'>): Promise<GraphQLResponse>;
    sdl(): DocumentNode;
}
//# sourceMappingURL=LocalGraphQLDataSource.d.ts.map