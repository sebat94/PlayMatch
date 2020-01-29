"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// This mem-cache module is a local in-memory cache - for production use, you would replace this with Memcache, Redis, or a database
const Cache = require('mem-cache');
const cache = new Cache();
// Variables To Generate OneTimeCode
const chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
const onTimeCodeLength = 6;
class SmsRetrieverUtils {
    /**
     * Save In Cache GeneratedOnTimeCode with your associated telephoneNumber
     */
    static cacheStoreItem(telephoneNumber, hash) {
        cache.set(telephoneNumber, hash, this.getExpirationTime()); // Key, Value, Expiration
    }
    /**
     * Get Item
     * @param telephoneNumber
     */
    static cacheGetItem(telephoneNumber) {
        return cache.get(telephoneNumber); // key
    }
    /**
     * Remove Element from Cache
     */
    static cacheRemoveItem(telephoneNumber) {
        cache.remove(telephoneNumber); // key
    }
    /**
     * Remove All Elements from Cache
     */
    static cacheClear() {
        cache.clear();
    }
    /**
     * Time Cache Expiration
     * Works in MilliSeconds (5 min = 300 seg ---> 300 seg * 1000) - Time that SMSRetrieverAPI wait for a SMS before launch a Timeout
     */
    static getExpirationTime() {
        return 300 * 1000;
    }
    /**
     * Get a Random String of Specific Length
     */
    static generateOneTimeCode() {
        let randomstring = '';
        for (let i = 0; i < onTimeCodeLength; i++) {
            let rnum = Math.floor(Math.random() * chars.length);
            randomstring += chars.substring(rnum, rnum + 1);
        }
        return randomstring;
    }
}
exports.SmsRetrieverUtils = SmsRetrieverUtils;
//# sourceMappingURL=SmsRetrieverUtils.js.map