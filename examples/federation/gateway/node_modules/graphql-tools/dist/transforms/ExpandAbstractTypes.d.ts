import { GraphQLSchema } from 'graphql';
import { Transform, Request } from '../Interfaces';
export default class ExpandAbstractTypes implements Transform {
    private targetSchema;
    private mapping;
    private reverseMapping;
    constructor(transformedSchema: GraphQLSchema, targetSchema: GraphQLSchema);
    transformRequest(originalRequest: Request): Request;
}
