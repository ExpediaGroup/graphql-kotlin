import { GraphQLSchema } from 'graphql';
import { Request } from '../Interfaces';
import { Transform } from './transforms';
export default class AddTypenameToAbstract implements Transform {
    private targetSchema;
    constructor(targetSchema: GraphQLSchema);
    transformRequest(originalRequest: Request): Request;
}
