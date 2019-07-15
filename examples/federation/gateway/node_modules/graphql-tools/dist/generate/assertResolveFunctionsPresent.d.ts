import { GraphQLSchema } from 'graphql';
import { IResolverValidationOptions } from '../Interfaces';
declare function assertResolveFunctionsPresent(schema: GraphQLSchema, resolverValidationOptions?: IResolverValidationOptions): void;
export default assertResolveFunctionsPresent;
