import { Observable } from 'apollo-link';
export declare function observableToAsyncIterable<T>(observable: Observable<T>): AsyncIterator<T>;
