"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const graphql_1 = require("graphql");
const graphql_extensions_1 = require("graphql-extensions");
class LocalGraphQLDataSource {
    constructor(schema) {
        this.schema = schema;
        graphql_extensions_1.enableGraphQLExtensions(schema);
    }
    process({ request, context, }) {
        return __awaiter(this, void 0, void 0, function* () {
            return graphql_1.graphql({
                schema: this.schema,
                source: request.query,
                variableValues: request.variables,
                operationName: request.operationName,
                contextValue: context,
            });
        });
    }
    sdl() {
        const result = graphql_1.graphqlSync({
            schema: this.schema,
            source: `{ _service { sdl }}`,
        });
        if (result.errors) {
            throw new Error(result.errors.map(error => error.message).join('\n\n'));
        }
        const sdl = result.data && result.data._service && result.data._service.sdl;
        return graphql_1.parse(sdl);
    }
}
exports.LocalGraphQLDataSource = LocalGraphQLDataSource;
//# sourceMappingURL=LocalGraphQLDataSource.js.map