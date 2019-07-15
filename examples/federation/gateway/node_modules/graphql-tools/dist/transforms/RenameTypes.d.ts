import { GraphQLSchema } from 'graphql';
import { Request, Result } from '../Interfaces';
import { Transform } from '../transforms/transforms';
export declare type RenameOptions = {
    renameBuiltins: boolean;
    renameScalars: boolean;
};
export default class RenameTypes implements Transform {
    private renamer;
    private reverseMap;
    private renameBuiltins;
    private renameScalars;
    constructor(renamer: (name: string) => string | undefined, options?: RenameOptions);
    transformSchema(originalSchema: GraphQLSchema): GraphQLSchema;
    transformRequest(originalRequest: Request): Request;
    transformResult(result: Result): Result;
    private renameTypes;
}
