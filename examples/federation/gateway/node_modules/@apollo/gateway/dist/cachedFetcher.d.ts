export interface CachedFetcherOptions {
    pollInterval?: number;
}
export declare class CachedFetcher {
    private logPrefix;
    private mapUrlToLastSuccessfulETag;
    private mapUrlToCachedResult;
    fetch(url: string): Promise<{
        isCacheHit: boolean;
        result: any;
    }>;
    getCache(): {
        [url: string]: any;
    };
}
//# sourceMappingURL=cachedFetcher.d.ts.map