declare type AnyFunction = (...args: any[]) => any;
declare type Args<F> = F extends (...args: infer A) => any ? A : never;
declare type FunctionPropertyNames<T, F extends AnyFunction = AnyFunction> = {
    [K in keyof T]: T[K] extends F ? K : never;
}[keyof T];
declare type AsFunction<F> = F extends AnyFunction ? F : never;
declare type UnwrapPromise<T> = T extends Promise<infer U> ? U : T;
declare type DidEndHook<TArgs extends any[]> = (...args: TArgs) => void;
export declare class Dispatcher<T> {
    protected targets: T[];
    constructor(targets: T[]);
    invokeHookAsync<TMethodName extends FunctionPropertyNames<Required<T>>>(methodName: TMethodName, ...args: Args<T[TMethodName]>): Promise<UnwrapPromise<ReturnType<AsFunction<T[TMethodName]>>>[]>;
    invokeHooksUntilNonNull<TMethodName extends FunctionPropertyNames<Required<T>>>(methodName: TMethodName, ...args: Args<T[TMethodName]>): Promise<UnwrapPromise<ReturnType<AsFunction<T[TMethodName]>>> | null>;
    invokeDidStartHook<TMethodName extends FunctionPropertyNames<Required<T>, (...args: any[]) => AnyFunction | void>, TEndHookArgs extends Args<ReturnType<AsFunction<T[TMethodName]>>>>(methodName: TMethodName, ...args: Args<T[TMethodName]>): DidEndHook<TEndHookArgs>;
}
export {};
//# sourceMappingURL=dispatcher.d.ts.map