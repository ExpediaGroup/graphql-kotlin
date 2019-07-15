import { GraphQLArgument, GraphQLDirective, GraphQLEnumType, GraphQLEnumValue, GraphQLField, GraphQLInputField, GraphQLInputObjectType, GraphQLInterfaceType, GraphQLNamedType, GraphQLObjectType, GraphQLScalarType, GraphQLSchema, GraphQLUnionType } from 'graphql';
export declare type VisitableSchemaType = GraphQLSchema | GraphQLObjectType | GraphQLInterfaceType | GraphQLInputObjectType | GraphQLNamedType | GraphQLScalarType | GraphQLField<any, any> | GraphQLArgument | GraphQLUnionType | GraphQLEnumType | GraphQLEnumValue;
export declare abstract class SchemaVisitor {
    schema: GraphQLSchema;
    static implementsVisitorMethod(methodName: string): boolean;
    visitSchema(schema: GraphQLSchema): void;
    visitScalar(scalar: GraphQLScalarType): GraphQLScalarType | void | null;
    visitObject(object: GraphQLObjectType): GraphQLObjectType | void | null;
    visitFieldDefinition(field: GraphQLField<any, any>, details: {
        objectType: GraphQLObjectType | GraphQLInterfaceType;
    }): GraphQLField<any, any> | void | null;
    visitArgumentDefinition(argument: GraphQLArgument, details: {
        field: GraphQLField<any, any>;
        objectType: GraphQLObjectType | GraphQLInterfaceType;
    }): GraphQLArgument | void | null;
    visitInterface(iface: GraphQLInterfaceType): GraphQLInterfaceType | void | null;
    visitUnion(union: GraphQLUnionType): GraphQLUnionType | void | null;
    visitEnum(type: GraphQLEnumType): GraphQLEnumType | void | null;
    visitEnumValue(value: GraphQLEnumValue, details: {
        enumType: GraphQLEnumType;
    }): GraphQLEnumValue | void | null;
    visitInputObject(object: GraphQLInputObjectType): GraphQLInputObjectType | void | null;
    visitInputFieldDefinition(field: GraphQLInputField, details: {
        objectType: GraphQLInputObjectType;
    }): GraphQLInputField | void | null;
}
export declare function visitSchema(schema: GraphQLSchema, visitorSelector: (type: VisitableSchemaType, methodName: string) => SchemaVisitor[]): GraphQLSchema;
export declare function healSchema(schema: GraphQLSchema): GraphQLSchema;
export declare class SchemaDirectiveVisitor extends SchemaVisitor {
    name: string;
    args: {
        [name: string]: any;
    };
    visitedType: VisitableSchemaType;
    context: {
        [key: string]: any;
    };
    static getDirectiveDeclaration(directiveName: string, schema: GraphQLSchema): GraphQLDirective;
    static visitSchemaDirectives(schema: GraphQLSchema, directiveVisitors: {
        [directiveName: string]: typeof SchemaDirectiveVisitor;
    }, context?: {
        [key: string]: any;
    }): {
        [directiveName: string]: SchemaDirectiveVisitor[];
    };
    protected static getDeclaredDirectives(schema: GraphQLSchema, directiveVisitors: {
        [directiveName: string]: typeof SchemaDirectiveVisitor;
    }): {
        [directiveName: string]: GraphQLDirective;
    };
    protected constructor(config: {
        name: string;
        args: {
            [name: string]: any;
        };
        visitedType: VisitableSchemaType;
        schema: GraphQLSchema;
        context: {
            [key: string]: any;
        };
    });
}
