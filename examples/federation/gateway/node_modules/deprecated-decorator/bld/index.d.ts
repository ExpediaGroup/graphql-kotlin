export declare type DeprecatedDecorator = ClassDecorator & PropertyDecorator;
export interface DeprecatedOptions {
    alternative?: string;
    version?: string;
    url?: string;
}
export declare function deprecated(options?: DeprecatedOptions): DeprecatedDecorator;
export declare function deprecated(alternative?: string, version?: string, url?: string): DeprecatedDecorator;
export declare function deprecated<T extends Function>(fn: T): T;
export declare function deprecated<T extends Function>(options: DeprecatedOptions, fn: T): T;
export declare function deprecated<T extends Function>(alternative: string, fn: T): T;
export declare function deprecated<T extends Function>(alternative: string, version: string, fn: T): T;
export declare function deprecated<T extends Function>(alternative: string, version: string, url: string, fn: T): T;
export default deprecated;
