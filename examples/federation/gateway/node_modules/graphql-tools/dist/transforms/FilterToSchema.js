var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var implementsAbstractType_1 = require("../implementsAbstractType");
var FilterToSchema = /** @class */ (function () {
    function FilterToSchema(targetSchema) {
        this.targetSchema = targetSchema;
    }
    FilterToSchema.prototype.transformRequest = function (originalRequest) {
        var document = filterDocumentToSchema(this.targetSchema, originalRequest.document);
        return __assign({}, originalRequest, { document: document });
    };
    return FilterToSchema;
}());
exports.default = FilterToSchema;
function filterDocumentToSchema(targetSchema, document) {
    var operations = document.definitions.filter(function (def) { return def.kind === graphql_1.Kind.OPERATION_DEFINITION; });
    var fragments = document.definitions.filter(function (def) { return def.kind === graphql_1.Kind.FRAGMENT_DEFINITION; });
    var usedFragments = [];
    var newOperations = [];
    var newFragments = [];
    var validFragments = fragments.filter(function (fragment) {
        var typeName = fragment.typeCondition.name.value;
        return Boolean(targetSchema.getType(typeName));
    });
    var validFragmentsWithType = {};
    validFragments.forEach(function (fragment) {
        var typeName = fragment.typeCondition.name.value;
        var type = targetSchema.getType(typeName);
        validFragmentsWithType[fragment.name.value] = type;
    });
    var fragmentSet = Object.create(null);
    operations.forEach(function (operation) {
        var type;
        if (operation.operation === 'subscription') {
            type = targetSchema.getSubscriptionType();
        }
        else if (operation.operation === 'mutation') {
            type = targetSchema.getMutationType();
        }
        else {
            type = targetSchema.getQueryType();
        }
        var _a = filterSelectionSet(targetSchema, type, validFragmentsWithType, operation.selectionSet), selectionSet = _a.selectionSet, operationUsedFragments = _a.usedFragments, operationUsedVariables = _a.usedVariables;
        usedFragments = union(usedFragments, operationUsedFragments);
        var _b = collectFragmentVariables(targetSchema, fragmentSet, validFragments, validFragmentsWithType, usedFragments), collectedUsedVariables = _b.usedVariables, collectedNewFragments = _b.newFragments, collectedFragmentSet = _b.fragmentSet;
        var fullUsedVariables = union(operationUsedVariables, collectedUsedVariables);
        newFragments = collectedNewFragments;
        fragmentSet = collectedFragmentSet;
        var variableDefinitions = operation.variableDefinitions.filter(function (variable) {
            return fullUsedVariables.indexOf(variable.variable.name.value) !== -1;
        });
        newOperations.push({
            kind: graphql_1.Kind.OPERATION_DEFINITION,
            operation: operation.operation,
            name: operation.name,
            directives: operation.directives,
            variableDefinitions: variableDefinitions,
            selectionSet: selectionSet,
        });
    });
    return {
        kind: graphql_1.Kind.DOCUMENT,
        definitions: newOperations.concat(newFragments),
    };
}
function collectFragmentVariables(targetSchema, fragmentSet, validFragments, validFragmentsWithType, usedFragments) {
    var usedVariables = [];
    var newFragments = [];
    var _loop_1 = function () {
        var nextFragmentName = usedFragments.pop();
        var fragment = validFragments.find(function (fr) { return fr.name.value === nextFragmentName; });
        if (fragment) {
            var name_1 = nextFragmentName;
            var typeName = fragment.typeCondition.name.value;
            var type = targetSchema.getType(typeName);
            var _a = filterSelectionSet(targetSchema, type, validFragmentsWithType, fragment.selectionSet), selectionSet = _a.selectionSet, fragmentUsedFragments = _a.usedFragments, fragmentUsedVariables = _a.usedVariables;
            usedFragments = union(usedFragments, fragmentUsedFragments);
            usedVariables = union(usedVariables, fragmentUsedVariables);
            if (!fragmentSet[name_1]) {
                fragmentSet[name_1] = true;
                newFragments.push({
                    kind: graphql_1.Kind.FRAGMENT_DEFINITION,
                    name: {
                        kind: graphql_1.Kind.NAME,
                        value: name_1,
                    },
                    typeCondition: fragment.typeCondition,
                    selectionSet: selectionSet,
                });
            }
        }
    };
    while (usedFragments.length !== 0) {
        _loop_1();
    }
    return {
        usedVariables: usedVariables,
        newFragments: newFragments,
        fragmentSet: fragmentSet,
    };
}
function filterSelectionSet(schema, type, validFragments, selectionSet) {
    var _a;
    var usedFragments = [];
    var usedVariables = [];
    var typeStack = [type];
    // Should be rewritten using visitWithSchema
    var filteredSelectionSet = graphql_1.visit(selectionSet, (_a = {},
        _a[graphql_1.Kind.FIELD] = {
            enter: function (node) {
                var parentType = resolveType(typeStack[typeStack.length - 1]);
                if (parentType instanceof graphql_1.GraphQLObjectType ||
                    parentType instanceof graphql_1.GraphQLInterfaceType) {
                    var fields = parentType.getFields();
                    var field = node.name.value === '__typename'
                        ? graphql_1.TypeNameMetaFieldDef
                        : fields[node.name.value];
                    if (!field) {
                        return null;
                    }
                    else {
                        typeStack.push(field.type);
                    }
                    var argNames_1 = (field.args || []).map(function (arg) { return arg.name; });
                    if (node.arguments) {
                        var args = node.arguments.filter(function (arg) {
                            return argNames_1.indexOf(arg.name.value) !== -1;
                        });
                        if (args.length !== node.arguments.length) {
                            return __assign({}, node, { arguments: args });
                        }
                    }
                }
                else if (parentType instanceof graphql_1.GraphQLUnionType &&
                    node.name.value === '__typename') {
                    typeStack.push(graphql_1.TypeNameMetaFieldDef.type);
                }
            },
            leave: function (node) {
                var _a;
                var currentType = typeStack.pop();
                var resolvedType = resolveType(currentType);
                if (resolvedType instanceof graphql_1.GraphQLObjectType ||
                    resolvedType instanceof graphql_1.GraphQLInterfaceType) {
                    var selections = node.selectionSet && node.selectionSet.selections || null;
                    if (!selections || selections.length === 0) {
                        // need to remove any added variables. Is there a better way to do this?
                        graphql_1.visit(node, (_a = {},
                            _a[graphql_1.Kind.VARIABLE] = function (variableNode) {
                                var index = usedVariables.indexOf(variableNode.name.value);
                                if (index !== -1) {
                                    usedVariables.splice(index, 1);
                                }
                            },
                            _a));
                        return null;
                    }
                }
            },
        },
        _a[graphql_1.Kind.FRAGMENT_SPREAD] = function (node) {
            if (node.name.value in validFragments) {
                var parentType = resolveType(typeStack[typeStack.length - 1]);
                var innerType = validFragments[node.name.value];
                if (!implementsAbstractType_1.default(schema, parentType, innerType)) {
                    return null;
                }
                else {
                    usedFragments.push(node.name.value);
                    return;
                }
            }
            else {
                return null;
            }
        },
        _a[graphql_1.Kind.INLINE_FRAGMENT] = {
            enter: function (node) {
                if (node.typeCondition) {
                    var innerType = schema.getType(node.typeCondition.name.value);
                    var parentType = resolveType(typeStack[typeStack.length - 1]);
                    if (implementsAbstractType_1.default(schema, parentType, innerType)) {
                        typeStack.push(innerType);
                    }
                    else {
                        return null;
                    }
                }
            },
            leave: function (node) {
                typeStack.pop();
            },
        },
        _a[graphql_1.Kind.VARIABLE] = function (node) {
            usedVariables.push(node.name.value);
        },
        _a));
    return {
        selectionSet: filteredSelectionSet,
        usedFragments: usedFragments,
        usedVariables: usedVariables,
    };
}
function resolveType(type) {
    var lastType = type;
    while (lastType instanceof graphql_1.GraphQLNonNull ||
        lastType instanceof graphql_1.GraphQLList) {
        lastType = lastType.ofType;
    }
    return lastType;
}
function union() {
    var arrays = [];
    for (var _i = 0; _i < arguments.length; _i++) {
        arrays[_i] = arguments[_i];
    }
    var cache = {};
    var result = [];
    arrays.forEach(function (array) {
        array.forEach(function (item) {
            if (!cache[item]) {
                cache[item] = true;
                result.push(item);
            }
        });
    });
    return result;
}
//# sourceMappingURL=FilterToSchema.js.map