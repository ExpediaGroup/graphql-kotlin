import { GraphQLResolveInfo } from 'graphql';
import { Transform } from './transforms';
export default class CheckResultAndHandleErrors implements Transform {
    private info;
    private fieldName?;
    constructor(info: GraphQLResolveInfo, fieldName?: string);
    transformResult(result: any): any;
}
