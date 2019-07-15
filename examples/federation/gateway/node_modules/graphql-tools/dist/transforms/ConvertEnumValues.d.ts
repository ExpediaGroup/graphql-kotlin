import { GraphQLSchema } from 'graphql';
import { Transform } from '../transforms/transforms';
export default class ConvertEnumValues implements Transform {
    private enumValueMap;
    constructor(enumValueMap: object);
    transformSchema(schema: GraphQLSchema): GraphQLSchema;
}
