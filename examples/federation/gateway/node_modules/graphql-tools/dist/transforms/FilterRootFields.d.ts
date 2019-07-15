import { GraphQLField, GraphQLSchema } from 'graphql';
import { Transform } from './transforms';
export declare type RootFilter = (operation: 'Query' | 'Mutation' | 'Subscription', fieldName: string, field: GraphQLField<any, any>) => boolean;
export default class FilterRootFields implements Transform {
    private transformer;
    constructor(filter: RootFilter);
    transformSchema(originalSchema: GraphQLSchema): GraphQLSchema;
}
