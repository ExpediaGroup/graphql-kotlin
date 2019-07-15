import { FieldNode, GraphQLCompositeType, GraphQLField, SelectionSetNode } from 'graphql';
export interface Field<TParent = GraphQLCompositeType> {
    parentType: TParent;
    fieldNode: FieldNode;
    fieldDef: GraphQLField<any, any>;
}
export declare type FieldSet = Field[];
export declare function printFields(fields?: FieldSet): string;
export declare function matchesField(field: Field): (otherField: Field<GraphQLCompositeType>) => boolean;
export declare const groupByResponseName: (iterable: Iterable<Field<GraphQLCompositeType>>) => Map<string, Field<GraphQLCompositeType>[]>;
export declare const groupByParentType: (iterable: Iterable<Field<GraphQLCompositeType>>) => Map<GraphQLCompositeType, Field<GraphQLCompositeType>[]>;
export declare function selectionSetFromFieldSet(fields: FieldSet, parentType?: GraphQLCompositeType): SelectionSetNode;
//# sourceMappingURL=FieldSet.d.ts.map