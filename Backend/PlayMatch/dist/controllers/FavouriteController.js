"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const bdconfig_1 = require("./bdconfig");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
class FavouriteController {
    /**
     * GET ALL CHATS OF USER ID AND THEIR LAST MESSAGE SENDED ORDERED BY MESSAGE DATE (MESSAGE: NULL - IF NOT EXISTS)
     * @param userId
     */
    static getMyFavourites(userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query(`SELECT id, telephoneNumber, nick, birthdate, gender, email, lat, lng, description, job, company, school, maxDistancePreference, minAgePreference, maxAgePreference, active, disabled
                            FROM user WHERE id IN (SELECT user2 FROM favourites WHERE user1 = ?)`, [userId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result);
            });
        });
    }
    /** -- PAGO --
     * GET ALL USERS THAH HAVE ME IN THEIR FAVOURITES
     * @param userId
     */
    static getUsersThahHaveMeInFavourites(userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query(`SELECT id, telephoneNumber, nick, birthdate, gender, email, lat, lng, description, job, company, school, maxDistancePreference, minAgePreference, maxAgePreference, active, disabled
                            FROM user WHERE id IN (SELECT user1 FROM favourites where user2 = 8);`, [userId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result);
            });
        });
    }
    /**
     * ADD PERSON TO FAVOURITES
     * @param userId
     * @param userIdThatILike
     */
    static addFavourite(userId, userIdThatILike) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('INSERT INTO favourites SET ?', { user1: userId, user2: userIdThatILike }, (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
    /**
     * DELETE A FAVOURITE
     * @param idChat
     * @param userIdToRemove
     */
    static removeFavourite(userId, userIdToRemove) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('DELETE FROM favourites WHERE user1 = ? AND user2 = ?', [userId, userIdToRemove], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
}
exports.FavouriteController = FavouriteController;
//# sourceMappingURL=FavouriteController.js.map