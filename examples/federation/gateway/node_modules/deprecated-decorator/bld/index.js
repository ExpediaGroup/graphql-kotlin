/*
    Deprecated Decorator v0.1
    https://github.com/vilic/deprecated-decorator
*/
"use strict";
/** @internal */
exports.options = {
    getWarner: undefined
};
function createWarner(type, name, alternative, version, url) {
    var warnedPositions = {};
    return function () {
        var stack = (new Error()).stack || '';
        var at = (stack.match(/(?:\s+at\s.+){2}\s+at\s(.+)/) || [undefined, ''])[1];
        if (/\)$/.test(at)) {
            at = at.match(/[^(]+(?=\)$)/)[0];
        }
        else {
            at = at.trim();
        }
        if (at in warnedPositions) {
            return;
        }
        warnedPositions[at] = true;
        var message;
        switch (type) {
            case 'class':
                message = 'Class';
                break;
            case 'property':
                message = 'Property';
                break;
            case 'method':
                message = 'Method';
                break;
            case 'function':
                message = 'Function';
                break;
        }
        message += " `" + name + "` has been deprecated";
        if (version) {
            message += " since version " + version;
        }
        if (alternative) {
            message += ", use `" + alternative + "` instead";
        }
        message += '.';
        if (at) {
            message += "\n    at " + at;
        }
        if (url) {
            message += "\nCheck out " + url + " for more information.";
        }
        console.warn(message);
    };
}
function decorateProperty(type, name, descriptor, alternative, version, url) {
    var warner = (exports.options.getWarner || createWarner)(type, name, alternative, version, url);
    descriptor = descriptor || {
        writable: true,
        enumerable: false,
        configurable: true
    };
    var deprecatedDescriptor = {
        enumerable: descriptor.enumerable,
        configurable: descriptor.configurable
    };
    if (descriptor.get || descriptor.set) {
        if (descriptor.get) {
            deprecatedDescriptor.get = function () {
                warner();
                return descriptor.get.call(this);
            };
        }
        if (descriptor.set) {
            deprecatedDescriptor.set = function (value) {
                warner();
                return descriptor.set.call(this, value);
            };
        }
    }
    else {
        var propertyValue_1 = descriptor.value;
        deprecatedDescriptor.get = function () {
            warner();
            return propertyValue_1;
        };
        if (descriptor.writable) {
            deprecatedDescriptor.set = function (value) {
                warner();
                propertyValue_1 = value;
            };
        }
    }
    return deprecatedDescriptor;
}
function decorateFunction(type, target, alternative, version, url) {
    var name = target.name;
    var warner = (exports.options.getWarner || createWarner)(type, name, alternative, version, url);
    var fn = function () {
        warner();
        return target.apply(this, arguments);
    };
    for (var _i = 0, _a = Object.getOwnPropertyNames(target); _i < _a.length; _i++) {
        var propertyName = _a[_i];
        var descriptor = Object.getOwnPropertyDescriptor(target, propertyName);
        if (descriptor.writable) {
            fn[propertyName] = target[propertyName];
        }
        else if (descriptor.configurable) {
            Object.defineProperty(fn, propertyName, descriptor);
        }
    }
    return fn;
}
function deprecated() {
    var args = [];
    for (var _i = 0; _i < arguments.length; _i++) {
        args[_i - 0] = arguments[_i];
    }
    var fn = args[args.length - 1];
    if (typeof fn === 'function') {
        fn = args.pop();
    }
    else {
        fn = undefined;
    }
    var options = args[0];
    var alternative;
    var version;
    var url;
    if (typeof options === 'string') {
        alternative = options;
        version = args[1];
        url = args[2];
    }
    else if (options) {
        (alternative = options.alternative, version = options.version, url = options.url, options);
    }
    if (fn) {
        return decorateFunction('function', fn, alternative, version, url);
    }
    return function (target, name, descriptor) {
        if (typeof name === 'string') {
            var type = descriptor && typeof descriptor.value === 'function' ?
                'method' : 'property';
            return decorateProperty(type, name, descriptor, alternative, version, url);
        }
        else if (typeof target === 'function') {
            var constructor = decorateFunction('class', target, alternative, version, url);
            var className = target.name;
            for (var _i = 0, _a = Object.getOwnPropertyNames(constructor); _i < _a.length; _i++) {
                var propertyName = _a[_i];
                var descriptor_1 = Object.getOwnPropertyDescriptor(constructor, propertyName);
                descriptor_1 = decorateProperty('class', className, descriptor_1, alternative, version, url);
                if (descriptor_1.writable) {
                    constructor[propertyName] = target[propertyName];
                }
                else if (descriptor_1.configurable) {
                    Object.defineProperty(constructor, propertyName, descriptor_1);
                }
            }
            return constructor;
        }
    };
}
exports.deprecated = deprecated;
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = deprecated;
//# sourceMappingURL=index.js.map