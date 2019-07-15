import { GraphQLSchema, GraphQLField, GraphQLFieldConfig } from 'graphql';
import { Transform } from './transforms';
export declare type RootTransformer = (operation: 'Query' | 'Mutation' | 'Subscription', fieldName: string, field: GraphQLField<any, any>) => GraphQLFieldConfig<any, any> | {
    name: string;
    field: GraphQLFieldConfig<any, any>;
} | null | undefined;
export default class TransformRootFields implements Transform {
    private transform;
    constructor(transform: RootTransformer);
    transformSchema(originalSchema: GraphQLSchema): GraphQLSchema;
}
