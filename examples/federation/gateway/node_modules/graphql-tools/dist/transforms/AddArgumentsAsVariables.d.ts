import { GraphQLSchema } from 'graphql';
import { Request } from '../Interfaces';
import { Transform } from './transforms';
export default class AddArgumentsAsVariablesTransform implements Transform {
    private schema;
    private args;
    constructor(schema: GraphQLSchema, args: {
        [key: string]: any;
    });
    transformRequest(originalRequest: Request): Request;
}
