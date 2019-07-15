import express from 'express';
import { GraphQLOptions } from 'apollo-server-core';
import { ValueOrPromise } from 'apollo-server-types';
export interface ExpressGraphQLOptionsFunction {
    (req: express.Request, res: express.Response): ValueOrPromise<GraphQLOptions>;
}
export declare function graphqlExpress(options: GraphQLOptions | ExpressGraphQLOptionsFunction): express.Handler;
//# sourceMappingURL=expressApollo.d.ts.map