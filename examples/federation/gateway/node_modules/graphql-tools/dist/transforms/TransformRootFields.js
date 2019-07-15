Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var isEmptyObject_1 = require("../isEmptyObject");
var visitSchema_1 = require("./visitSchema");
var schemaRecreation_1 = require("../stitching/schemaRecreation");
var TransformRootFields = /** @class */ (function () {
    function TransformRootFields(transform) {
        this.transform = transform;
    }
    TransformRootFields.prototype.transformSchema = function (originalSchema) {
        var _this = this;
        var _a;
        return visitSchema_1.visitSchema(originalSchema, (_a = {},
            _a[visitSchema_1.VisitSchemaKind.QUERY] = function (type) {
                return transformFields(type, function (fieldName, field) {
                    return _this.transform('Query', fieldName, field);
                });
            },
            _a[visitSchema_1.VisitSchemaKind.MUTATION] = function (type) {
                return transformFields(type, function (fieldName, field) {
                    return _this.transform('Mutation', fieldName, field);
                });
            },
            _a[visitSchema_1.VisitSchemaKind.SUBSCRIPTION] = function (type) {
                return transformFields(type, function (fieldName, field) {
                    return _this.transform('Subscription', fieldName, field);
                });
            },
            _a));
    };
    return TransformRootFields;
}());
exports.default = TransformRootFields;
function transformFields(type, transformer) {
    var resolveType = schemaRecreation_1.createResolveType(function (name, originalType) {
        return originalType;
    });
    var fields = type.getFields();
    var newFields = {};
    Object.keys(fields).forEach(function (fieldName) {
        var field = fields[fieldName];
        var newField = transformer(fieldName, field);
        if (typeof newField === 'undefined') {
            newFields[fieldName] = schemaRecreation_1.fieldToFieldConfig(field, resolveType, true);
        }
        else if (newField !== null) {
            if (newField.name) {
                newFields[newField.name] = newField.field;
            }
            else {
                newFields[fieldName] = newField;
            }
        }
    });
    if (isEmptyObject_1.default(newFields)) {
        return null;
    }
    else {
        return new graphql_1.GraphQLObjectType({
            name: type.name,
            description: type.description,
            astNode: type.astNode,
            fields: newFields,
        });
    }
}
//# sourceMappingURL=TransformRootFields.js.map