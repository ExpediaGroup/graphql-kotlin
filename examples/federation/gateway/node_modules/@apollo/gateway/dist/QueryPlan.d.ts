import { FragmentDefinitionNode, GraphQLSchema, OperationDefinitionNode, SelectionSetNode, VariableDefinitionNode } from 'graphql';
export declare type ResponsePath = (string | number)[];
export declare type FragmentMap = {
    [fragmentName: string]: FragmentDefinitionNode;
};
export interface QueryPlan {
    kind: 'QueryPlan';
    node?: PlanNode;
}
export declare type OperationContext = {
    schema: GraphQLSchema;
    operation: OperationDefinitionNode;
    fragments: FragmentMap;
};
export declare type PlanNode = SequenceNode | ParallelNode | FetchNode | FlattenNode;
export interface SequenceNode {
    kind: 'Sequence';
    nodes: PlanNode[];
}
export interface ParallelNode {
    kind: 'Parallel';
    nodes: PlanNode[];
}
export interface FetchNode {
    kind: 'Fetch';
    serviceName: string;
    selectionSet: SelectionSetNode;
    variableUsages?: {
        [name: string]: VariableDefinitionNode;
    };
    requires?: SelectionSetNode;
}
export interface FlattenNode {
    kind: 'Flatten';
    path: ResponsePath;
    node: PlanNode;
}
export declare function serializeQueryPlan(queryPlan: QueryPlan): string;
//# sourceMappingURL=QueryPlan.d.ts.map