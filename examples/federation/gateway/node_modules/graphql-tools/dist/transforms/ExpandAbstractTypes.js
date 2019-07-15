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
var ExpandAbstractTypes = /** @class */ (function () {
    function ExpandAbstractTypes(transformedSchema, targetSchema) {
        this.targetSchema = targetSchema;
        this.mapping = extractPossibleTypes(transformedSchema, targetSchema);
        this.reverseMapping = flipMapping(this.mapping);
    }
    ExpandAbstractTypes.prototype.transformRequest = function (originalRequest) {
        var document = expandAbstractTypes(this.targetSchema, this.mapping, this.reverseMapping, originalRequest.document);
        return __assign({}, originalRequest, { document: document });
    };
    return ExpandAbstractTypes;
}());
exports.default = ExpandAbstractTypes;
function extractPossibleTypes(transformedSchema, targetSchema) {
    var typeMap = transformedSchema.getTypeMap();
    var mapping = {};
    Object.keys(typeMap).forEach(function (typeName) {
        var type = typeMap[typeName];
        if (graphql_1.isAbstractType(type)) {
            var targetType = targetSchema.getType(typeName);
            if (!graphql_1.isAbstractType(targetType)) {
                var implementations = transformedSchema.getPossibleTypes(type) || [];
                mapping[typeName] = implementations
                    .filter(function (impl) { return targetSchema.getType(impl.name); })
                    .map(function (impl) { return impl.name; });
            }
        }
    });
    return mapping;
}
function flipMapping(mapping) {
    var result = {};
    Object.keys(mapping).forEach(function (typeName) {
        var toTypeNames = mapping[typeName];
        toTypeNames.forEach(function (toTypeName) {
            if (!result[toTypeName]) {
                result[toTypeName] = [];
            }
            result[toTypeName].push(typeName);
        });
    });
    return result;
}
function expandAbstractTypes(targetSchema, mapping, reverseMapping, document) {
    var _a;
    var operations = document.definitions.filter(function (def) { return def.kind === graphql_1.Kind.OPERATION_DEFINITION; });
    var fragments = document.definitions.filter(function (def) { return def.kind === graphql_1.Kind.FRAGMENT_DEFINITION; });
    var existingFragmentNames = fragments.map(function (fragment) { return fragment.name.value; });
    var fragmentCounter = 0;
    var generateFragmentName = function (typeName) {
        var fragmentName;
        do {
            fragmentName = "_" + typeName + "_Fragment" + fragmentCounter;
            fragmentCounter++;
        } while (existingFragmentNames.indexOf(fragmentName) !== -1);
        return fragmentName;
    };
    var newFragments = [];
    var fragmentReplacements = {};
    fragments.forEach(function (fragment) {
        newFragments.push(fragment);
        var possibleTypes = mapping[fragment.typeCondition.name.value];
        if (possibleTypes) {
            fragmentReplacements[fragment.name.value] = [];
            possibleTypes.forEach(function (possibleTypeName) {
                var name = generateFragmentName(possibleTypeName);
                existingFragmentNames.push(name);
                var newFragment = {
                    kind: graphql_1.Kind.FRAGMENT_DEFINITION,
                    name: {
                        kind: graphql_1.Kind.NAME,
                        value: name,
                    },
                    typeCondition: {
                        kind: graphql_1.Kind.NAMED_TYPE,
                        name: {
                            kind: graphql_1.Kind.NAME,
                            value: possibleTypeName,
                        },
                    },
                    selectionSet: fragment.selectionSet,
                };
                newFragments.push(newFragment);
                fragmentReplacements[fragment.name.value].push({
                    fragmentName: name,
                    typeName: possibleTypeName,
                });
            });
        }
    });
    var newDocument = __assign({}, document, { definitions: operations.concat(newFragments) });
    var typeInfo = new graphql_1.TypeInfo(targetSchema);
    return graphql_1.visit(newDocument, graphql_1.visitWithTypeInfo(typeInfo, (_a = {},
        _a[graphql_1.Kind.SELECTION_SET] = function (node) {
            var newSelections = node.selections.slice();
            var parentType = graphql_1.getNamedType(typeInfo.getParentType());
            node.selections.forEach(function (selection) {
                if (selection.kind === graphql_1.Kind.INLINE_FRAGMENT) {
                    var possibleTypes = mapping[selection.typeCondition.name.value];
                    if (possibleTypes) {
                        possibleTypes.forEach(function (possibleType) {
                            if (implementsAbstractType_1.default(targetSchema, parentType, targetSchema.getType(possibleType))) {
                                newSelections.push({
                                    kind: graphql_1.Kind.INLINE_FRAGMENT,
                                    typeCondition: {
                                        kind: graphql_1.Kind.NAMED_TYPE,
                                        name: {
                                            kind: graphql_1.Kind.NAME,
                                            value: possibleType,
                                        },
                                    },
                                    selectionSet: selection.selectionSet,
                                });
                            }
                        });
                    }
                }
                else if (selection.kind === graphql_1.Kind.FRAGMENT_SPREAD) {
                    var fragmentName = selection.name.value;
                    var replacements = fragmentReplacements[fragmentName];
                    if (replacements) {
                        replacements.forEach(function (replacement) {
                            var typeName = replacement.typeName;
                            if (implementsAbstractType_1.default(targetSchema, parentType, targetSchema.getType(typeName))) {
                                newSelections.push({
                                    kind: graphql_1.Kind.FRAGMENT_SPREAD,
                                    name: {
                                        kind: graphql_1.Kind.NAME,
                                        value: replacement.fragmentName,
                                    },
                                });
                            }
                        });
                    }
                }
            });
            if (parentType && reverseMapping[parentType.name]) {
                newSelections.push({
                    kind: graphql_1.Kind.FIELD,
                    name: {
                        kind: graphql_1.Kind.NAME,
                        value: '__typename',
                    },
                });
            }
            if (newSelections.length !== node.selections.length) {
                return __assign({}, node, { selections: newSelections });
            }
        },
        _a)));
}
//# sourceMappingURL=ExpandAbstractTypes.js.map