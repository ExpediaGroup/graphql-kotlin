"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const graphql_1 = require("graphql");
var CacheScope;
(function (CacheScope) {
    CacheScope["Public"] = "PUBLIC";
    CacheScope["Private"] = "PRIVATE";
})(CacheScope = exports.CacheScope || (exports.CacheScope = {}));
class CacheControlExtension {
    constructor(options = {}) {
        this.options = options;
        this.hints = new Map();
        this.defaultMaxAge = options.defaultMaxAge || 0;
    }
    willResolveField(_source, _args, _context, info) {
        let hint = {};
        const targetType = graphql_1.getNamedType(info.returnType);
        if (targetType instanceof graphql_1.GraphQLObjectType ||
            targetType instanceof graphql_1.GraphQLInterfaceType) {
            if (targetType.astNode) {
                hint = mergeHints(hint, cacheHintFromDirectives(targetType.astNode.directives));
            }
        }
        const fieldDef = info.parentType.getFields()[info.fieldName];
        if (fieldDef.astNode) {
            hint = mergeHints(hint, cacheHintFromDirectives(fieldDef.astNode.directives));
        }
        if ((targetType instanceof graphql_1.GraphQLObjectType ||
            targetType instanceof graphql_1.GraphQLInterfaceType ||
            !info.path.prev) &&
            hint.maxAge === undefined) {
            hint.maxAge = this.defaultMaxAge;
        }
        if (hint.maxAge !== undefined || hint.scope !== undefined) {
            this.addHint(info.path, hint);
        }
        info.cacheControl = {
            setCacheHint: (hint) => {
                this.addHint(info.path, hint);
            },
            cacheHint: hint,
        };
    }
    addHint(path, hint) {
        const existingCacheHint = this.hints.get(path);
        if (existingCacheHint) {
            this.hints.set(path, mergeHints(existingCacheHint, hint));
        }
        else {
            this.hints.set(path, hint);
        }
    }
    format() {
        if (this.options.stripFormattedExtensions !== false)
            return;
        return [
            'cacheControl',
            {
                version: 1,
                hints: Array.from(this.hints).map(([path, hint]) => (Object.assign({ path: [...graphql_1.responsePathAsArray(path)] }, hint))),
            },
        ];
    }
    willSendResponse(o) {
        if (!this.options.calculateHttpHeaders ||
            !o.graphqlResponse.http ||
            o.graphqlResponse.errors) {
            return;
        }
        const overallCachePolicy = this.computeOverallCachePolicy();
        if (overallCachePolicy) {
            o.graphqlResponse.http.headers.set('Cache-Control', `max-age=${overallCachePolicy.maxAge}, ${overallCachePolicy.scope.toLowerCase()}`);
        }
    }
    overrideOverallCachePolicy(overallCachePolicy) {
        this.overallCachePolicyOverride = overallCachePolicy;
    }
    computeOverallCachePolicy() {
        if (this.overallCachePolicyOverride) {
            return this.overallCachePolicyOverride;
        }
        let lowestMaxAge = undefined;
        let scope = CacheScope.Public;
        for (const hint of this.hints.values()) {
            if (hint.maxAge !== undefined) {
                lowestMaxAge =
                    lowestMaxAge !== undefined
                        ? Math.min(lowestMaxAge, hint.maxAge)
                        : hint.maxAge;
            }
            if (hint.scope === CacheScope.Private) {
                scope = CacheScope.Private;
            }
        }
        return lowestMaxAge
            ? {
                maxAge: lowestMaxAge,
                scope,
            }
            : undefined;
    }
}
exports.CacheControlExtension = CacheControlExtension;
function cacheHintFromDirectives(directives) {
    if (!directives)
        return undefined;
    const cacheControlDirective = directives.find(directive => directive.name.value === 'cacheControl');
    if (!cacheControlDirective)
        return undefined;
    if (!cacheControlDirective.arguments)
        return undefined;
    const maxAgeArgument = cacheControlDirective.arguments.find(argument => argument.name.value === 'maxAge');
    const scopeArgument = cacheControlDirective.arguments.find(argument => argument.name.value === 'scope');
    return {
        maxAge: maxAgeArgument &&
            maxAgeArgument.value &&
            maxAgeArgument.value.kind === 'IntValue'
            ? parseInt(maxAgeArgument.value.value)
            : undefined,
        scope: scopeArgument &&
            scopeArgument.value &&
            scopeArgument.value.kind === 'EnumValue'
            ? scopeArgument.value.value
            : undefined,
    };
}
function mergeHints(hint, otherHint) {
    if (!otherHint)
        return hint;
    return {
        maxAge: otherHint.maxAge !== undefined ? otherHint.maxAge : hint.maxAge,
        scope: otherHint.scope || hint.scope,
    };
}
//# sourceMappingURL=index.js.map