import { Transform, Request } from '../Interfaces';
export default class ExtractField implements Transform {
    private from;
    private to;
    constructor({ from, to }: {
        from: Array<string>;
        to: Array<string>;
    });
    transformRequest(originalRequest: Request): Request;
}
