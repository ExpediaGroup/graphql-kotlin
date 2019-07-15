import { SelectionNode, SelectionSetNode } from 'graphql';
import { Transform, Request, Result } from '../Interfaces';
export declare type QueryWrapper = (subtree: SelectionSetNode) => SelectionNode | SelectionSetNode;
export default class WrapQuery implements Transform {
    private wrapper;
    private extractor;
    private path;
    constructor(path: Array<string>, wrapper: QueryWrapper, extractor: (result: any) => any);
    transformRequest(originalRequest: Request): Request;
    transformResult(originalResult: Result): Result;
}
