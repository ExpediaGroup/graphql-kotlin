"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const graphql_1 = require("graphql");
class GraphQLExtension {
}
exports.GraphQLExtension = GraphQLExtension;
class GraphQLExtensionStack {
    constructor(extensions) {
        this.extensions = extensions;
    }
    requestDidStart(o) {
        return this.handleDidStart(ext => ext.requestDidStart && ext.requestDidStart(o));
    }
    parsingDidStart(o) {
        return this.handleDidStart(ext => ext.parsingDidStart && ext.parsingDidStart(o));
    }
    validationDidStart() {
        return this.handleDidStart(ext => ext.validationDidStart && ext.validationDidStart());
    }
    executionDidStart(o) {
        if (o.executionArgs.fieldResolver) {
            this.fieldResolver = o.executionArgs.fieldResolver;
        }
        return this.handleDidStart(ext => ext.executionDidStart && ext.executionDidStart(o));
    }
    didEncounterErrors(errors) {
        this.extensions.forEach(extension => {
            if (extension.didEncounterErrors) {
                extension.didEncounterErrors(errors);
            }
        });
    }
    willSendResponse(o) {
        let reference = o;
        [...this.extensions].reverse().forEach(extension => {
            if (extension.willSendResponse) {
                const result = extension.willSendResponse(reference);
                if (result) {
                    reference = result;
                }
            }
        });
        return reference;
    }
    willResolveField(source, args, context, info) {
        const handlers = this.extensions
            .map(extension => extension.willResolveField &&
            extension.willResolveField(source, args, context, info))
            .filter(x => x)
            .reverse();
        return (error, result) => {
            for (const handler of handlers) {
                handler(error, result);
            }
        };
    }
    format() {
        return this.extensions
            .map(extension => extension.format && extension.format())
            .filter(x => x).reduce((extensions, [key, value]) => Object.assign(extensions, { [key]: value }), {});
    }
    handleDidStart(startInvoker) {
        const endHandlers = [];
        this.extensions.forEach(extension => {
            try {
                const endHandler = startInvoker(extension);
                if (endHandler) {
                    endHandlers.push(endHandler);
                }
            }
            catch (error) {
                console.error(error);
            }
        });
        return (...errors) => {
            endHandlers.reverse();
            for (const endHandler of endHandlers) {
                try {
                    endHandler(...errors);
                }
                catch (error) {
                    console.error(error);
                }
            }
        };
    }
}
exports.GraphQLExtensionStack = GraphQLExtensionStack;
function enableGraphQLExtensions(schema) {
    if (schema._extensionsEnabled) {
        return schema;
    }
    schema._extensionsEnabled = true;
    forEachField(schema, wrapField);
    return schema;
}
exports.enableGraphQLExtensions = enableGraphQLExtensions;
function wrapField(field) {
    const fieldResolver = field.resolve;
    field.resolve = (source, args, context, info) => {
        const parentPath = info.path.prev;
        const extensionStack = context && context._extensionStack;
        const handler = (extensionStack &&
            extensionStack.willResolveField(source, args, context, info)) ||
            ((_err, _result) => {
            });
        const resolveObject = info.parentType.resolveObject;
        let whenObjectResolved;
        if (parentPath && resolveObject) {
            if (!parentPath.__fields) {
                parentPath.__fields = {};
            }
            parentPath.__fields[info.fieldName] = info.fieldNodes;
            whenObjectResolved = parentPath.__whenObjectResolved;
            if (!whenObjectResolved) {
                whenObjectResolved = Promise.resolve().then(() => {
                    return resolveObject(source, parentPath.__fields, context, info);
                });
                parentPath.__whenObjectResolved = whenObjectResolved;
            }
        }
        try {
            const actualFieldResolver = fieldResolver ||
                (extensionStack && extensionStack.fieldResolver) ||
                graphql_1.defaultFieldResolver;
            let result;
            if (whenObjectResolved) {
                result = whenObjectResolved.then((resolvedObject) => {
                    return actualFieldResolver(resolvedObject, args, context, info);
                });
            }
            else {
                result = actualFieldResolver(source, args, context, info);
            }
            whenResultIsFinished(result, handler);
            return result;
        }
        catch (error) {
            handler(error);
            throw error;
        }
    };
}
function isPromise(x) {
    return x && typeof x.then === 'function';
}
function whenResultIsFinished(result, callback) {
    if (isPromise(result)) {
        result.then((r) => callback(null, r), (err) => callback(err));
    }
    else if (Array.isArray(result)) {
        if (result.some(isPromise)) {
            Promise.all(result).then((r) => callback(null, r), (err) => callback(err));
        }
        else {
            callback(null, result);
        }
    }
    else {
        callback(null, result);
    }
}
function forEachField(schema, fn) {
    const typeMap = schema.getTypeMap();
    Object.keys(typeMap).forEach(typeName => {
        const type = typeMap[typeName];
        if (!graphql_1.getNamedType(type).name.startsWith('__') &&
            type instanceof graphql_1.GraphQLObjectType) {
            const fields = type.getFields();
            Object.keys(fields).forEach(fieldName => {
                const field = fields[fieldName];
                fn(field, typeName, fieldName);
            });
        }
    });
}
//# sourceMappingURL=index.js.map