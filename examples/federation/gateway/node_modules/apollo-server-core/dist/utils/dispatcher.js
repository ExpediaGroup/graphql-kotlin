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
class Dispatcher {
    constructor(targets) {
        this.targets = targets;
    }
    invokeHookAsync(methodName, ...args) {
        return __awaiter(this, void 0, void 0, function* () {
            return yield Promise.all(this.targets.map(target => {
                const method = target[methodName];
                if (method && typeof method === 'function') {
                    return method.apply(target, args);
                }
            }));
        });
    }
    invokeHooksUntilNonNull(methodName, ...args) {
        return __awaiter(this, void 0, void 0, function* () {
            for (const target of this.targets) {
                const method = target[methodName];
                if (!(method && typeof method === 'function')) {
                    continue;
                }
                const value = yield method.apply(target, args);
                if (value !== null) {
                    return value;
                }
            }
            return null;
        });
    }
    invokeDidStartHook(methodName, ...args) {
        const didEndHooks = [];
        for (const target of this.targets) {
            const method = target[methodName];
            if (method && typeof method === 'function') {
                const didEndHook = method.apply(target, args);
                if (didEndHook) {
                    didEndHooks.push(didEndHook);
                }
            }
        }
        return (...args) => {
            didEndHooks.reverse();
            for (const didEndHook of didEndHooks) {
                didEndHook(...args);
            }
        };
    }
}
exports.Dispatcher = Dispatcher;
//# sourceMappingURL=dispatcher.js.map