import { DocumentNode, GraphQLError } from 'graphql';
import { Trace } from 'apollo-engine-reporting-protobuf';
import { RequestAgent } from 'apollo-server-env';
import { EngineReportingExtension } from './extension';
import { GraphQLRequestContext } from 'apollo-server-types';
export interface ClientInfo {
    clientName?: string;
    clientVersion?: string;
    clientReferenceId?: string;
}
export declare type SendValuesBaseOptions = {
    onlyNames: Array<String>;
} | {
    exceptNames: Array<String>;
} | {
    all: true;
} | {
    none: true;
};
declare type VariableValueTransformOptions = {
    variables: Record<string, any>;
    operationString?: string;
};
export declare type VariableValueOptions = {
    transform: (options: VariableValueTransformOptions) => Record<string, any>;
} | SendValuesBaseOptions;
export declare type GenerateClientInfo<TContext> = (requestContext: GraphQLRequestContext<TContext>) => ClientInfo;
export interface EngineReportingOptions<TContext> {
    apiKey?: string;
    calculateSignature?: (ast: DocumentNode, operationName: string) => string;
    reportIntervalMs?: number;
    maxUncompressedReportSize?: number;
    endpointUrl?: string;
    debugPrintReports?: boolean;
    requestAgent?: RequestAgent | false;
    maxAttempts?: number;
    minimumRetryDelayMs?: number;
    reportErrorFunction?: (err: Error) => void;
    sendVariableValues?: VariableValueOptions;
    privateVariables?: Array<String> | boolean;
    sendHeaders?: SendValuesBaseOptions;
    privateHeaders?: Array<String> | boolean;
    handleSignals?: boolean;
    sendReportsImmediately?: boolean;
    maskErrorDetails?: boolean;
    rewriteError?: (err: GraphQLError) => GraphQLError | null;
    schemaTag?: string;
    generateClientInfo?: GenerateClientInfo<TContext>;
}
export interface AddTraceArgs {
    trace: Trace;
    operationName: string;
    queryHash: string;
    schemaHash: string;
    queryString?: string;
    documentAST?: DocumentNode;
}
export declare class EngineReportingAgent<TContext = any> {
    private options;
    private apiKey;
    private reports;
    private reportSizes;
    private reportTimer;
    private sendReportsImmediately?;
    private stopped;
    private reportHeaders;
    private signatureCache;
    private signalHandlers;
    constructor(options?: EngineReportingOptions<TContext>);
    newExtension(schemaHash: string): EngineReportingExtension<TContext>;
    addTrace({ trace, queryHash, documentAST, operationName, queryString, schemaHash, }: AddTraceArgs): Promise<void>;
    sendAllReports(): Promise<void>;
    sendReport(schemaHash: string): Promise<void>;
    stop(): void;
    private getTraceSignature;
    private sendAllReportsAndReportErrors;
    private sendReportAndReportErrors;
    private resetReport;
}
export declare function signatureCacheKey(queryHash: string, operationName: string): string;
export declare function handleLegacyOptions(options: EngineReportingOptions<any>): void;
export {};
//# sourceMappingURL=agent.d.ts.map