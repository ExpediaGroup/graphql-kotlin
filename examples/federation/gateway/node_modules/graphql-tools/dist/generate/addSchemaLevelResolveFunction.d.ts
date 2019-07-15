import { GraphQLSchema, GraphQLFieldResolver } from 'graphql';
declare function addSchemaLevelResolveFunction(schema: GraphQLSchema, fn: GraphQLFieldResolver<any, any>): void;
export default addSchemaLevelResolveFunction;
