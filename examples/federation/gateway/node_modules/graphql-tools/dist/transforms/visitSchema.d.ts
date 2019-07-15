import { GraphQLSchema, GraphQLType, GraphQLNamedType } from 'graphql';
export declare enum VisitSchemaKind {
    TYPE = "VisitSchemaKind.TYPE",
    SCALAR_TYPE = "VisitSchemaKind.SCALAR_TYPE",
    ENUM_TYPE = "VisitSchemaKind.ENUM_TYPE",
    COMPOSITE_TYPE = "VisitSchemaKind.COMPOSITE_TYPE",
    OBJECT_TYPE = "VisitSchemaKind.OBJECT_TYPE",
    INPUT_OBJECT_TYPE = "VisitSchemaKind.INPUT_OBJECT_TYPE",
    ABSTRACT_TYPE = "VisitSchemaKind.ABSTRACT_TYPE",
    UNION_TYPE = "VisitSchemaKind.UNION_TYPE",
    INTERFACE_TYPE = "VisitSchemaKind.INTERFACE_TYPE",
    ROOT_OBJECT = "VisitSchemaKind.ROOT_OBJECT",
    QUERY = "VisitSchemaKind.QUERY",
    MUTATION = "VisitSchemaKind.MUTATION",
    SUBSCRIPTION = "VisitSchemaKind.SUBSCRIPTION"
}
export declare type SchemaVisitor = {
    [key: string]: TypeVisitor;
};
export declare type TypeVisitor = (type: GraphQLType, schema: GraphQLSchema) => GraphQLNamedType;
export declare function visitSchema(schema: GraphQLSchema, visitor: SchemaVisitor, stripResolvers?: boolean): GraphQLSchema;
