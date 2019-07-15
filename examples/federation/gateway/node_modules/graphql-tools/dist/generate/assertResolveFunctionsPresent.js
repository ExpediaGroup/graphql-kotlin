Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var _1 = require(".");
function assertResolveFunctionsPresent(schema, resolverValidationOptions) {
    if (resolverValidationOptions === void 0) { resolverValidationOptions = {}; }
    var _a = resolverValidationOptions.requireResolversForArgs, requireResolversForArgs = _a === void 0 ? false : _a, _b = resolverValidationOptions.requireResolversForNonScalar, requireResolversForNonScalar = _b === void 0 ? false : _b, _c = resolverValidationOptions.requireResolversForAllFields, requireResolversForAllFields = _c === void 0 ? false : _c;
    if (requireResolversForAllFields &&
        (requireResolversForArgs || requireResolversForNonScalar)) {
        throw new TypeError('requireResolversForAllFields takes precedence over the more specific assertions. ' +
            'Please configure either requireResolversForAllFields or requireResolversForArgs / ' +
            'requireResolversForNonScalar, but not a combination of them.');
    }
    _1.forEachField(schema, function (field, typeName, fieldName) {
        // requires a resolve function for *every* field.
        if (requireResolversForAllFields) {
            expectResolveFunction(field, typeName, fieldName);
        }
        // requires a resolve function on every field that has arguments
        if (requireResolversForArgs && field.args.length > 0) {
            expectResolveFunction(field, typeName, fieldName);
        }
        // requires a resolve function on every field that returns a non-scalar type
        if (requireResolversForNonScalar &&
            !(graphql_1.getNamedType(field.type) instanceof graphql_1.GraphQLScalarType)) {
            expectResolveFunction(field, typeName, fieldName);
        }
    });
}
function expectResolveFunction(field, typeName, fieldName) {
    if (!field.resolve) {
        console.warn(
        // tslint:disable-next-line: max-line-length
        "Resolve function missing for \"" + typeName + "." + fieldName + "\". To disable this warning check https://github.com/apollostack/graphql-tools/issues/131");
        return;
    }
    if (typeof field.resolve !== 'function') {
        throw new _1.SchemaError("Resolver \"" + typeName + "." + fieldName + "\" must be a function");
    }
}
exports.default = assertResolveFunctionsPresent;
//# sourceMappingURL=assertResolveFunctionsPresent.js.map