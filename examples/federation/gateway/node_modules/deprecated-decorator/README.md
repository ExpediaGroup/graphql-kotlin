[![NPM Package](https://badge.fury.io/js/deprecated-decorator.svg)](https://www.npmjs.com/package/deprecated-decorator)
[![Build Status](https://travis-ci.org/vilic/deprecated-decorator.svg)](https://travis-ci.org/vilic/deprecated-decorator) 

# Deprecated Decorator

A simple decorator for deprecated properties, methods and classes. It can also wrap normal functions via the old-fashioned way.

Transpilers supported:

- **TypeScript** with `experimentalDecorators` option enabled.
- **Babel** with [transform-decorators-legacy](https://github.com/loganfsmyth/babel-plugin-transform-decorators-legacy) for version 6.x.

## Install

```sh
npm install deprecated-decorator --save
```

## API References

```ts
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
```

## Usage

Decorating a class will enable warning on constructor and static methods (including static getters and setters):

```ts
import deprecated from 'deprecated-decorator';

// alternative, since version, url
@deprecated('Bar', '0.1.0', 'http://vane.life/')
class Foo {
    static method() { }
}
```

Or you can decorate methods respectively:

```ts
import deprecated from 'deprecated-decorator';

class Foo {
    @deprecated('otherMethod')
    method() { }
    
    @deprecated({
        alternative: 'otherProperty',
        version: '0.1.2',
        url: 'http://vane.life/'
    })
    get property() { }
}
```

For functions:

```ts
import deprecated from 'deprecated-decorator';

let foo = deprecated({
    alternative: 'bar',
    version: '0.1.0'
}, function foo() {
    // ...
});
```

## License

MIT License.
