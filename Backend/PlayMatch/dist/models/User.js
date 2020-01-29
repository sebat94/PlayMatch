"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class User {
    constructor(telephoneNumber, nick, birthdate, gender, email, password, lat, lng, city, description, job, company, school, maxDistancePreference, minAgePreference, maxAgePreference, active, disabled) {
        this.telephoneNumber = telephoneNumber;
        this.nick = nick;
        this.birthdate = birthdate;
        this.gender = gender;
        this.email = email;
        this.password = password;
        this.lat = lat;
        this.lng = lng;
        this.city = city;
        this.description = description;
        this.job = job;
        this.company = company;
        this.school = school;
        this.maxDistancePreference = maxDistancePreference;
        this.minAgePreference = minAgePreference;
        this.maxAgePreference = maxAgePreference;
        this.active = active;
        this.disabled = disabled;
    }
    getTelephoneNumber() {
        return this.telephoneNumber;
    }
    setTelephoneNumber(telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
    getNick() {
        return this.nick;
    }
    setNick(nick) {
        this.nick = nick;
    }
    getBirthdate() {
        return this.birthdate;
    }
    setBirthdate(birthdate) {
        this.birthdate = birthdate;
    }
    getGender() {
        return this.gender;
    }
    setGender(gender) {
        this.gender = gender;
    }
    getEmail() {
        return this.email;
    }
    setEmail(email) {
        this.email = email;
    }
    getPassword() {
        return this.password;
    }
    setPassword(password) {
        this.password = password;
    }
    getLat() {
        return this.lat;
    }
    setLat(lat) {
        this.lat = lat;
    }
    getLng() {
        return this.lng;
    }
    setLng(lng) {
        this.lng = lng;
    }
    getCity() {
        return this.city;
    }
    setCity(city) {
        this.city = city;
    }
    getDescription() {
        return this.description;
    }
    setDescription(description) {
        this.description = description;
    }
    getJob() {
        return this.job;
    }
    setJob(job) {
        this.job = job;
    }
    getCompany() {
        return this.company;
    }
    setCompany(company) {
        this.company = company;
    }
    getSchool() {
        return this.school;
    }
    setSchool(school) {
        this.school = school;
    }
    getMaxdistancepreference() {
        return this.maxDistancePreference;
    }
    setMaxdistancepreference(maxDistancePreference) {
        this.maxDistancePreference = maxDistancePreference;
    }
    getMinagepreference() {
        return this.minAgePreference;
    }
    setMinagepreference(minAgePreference) {
        this.minAgePreference = minAgePreference;
    }
    getMaxagepreference() {
        return this.maxAgePreference;
    }
    setMaxagepreference(maxAgePreference) {
        this.maxAgePreference = maxAgePreference;
    }
    getActive() {
        return this.active;
    }
    setActive(active) {
        this.active = active;
    }
    getDisabled() {
        return this.disabled;
    }
    setDisabled(disabled) {
        this.disabled = disabled;
    }
}
exports.User = User;
//# sourceMappingURL=User.js.map