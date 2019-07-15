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
var AddTypenameToAbstract = /** @class */ (function () {
    function AddTypenameToAbstract(targetSchema) {
        this.targetSchema = targetSchema;
    }
    AddTypenameToAbstract.prototype.transformRequest = function (originalRequest) {
        var document = addTypenameToAbstract(this.targetSchema, originalRequest.document);
        return __assign({}, originalRequest, { document: document });
    };
    return AddTypenameToAbstract;
}());
exports.default = AddTypenameToAbstract;
function addTypenameToAbstract(targetSchema, document) {
    var _a;
    var typeInfo = new graphql_1.TypeInfo(targetSchema);
    return graphql_1.visit(document, graphql_1.visitWithTypeInfo(typeInfo, (_a = {},
        _a[graphql_1.Kind.SELECTION_SET] = function (node) {
            var parentType = typeInfo.getParentType();
            var selections = node.selections;
            if (parentType &&
                (parentType instanceof graphql_1.GraphQLInterfaceType ||
                    parentType instanceof graphql_1.GraphQLUnionType) &&
                !selections.find(function (_) {
                    return _.kind === graphql_1.Kind.FIELD &&
                        _.name.value === '__typename';
                })) {
                selections = selections.concat({
                    kind: graphql_1.Kind.FIELD,
                    name: {
                        kind: graphql_1.Kind.NAME,
                        value: '__typename',
                    },
                });
            }
            if (selections !== node.selections) {
                return __assign({}, node, { selections: selections });
            }
        },
        _a)));
}
//# sourceMappingURL=AddTypenameToAbstract.js.map