"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Image {
    constructor(url, user, isProfile, eliminationDate) {
        this.url = url;
        this.user = user;
        this.isProfile = isProfile;
        this.eliminationDate = eliminationDate;
    }
    getUrl() {
        return this.url;
    }
    setUrl(url) {
        this.url = url;
    }
    getUser() {
        return this.user;
    }
    setUser(user) {
        this.user = user;
    }
    getIsprofile() {
        return this.isProfile;
    }
    setIsprofile(isProfile) {
        this.isProfile = isProfile;
    }
    getEliminationDate() {
        return this.eliminationDate;
    }
    setEliminationDate(eliminationDate) {
        this.eliminationDate = eliminationDate;
    }
}
exports.Image = Image;
//# sourceMappingURL=Image.js.map