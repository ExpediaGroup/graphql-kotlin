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
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const node_fetch_1 = __importDefault(require("node-fetch"));
class CachedFetcher {
    constructor() {
        this.logPrefix = 'CachedFetcher: ';
        this.mapUrlToLastSuccessfulETag = Object.create(null);
        this.mapUrlToCachedResult = Object.create(null);
    }
    fetch(url) {
        return __awaiter(this, void 0, void 0, function* () {
            const fetchOptions = {
                method: 'GET',
                headers: Object.create(null),
            };
            const lastSuccessfulETag = this.mapUrlToLastSuccessfulETag[url];
            if (lastSuccessfulETag) {
                fetchOptions.headers = Object.assign(Object.assign({}, fetchOptions.headers), { 'If-None-Match': lastSuccessfulETag });
            }
            let response;
            try {
                response = yield node_fetch_1.default(url, fetchOptions);
            }
            catch (error) {
                throw error;
            }
            const receivedETag = response.headers.get('etag');
            if (response.status === 304) {
                return { isCacheHit: true, result: this.mapUrlToCachedResult[url] };
            }
            if (!response.ok) {
                throw new Error(`${this.logPrefix}Could not fetch ${yield response.text()}`);
            }
            if (receivedETag) {
                this.mapUrlToLastSuccessfulETag[url] = receivedETag;
            }
            this.mapUrlToCachedResult[url] = yield response.text();
            return { isCacheHit: false, result: this.mapUrlToCachedResult[url] };
        });
    }
    getCache() {
        return this.mapUrlToCachedResult;
    }
}
exports.CachedFetcher = CachedFetcher;
//# sourceMappingURL=cachedFetcher.js.map