import { GraphQLSchema } from 'graphql';
import { IFieldIteratorFn } from '../Interfaces';
declare function forEachField(schema: GraphQLSchema, fn: IFieldIteratorFn): void;
export default forEachField;
