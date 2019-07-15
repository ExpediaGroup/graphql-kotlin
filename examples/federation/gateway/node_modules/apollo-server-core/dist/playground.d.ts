import { CursorShape, RenderPageOptions as PlaygroundRenderPageOptions, Theme } from '@apollographql/graphql-playground-html/dist/render-playground-page';
export { RenderPageOptions as PlaygroundRenderPageOptions, } from '@apollographql/graphql-playground-html/dist/render-playground-page';
declare type RecursivePartial<T> = {
    [P in keyof T]?: T[P] extends (infer U)[] ? RecursivePartial<U>[] : T[P] extends (object | undefined) ? RecursivePartial<T[P]> : T[P];
};
export declare type PlaygroundConfig = RecursivePartial<PlaygroundRenderPageOptions> | boolean;
export declare const defaultPlaygroundOptions: {
    version: string;
    settings: {
        'general.betaUpdates': boolean;
        'editor.theme': Theme;
        'editor.cursorShape': CursorShape;
        'editor.reuseHeaders': boolean;
        'tracing.hideTracingResponse': boolean;
        'queryPlan.hideQueryPlanResponse': boolean;
        'editor.fontSize': number;
        'editor.fontFamily': string;
        'request.credentials': string;
    };
};
export declare function createPlaygroundOptions(playground?: PlaygroundConfig): PlaygroundRenderPageOptions | undefined;
//# sourceMappingURL=playground.d.ts.map