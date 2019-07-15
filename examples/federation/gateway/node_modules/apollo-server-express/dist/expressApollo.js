"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const apollo_server_core_1 = require("apollo-server-core");
function graphqlExpress(options) {
    if (!options) {
        throw new Error('Apollo Server requires options.');
    }
    if (arguments.length > 1) {
        throw new Error(`Apollo Server expects exactly one argument, got ${arguments.length}`);
    }
    return (req, res, next) => {
        apollo_server_core_1.runHttpQuery([req, res], {
            method: req.method,
            options: options,
            query: req.method === 'POST' ? req.body : req.query,
            request: apollo_server_core_1.convertNodeHttpToRequest(req),
        }).then(({ graphqlResponse, responseInit }) => {
            if (responseInit.headers) {
                for (const [name, value] of Object.entries(responseInit.headers)) {
                    res.setHeader(name, value);
                }
            }
            if (typeof res.send === 'function') {
                res.send(graphqlResponse);
            }
            else {
                res.end(graphqlResponse);
            }
        }, (error) => {
            if ('HttpQueryError' !== error.name) {
                return next(error);
            }
            if (error.headers) {
                for (const [name, value] of Object.entries(error.headers)) {
                    res.setHeader(name, value);
                }
            }
            res.statusCode = error.statusCode;
            if (typeof res.send === 'function') {
                res.send(error.message);
            }
            else {
                res.end(error.message);
            }
        });
    };
}
exports.graphqlExpress = graphqlExpress;
//# sourceMappingURL=expressApollo.js.map