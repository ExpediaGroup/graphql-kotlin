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
var ReplaceFieldWithFragment = /** @class */ (function () {
    function ReplaceFieldWithFragment(targetSchema, fragments) {
        this.targetSchema = targetSchema;
        this.mapping = {};
        for (var _i = 0, fragments_1 = fragments; _i < fragments_1.length; _i++) {
            var _a = fragments_1[_i], field = _a.field, fragment = _a.fragment;
            var parsedFragment = parseFragmentToInlineFragment(fragment);
            var actualTypeName = parsedFragment.typeCondition.name.value;
            this.mapping[actualTypeName] = this.mapping[actualTypeName] || {};
            if (this.mapping[actualTypeName][field]) {
                this.mapping[actualTypeName][field].push(parsedFragment);
            }
            else {
                this.mapping[actualTypeName][field] = [parsedFragment];
            }
        }
    }
    ReplaceFieldWithFragment.prototype.transformRequest = function (originalRequest) {
        var document = replaceFieldsWithFragments(this.targetSchema, originalRequest.document, this.mapping);
        return __assign({}, originalRequest, { document: document });
    };
    return ReplaceFieldWithFragment;
}());
exports.default = ReplaceFieldWithFragment;
function replaceFieldsWithFragments(targetSchema, document, mapping) {
    var _a;
    var typeInfo = new graphql_1.TypeInfo(targetSchema);
    return graphql_1.visit(document, graphql_1.visitWithTypeInfo(typeInfo, (_a = {},
        _a[graphql_1.Kind.SELECTION_SET] = function (node) {
            var parentType = typeInfo.getParentType();
            if (parentType) {
                var parentTypeName_1 = parentType.name;
                var selections_1 = node.selections;
                if (mapping[parentTypeName_1]) {
                    node.selections.forEach(function (selection) {
                        if (selection.kind === graphql_1.Kind.FIELD) {
                            var name_1 = selection.name.value;
                            var fragments = mapping[parentTypeName_1][name_1];
                            if (fragments && fragments.length > 0) {
                                var fragment = concatInlineFragments(parentTypeName_1, fragments);
                                selections_1 = selections_1.concat(fragment);
                            }
                        }
                    });
                }
                if (selections_1 !== node.selections) {
                    return __assign({}, node, { selections: selections_1 });
                }
            }
        },
        _a)));
}
function parseFragmentToInlineFragment(definitions) {
    if (definitions.trim().startsWith('fragment')) {
        var document_1 = graphql_1.parse(definitions);
        for (var _i = 0, _a = document_1.definitions; _i < _a.length; _i++) {
            var definition = _a[_i];
            if (definition.kind === graphql_1.Kind.FRAGMENT_DEFINITION) {
                return {
                    kind: graphql_1.Kind.INLINE_FRAGMENT,
                    typeCondition: definition.typeCondition,
                    selectionSet: definition.selectionSet,
                };
            }
        }
    }
    var query = graphql_1.parse("{" + definitions + "}")
        .definitions[0];
    for (var _b = 0, _c = query.selectionSet.selections; _b < _c.length; _b++) {
        var selection = _c[_b];
        if (selection.kind === graphql_1.Kind.INLINE_FRAGMENT) {
            return selection;
        }
    }
    throw new Error('Could not parse fragment');
}
function concatInlineFragments(type, fragments) {
    var fragmentSelections = fragments.reduce(function (selections, fragment) {
        return selections.concat(fragment.selectionSet.selections);
    }, []);
    var deduplicatedFragmentSelection = deduplicateSelection(fragmentSelections);
    return {
        kind: graphql_1.Kind.INLINE_FRAGMENT,
        typeCondition: {
            kind: graphql_1.Kind.NAMED_TYPE,
            name: {
                kind: graphql_1.Kind.NAME,
                value: type,
            },
        },
        selectionSet: {
            kind: graphql_1.Kind.SELECTION_SET,
            selections: deduplicatedFragmentSelection,
        },
    };
}
function deduplicateSelection(nodes) {
    var selectionMap = nodes.reduce(function (map, node) {
        var _a, _b, _c;
        switch (node.kind) {
            case 'Field': {
                if (node.alias) {
                    if (map.hasOwnProperty(node.alias.value)) {
                        return map;
                    }
                    else {
                        return __assign({}, map, (_a = {}, _a[node.alias.value] = node, _a));
                    }
                }
                else {
                    if (map.hasOwnProperty(node.name.value)) {
                        return map;
                    }
                    else {
                        return __assign({}, map, (_b = {}, _b[node.name.value] = node, _b));
                    }
                }
            }
            case 'FragmentSpread': {
                if (map.hasOwnProperty(node.name.value)) {
                    return map;
                }
                else {
                    return __assign({}, map, (_c = {}, _c[node.name.value] = node, _c));
                }
            }
            case 'InlineFragment': {
                if (map.__fragment) {
                    var fragment = map.__fragment;
                    return __assign({}, map, { __fragment: concatInlineFragments(fragment.typeCondition.name.value, [fragment, node]) });
                }
                else {
                    return __assign({}, map, { __fragment: node });
                }
            }
            default: {
                return map;
            }
        }
    }, {});
    var selection = Object.keys(selectionMap).reduce(function (selectionList, node) { return selectionList.concat(selectionMap[node]); }, []);
    return selection;
}
//# sourceMappingURL=ReplaceFieldWithFragment.js.map