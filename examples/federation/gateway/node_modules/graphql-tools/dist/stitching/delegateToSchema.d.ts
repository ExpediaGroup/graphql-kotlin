import { GraphQLSchema } from 'graphql';
import { IDelegateToSchemaOptions } from '../Interfaces';
export default function delegateToSchema(options: IDelegateToSchemaOptions | GraphQLSchema, ...args: any[]): Promise<any>;
