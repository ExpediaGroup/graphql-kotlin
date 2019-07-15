/// <reference types="node" />
import express from 'express';
import http from 'http';
import { ApolloServer as ApolloServerBase, CorsOptions, ApolloServerExpressConfig } from 'apollo-server-express';
export * from './exports';
export interface ServerInfo {
    address: string;
    family: string;
    url: string;
    subscriptionsUrl: string;
    port: number | string;
    subscriptionsPath: string;
    server: http.Server;
}
export declare class ApolloServer extends ApolloServerBase {
    private httpServer?;
    private cors?;
    private onHealthCheck?;
    constructor(config: ApolloServerExpressConfig & {
        cors?: CorsOptions | boolean;
        onHealthCheck?: (req: express.Request) => Promise<any>;
    });
    private createServerInfo;
    applyMiddleware(): void;
    listen(...opts: Array<any>): Promise<ServerInfo>;
    stop(): Promise<void>;
}
//# sourceMappingURL=index.d.ts.map