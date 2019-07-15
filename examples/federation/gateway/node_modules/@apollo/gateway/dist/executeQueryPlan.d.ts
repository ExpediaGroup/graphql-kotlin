import { GraphQLExecutionResult, GraphQLRequestContext } from 'apollo-server-types';
import { GraphQLFieldResolver } from 'graphql';
import { GraphQLDataSource } from './datasources/types';
import { QueryPlan, OperationContext } from './QueryPlan';
export declare type ServiceMap = {
    [serviceName: string]: GraphQLDataSource;
};
export declare function executeQueryPlan<TContext>(queryPlan: QueryPlan, serviceMap: ServiceMap, requestContext: GraphQLRequestContext<TContext>, operationContext: OperationContext): Promise<GraphQLExecutionResult>;
export declare const defaultFieldResolverWithAliasSupport: GraphQLFieldResolver<any, any>;
//# sourceMappingURL=executeQueryPlan.d.ts.map