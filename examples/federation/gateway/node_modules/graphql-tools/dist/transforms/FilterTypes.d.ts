import { GraphQLSchema, GraphQLNamedType } from 'graphql';
import { Transform } from '../transforms/transforms';
export default class FilterTypes implements Transform {
    private filter;
    constructor(filter: (type: GraphQLNamedType) => boolean);
    transformSchema(schema: GraphQLSchema): GraphQLSchema;
}
