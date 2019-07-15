export declare function compactMap<T, U>(array: T[], callbackfn: (value: T, index: number, array: T[]) => U | null | undefined): U[];
export declare function partition<T>(array: T[], predicate: (element: T, index: number, array: T[]) => boolean): [T[], T[]];
export declare function findAndExtract<T>(array: T[], predicate: (element: T, index: number, array: T[]) => boolean): [T | undefined, T[]];
//# sourceMappingURL=array.d.ts.map