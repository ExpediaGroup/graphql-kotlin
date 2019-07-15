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
var AddArgumentsAsVariablesTransform = /** @class */ (function () {
    function AddArgumentsAsVariablesTransform(schema, args) {
        this.schema = schema;
        this.args = args;
    }
    AddArgumentsAsVariablesTransform.prototype.transformRequest = function (originalRequest) {
        var _a = addVariablesToRootField(this.schema, originalRequest.document, this.args), document = _a.document, newVariables = _a.newVariables;
        var variables = __assign({}, originalRequest.variables, newVariables);
        return {
            document: document,
            variables: variables,
        };
    };
    return AddArgumentsAsVariablesTransform;
}());
exports.default = AddArgumentsAsVariablesTransform;
function addVariablesToRootField(targetSchema, document, args) {
    var operations = document.definitions.filter(function (def) { return def.kind === graphql_1.Kind.OPERATION_DEFINITION; });
    var fragments = document.definitions.filter(function (def) { return def.kind === graphql_1.Kind.FRAGMENT_DEFINITION; });
    var variableNames = {};
    var newOperations = operations.map(function (operation) {
        var existingVariables = operation.variableDefinitions.map(function (variableDefinition) {
            return variableDefinition.variable.name.value;
        });
        var variableCounter = 0;
        var variables = {};
        var generateVariableName = function (argName) {
            var varName;
            do {
                varName = "_v" + variableCounter + "_" + argName;
                variableCounter++;
            } while (existingVariables.indexOf(varName) !== -1);
            return varName;
        };
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
        var newSelectionSet = [];
        operation.selectionSet.selections.forEach(function (selection) {
            if (selection.kind === graphql_1.Kind.FIELD) {
                var newArgs_1 = {};
                selection.arguments.forEach(function (argument) {
                    newArgs_1[argument.name.value] = argument;
                });
                var name_1 = selection.name.value;
                var field = type.getFields()[name_1];
                field.args.forEach(function (argument) {
                    if (argument.name in args) {
                        var variableName = generateVariableName(argument.name);
                        variableNames[argument.name] = variableName;
                        newArgs_1[argument.name] = {
                            kind: graphql_1.Kind.ARGUMENT,
                            name: {
                                kind: graphql_1.Kind.NAME,
                                value: argument.name,
                            },
                            value: {
                                kind: graphql_1.Kind.VARIABLE,
                                name: {
                                    kind: graphql_1.Kind.NAME,
                                    value: variableName,
                                },
                            },
                        };
                        existingVariables.push(variableName);
                        variables[variableName] = {
                            kind: graphql_1.Kind.VARIABLE_DEFINITION,
                            variable: {
                                kind: graphql_1.Kind.VARIABLE,
                                name: {
                                    kind: graphql_1.Kind.NAME,
                                    value: variableName,
                                },
                            },
                            type: typeToAst(argument.type),
                        };
                    }
                });
                newSelectionSet.push(__assign({}, selection, { arguments: Object.keys(newArgs_1).map(function (argName) { return newArgs_1[argName]; }) }));
            }
            else {
                newSelectionSet.push(selection);
            }
        });
        return __assign({}, operation, { variableDefinitions: operation.variableDefinitions.concat(Object.keys(variables).map(function (varName) { return variables[varName]; })), selectionSet: {
                kind: graphql_1.Kind.SELECTION_SET,
                selections: newSelectionSet,
            } });
    });
    var newVariables = {};
    Object.keys(variableNames).forEach(function (name) {
        newVariables[variableNames[name]] = args[name];
    });
    return {
        document: __assign({}, document, { definitions: newOperations.concat(fragments) }),
        newVariables: newVariables,
    };
}
function typeToAst(type) {
    if (type instanceof graphql_1.GraphQLNonNull) {
        var innerType = typeToAst(type.ofType);
        if (innerType.kind === graphql_1.Kind.LIST_TYPE ||
            innerType.kind === graphql_1.Kind.NAMED_TYPE) {
            return {
                kind: graphql_1.Kind.NON_NULL_TYPE,
                type: innerType,
            };
        }
        else {
            throw new Error('Incorrent inner non-null type');
        }
    }
    else if (type instanceof graphql_1.GraphQLList) {
        return {
            kind: graphql_1.Kind.LIST_TYPE,
            type: typeToAst(type.ofType),
        };
    }
    else {
        return {
            kind: graphql_1.Kind.NAMED_TYPE,
            name: {
                kind: graphql_1.Kind.NAME,
                value: type.toString(),
            },
        };
    }
}
//# sourceMappingURL=AddArgumentsAsVariables.js.map