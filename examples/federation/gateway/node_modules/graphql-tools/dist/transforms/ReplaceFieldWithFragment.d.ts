import { GraphQLSchema } from 'graphql';
import { Request } from '../Interfaces';
import { Transform } from './transforms';
export default class ReplaceFieldWithFragment implements Transform {
    private targetSchema;
    private mapping;
    constructor(targetSchema: GraphQLSchema, fragments: Array<{
        field: string;
        fragment: string;
    }>);
    transformRequest(originalRequest: Request): Request;
}
