import { GraphQLFieldResolver } from 'graphql';
import { ILogger } from '../Interfaces';
declare function decorateWithLogger(fn: GraphQLFieldResolver<any, any> | undefined, logger: ILogger, hint: string): GraphQLFieldResolver<any, any>;
export default decorateWithLogger;
