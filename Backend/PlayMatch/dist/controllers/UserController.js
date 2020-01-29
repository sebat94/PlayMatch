"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const bdconfig_1 = require("./bdconfig");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
class UserController {
    /**
     * GET USER BY ID
     * @param id
     */
    static findUserById(id) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query(`SELECT id, telephoneNumber, nick, birthdate, gender, email, lat, lng, city, description, job, company, school, maxDistancePreference, minAgePreference, maxAgePreference, active, disabled FROM user WHERE id = ?`, [id], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return this.setImagesAndGenderPreferencesToUser(result[0])
                        .then((user) => resolve(user))
                        .catch(error => reject(error));
            });
        });
    }
    /**
     * GET USER BY TELEPHONE_NUMBER (ONLY USED TO CHECK IF USER EXISTS BEFORE REGISTER)
     * @param telephoneNumber
     */
    static findUserByTelephoneNumber(telephoneNumber) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query(`SELECT id, telephoneNumber, nick, birthdate, gender, email, lat, lng, city, description, job, company, school, maxDistancePreference, minAgePreference, maxAgePreference, active, disabled FROM user WHERE telephoneNumber = ?`, [telephoneNumber], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result[0]);
            });
        });
    }
    /**
     * REGISTER NEW USER            ---> TODO: Ver Porque no se inserta el usuario
     * @param userJson
     */
    static registerNewUser(userJson) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('INSERT INTO user SET ?', userJson, (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return this.findUserById(result.insertId)
                        .then((res) => resolve(res))
                        .catch(error => reject(error));
            });
        });
    }
    /**
     * UPDATE USER BY ID
     * @param userJson
     * @param userId
     */
    static updateUser(userJson, userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('UPDATE user SET ? WHERE id = ?', [userJson, userId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else // result --> {"fieldCount":0,"affectedRows":1,"insertId":0,"serverStatus":2,"warningCount":0,"message":"(Rows matched: 1  Changed: 0  Warnings: 0","protocol41":true,"changedRows":0}
                    return this.findUserById(userId)
                        .then((res) => resolve(res))
                        .catch(error => reject(error));
            });
        });
    }
    /**
     * DROP OUT USER BY ID - ONLY AFFECTS TO FIELD 'ACTIVE' SETTING IT TO NULL
     * @param userId
     */
    static dropOutUser(userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('UPDATE user SET disabled = NOW() WHERE id = ?', [userId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return this.deleteUserToken(userId)
                        .then(() => resolve())
                        .catch(error => reject(error));
            });
        });
    }
    /**
     * WHEN UNSUBSCRIBED USER TRY TO CONNECT AGAIN THEN WE SUBSCRIBE AGAIN
     * @param userId
     */
    static reRegisterUser(userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('UPDATE user SET disabled = null WHERE id = ?', [userId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return this.deleteUserToken(userId)
                        .then(() => resolve())
                        .catch(error => reject(error));
            });
        });
    }
    /**
     * CREATE GENDER PREFERENCE/S
     * @param userId
     * @param genderIds
     */
    static createGenderPreferences(userId, genderIds) {
        let values = [];
        genderIds.forEach((eachGenderId) => values.push([userId, eachGenderId]));
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('INSERT INTO gender_preference (user, gender) VALUES ?', [values], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
    /**
     * DELETE GENDER PREFERENCE
     * @param userId
     * @param genderId
     */
    static deleteGenderPreference(userId, genderId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('DELETE FROM gender_preference WHERE user = ? AND gender = ?', [userId, genderId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
    /**
     * GET ALL USERS
     */
    static listAllUsers(userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query(`SELECT * FROM user WHERE id != ${userId}`, (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return this.setImagesAndGenderPreferencesToUser(result)
                        .then((usersArray) => resolve(usersArray))
                        .catch(error => reject(error));
            });
        });
    }
    /**
     * ADD CORRESPONDING IMAGES AND GENDER PREFERENCES TO EACH USER
     * @param result
     */
    static setImagesAndGenderPreferencesToUser(result) {
        if (result instanceof Array) {
            return Promise.all(result.map((user) => __awaiter(this, void 0, void 0, function* () {
                // Add Images To User
                const imagesArray = yield this.getImagesFromUser(user.id);
                user.images = imagesArray;
                // Add Gender Preferences To User
                const genderPreferenceArray = yield this.getGenderPreferencesFromUser(user.id);
                user.genderPreferences = genderPreferenceArray;
                return user;
            })));
        }
        else {
            return (() => __awaiter(this, void 0, void 0, function* () {
                // Add Images To User
                const imagesArray = yield this.getImagesFromUser(result.id);
                result.images = imagesArray;
                // Add Gender Preferences To User
                const genderPreferenceArray = yield this.getGenderPreferencesFromUser(result.id);
                result.genderPreferences = genderPreferenceArray;
                return result;
            }))();
        }
    }
    /**
     * GET IMAGES OF USER-ID (We don't compare 'eliminationDate' field IS NULL, we return all the images, then the user can choose what image want do "Undo Delete Operation". Android only shows images with 'eliminationDate = NULL')
     * @param userId
     */
    static getImagesFromUser(userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('SELECT * FROM image WHERE user = ?', [userId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result);
            });
        });
    }
    /**
     * GET GENDER PREFERENCES OF USER-ID
     * @param userId
     */
    static getGenderPreferencesFromUser(userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('SELECT * FROM gender_preference WHERE user = ?', [userId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result);
            });
        });
    }
    /**
     * DELETE TOKEN OF DROP OUT USER
     * @param userId
     */
    static deleteUserToken(userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('DELETE FROM auth WHERE user = ?', [userId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
    /*-- ************************************************************************************************************** --*/
    /*-- **************************************************** JOBS **************************************************** --*/
    /*-- ************************************************************************************************************** --*/
    /**
     * DELETE ALL USERS THAT HAVE BEEN UNSUBSCRIBED - (JOB 'checkUsersToDelete')        ---------------------------- SIN USAR
     */
    static deleteUnsubscribedUsers() {
        return new Promise((resolve, reject) => {
            this.getAllUnsubscribedUsers().then((users) => {
                // If there ara unsubscribed users
                if (users.length > 0) {
                    let arrayUserIds = users.map(eachUser => eachUser.id);
                    let arrayImages = users.map((eachUser) => eachUser.images.map((eachImage) => eachImage)).reduce((prev, curr) => prev.concat(curr));
                    bdconfig_1.conexion.query('UPDATE user SET |||| -- anonimize data -- |||| WHERE disabled < (NOW() - INTERVAL 60 MONTH) and id IN (?)', [arrayUserIds], (error, result, fields) => {
                        if (error)
                            return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                        else
                            return resolve(arrayImages);
                    });
                }
                else
                    return reject('No unsibscribed users');
            }).catch((error) => reject(error));
        });
    }
    /**
     * GET ALL USERS TO BE UNSUBSCRIBED - (JOB 'checkUsersToDelete')                    ---------------------------- SIN USAR
     */
    static getAllUnsubscribedUsers() {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('SELECT * FROM user WHERE disabled IS NOT NULL', (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return this.setImagesAndGenderPreferencesToUser(result)
                        .then((usersArray) => resolve(usersArray))
                        .catch(error => reject(error));
            });
        });
    }
}
exports.UserController = UserController;
//# sourceMappingURL=UserController.js.map