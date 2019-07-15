Object.defineProperty(exports, "__esModule", { value: true });
var apollo_link_1 = require("apollo-link");
var apollo_link_2 = require("apollo-link");
exports.execute = apollo_link_2.execute;
function linkToFetcher(link) {
    return function (fetcherOperation) {
        return apollo_link_1.makePromise(apollo_link_1.execute(link, fetcherOperation));
    };
}
exports.default = linkToFetcher;
//# sourceMappingURL=linkToFetcher.js.map