import { GraphQLSchema } from 'graphql';
import { IDirectiveResolvers } from '../Interfaces';
declare function attachDirectiveResolvers(schema: GraphQLSchema, directiveResolvers: IDirectiveResolvers<any, any>): void;
export default attachDirectiveResolvers;
