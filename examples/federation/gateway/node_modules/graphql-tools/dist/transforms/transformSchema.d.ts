import { GraphQLSchema } from 'graphql';
import { Transform } from '../transforms/transforms';
export default function transformSchema(targetSchema: GraphQLSchema, transforms: Array<Transform>): GraphQLSchema & {
    transforms: Array<Transform>;
};
