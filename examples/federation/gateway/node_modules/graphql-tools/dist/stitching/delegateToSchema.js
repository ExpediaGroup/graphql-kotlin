var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var transforms_1 = require("../transforms/transforms");
var AddArgumentsAsVariables_1 = require("../transforms/AddArgumentsAsVariables");
var FilterToSchema_1 = require("../transforms/FilterToSchema");
var AddTypenameToAbstract_1 = require("../transforms/AddTypenameToAbstract");
var CheckResultAndHandleErrors_1 = require("../transforms/CheckResultAndHandleErrors");
var mapAsyncIterator_1 = require("./mapAsyncIterator");
var ExpandAbstractTypes_1 = require("../transforms/ExpandAbstractTypes");
var ReplaceFieldWithFragment_1 = require("../transforms/ReplaceFieldWithFragment");
var ConvertEnumResponse_1 = require("../transforms/ConvertEnumResponse");
function delegateToSchema(options) {
    var args = [];
    for (var _i = 1; _i < arguments.length; _i++) {
        args[_i - 1] = arguments[_i];
    }
    if (options instanceof graphql_1.GraphQLSchema) {
        throw new Error('Passing positional arguments to delegateToSchema is a deprecated. ' +
            'Please pass named parameters instead.');
    }
    return delegateToSchemaImplementation(options);
}
exports.default = delegateToSchema;
function delegateToSchemaImplementation(options) {
    return __awaiter(this, void 0, void 0, function () {
        var info, _a, args, operation, rawDocument, rawRequest, transforms, processedRequest, errors, _b, executionResult;
        return __generator(this, function (_c) {
            switch (_c.label) {
                case 0:
                    info = options.info, _a = options.args, args = _a === void 0 ? {} : _a;
                    operation = options.operation || info.operation.operation;
                    rawDocument = createDocument(options.fieldName, operation, info.fieldNodes, Object.keys(info.fragments).map(function (fragmentName) { return info.fragments[fragmentName]; }), info.operation.variableDefinitions, info.operation.name);
                    rawRequest = {
                        document: rawDocument,
                        variables: info.variableValues,
                    };
                    transforms = (options.transforms || []).concat([
                        new ExpandAbstractTypes_1.default(info.schema, options.schema),
                    ]);
                    if (info.mergeInfo && info.mergeInfo.fragments) {
                        transforms.push(new ReplaceFieldWithFragment_1.default(options.schema, info.mergeInfo.fragments));
                    }
                    transforms = transforms.concat([
                        new AddArgumentsAsVariables_1.default(options.schema, args),
                        new FilterToSchema_1.default(options.schema),
                        new AddTypenameToAbstract_1.default(options.schema),
                        new CheckResultAndHandleErrors_1.default(info, options.fieldName),
                    ]);
                    if (graphql_1.isEnumType(options.info.returnType)) {
                        transforms = transforms.concat(new ConvertEnumResponse_1.default(options.info.returnType));
                    }
                    processedRequest = transforms_1.applyRequestTransforms(rawRequest, transforms);
                    if (!options.skipValidation) {
                        errors = graphql_1.validate(options.schema, processedRequest.document);
                        if (errors.length > 0) {
                            throw errors;
                        }
                    }
                    if (!(operation === 'query' || operation === 'mutation')) return [3 /*break*/, 2];
                    _b = transforms_1.applyResultTransforms;
                    return [4 /*yield*/, graphql_1.execute(options.schema, processedRequest.document, info.rootValue, options.context, processedRequest.variables)];
                case 1: return [2 /*return*/, _b.apply(void 0, [_c.sent(),
                        transforms])];
                case 2:
                    if (!(operation === 'subscription')) return [3 /*break*/, 4];
                    return [4 /*yield*/, graphql_1.subscribe(options.schema, processedRequest.document, info.rootValue, options.context, processedRequest.variables)];
                case 3:
                    executionResult = (_c.sent());
                    // "subscribe" to the subscription result and map the result through the transforms
                    return [2 /*return*/, mapAsyncIterator_1.default(executionResult, function (result) {
                            var _a;
                            var transformedResult = transforms_1.applyResultTransforms(result, transforms);
                            var subscriptionKey = Object.keys(result.data)[0];
                            // for some reason the returned transformedResult needs to be nested inside the root subscription field
                            // does not work otherwise...
                            return _a = {},
                                _a[subscriptionKey] = transformedResult,
                                _a;
                        })];
                case 4: return [2 /*return*/];
            }
        });
    });
}
function createDocument(targetField, targetOperation, originalSelections, fragments, variables, operationName) {
    var selections = [];
    var args = [];
    originalSelections.forEach(function (field) {
        var fieldSelections = field.selectionSet
            ? field.selectionSet.selections
            : [];
        selections = selections.concat(fieldSelections);
        args = args.concat(field.arguments || []);
    });
    var selectionSet = null;
    if (selections.length > 0) {
        selectionSet = {
            kind: graphql_1.Kind.SELECTION_SET,
            selections: selections,
        };
    }
    var rootField = {
        kind: graphql_1.Kind.FIELD,
        alias: null,
        arguments: args,
        selectionSet: selectionSet,
        name: {
            kind: graphql_1.Kind.NAME,
            value: targetField,
        },
    };
    var rootSelectionSet = {
        kind: graphql_1.Kind.SELECTION_SET,
        selections: [rootField],
    };
    var operationDefinition = {
        kind: graphql_1.Kind.OPERATION_DEFINITION,
        operation: targetOperation,
        variableDefinitions: variables,
        selectionSet: rootSelectionSet,
        name: operationName,
    };
    return {
        kind: graphql_1.Kind.DOCUMENT,
        definitions: [operationDefinition].concat(fragments),
    };
}
//# sourceMappingURL=delegateToSchema.js.map