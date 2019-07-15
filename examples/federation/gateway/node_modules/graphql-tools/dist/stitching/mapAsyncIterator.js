Object.defineProperty(exports, "__esModule", { value: true });
var iterall_1 = require("iterall");
/**
 * Given an AsyncIterable and a callback function, return an AsyncIterator
 * which produces values mapped via calling the callback function.
 */
function mapAsyncIterator(iterator, callback, rejectCallback) {
    var _a;
    var $return;
    var abruptClose;
    if (typeof iterator.return === 'function') {
        $return = iterator.return;
        abruptClose = function (error) {
            var rethrow = function () { return Promise.reject(error); };
            return $return.call(iterator).then(rethrow, rethrow);
        };
    }
    function mapResult(result) {
        return result.done
            ? result
            : asyncMapValue(result.value, callback).then(iteratorResult, abruptClose);
    }
    var mapReject;
    if (rejectCallback) {
        // Capture rejectCallback to ensure it cannot be null.
        var reject_1 = rejectCallback;
        mapReject = function (error) {
            return asyncMapValue(error, reject_1).then(iteratorResult, abruptClose);
        };
    }
    return _a = {
            next: function () {
                return iterator.next().then(mapResult, mapReject);
            },
            return: function () {
                return $return
                    ? $return.call(iterator).then(mapResult, mapReject)
                    : Promise.resolve({ value: undefined, done: true });
            },
            throw: function (error) {
                if (typeof iterator.throw === 'function') {
                    return iterator.throw(error).then(mapResult, mapReject);
                }
                return Promise.reject(error).catch(abruptClose);
            }
        },
        _a[iterall_1.$$asyncIterator] = function () {
            return this;
        },
        _a;
}
exports.default = mapAsyncIterator;
function asyncMapValue(value, callback) {
    return new Promise(function (resolve) { return resolve(callback(value)); });
}
function iteratorResult(value) {
    return { value: value, done: false };
}
//# sourceMappingURL=mapAsyncIterator.js.map