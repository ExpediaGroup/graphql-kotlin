Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var visitSchema_1 = require("../transforms/visitSchema");
// Transformation used to modifiy `GraphQLEnumType` values in a schema.
var ConvertEnumValues = /** @class */ (function () {
    function ConvertEnumValues(enumValueMap) {
        this.enumValueMap = enumValueMap;
    }
    // Walk a schema looking for `GraphQLEnumType` types. If found, and
    // matching types have been identified in `this.enumValueMap`, create new
    // `GraphQLEnumType` types using the `this.enumValueMap` specified new
    // values, and return them in the new schema.
    ConvertEnumValues.prototype.transformSchema = function (schema) {
        var _a;
        var enumValueMap = this.enumValueMap;
        if (!enumValueMap || Object.keys(enumValueMap).length === 0) {
            return schema;
        }
        var transformedSchema = visitSchema_1.visitSchema(schema, (_a = {},
            _a[visitSchema_1.VisitSchemaKind.ENUM_TYPE] = function (enumType) {
                var externalToInternalValueMap = enumValueMap[enumType.name];
                if (externalToInternalValueMap) {
                    var values = enumType.getValues();
                    var newValues_1 = {};
                    values.forEach(function (value) {
                        var newValue = Object.keys(externalToInternalValueMap).includes(value.name)
                            ? externalToInternalValueMap[value.name]
                            : value.name;
                        newValues_1[value.name] = {
                            value: newValue,
                            deprecationReason: value.deprecationReason,
                            description: value.description,
                            astNode: value.astNode,
                        };
                    });
                    return new graphql_1.GraphQLEnumType({
                        name: enumType.name,
                        description: enumType.description,
                        astNode: enumType.astNode,
                        values: newValues_1,
                    });
                }
                return enumType;
            },
            _a));
        return transformedSchema;
    };
    return ConvertEnumValues;
}());
exports.default = ConvertEnumValues;
//# sourceMappingURL=ConvertEnumValues.js.map