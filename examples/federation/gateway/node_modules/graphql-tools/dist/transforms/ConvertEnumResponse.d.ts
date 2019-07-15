import { Transform } from './transforms';
import { GraphQLEnumType } from 'graphql';
export default class ConvertEnumResponse implements Transform {
    private enumNode;
    constructor(enumNode: GraphQLEnumType);
    transformResult(result: any): any;
}
