import { conexion } from "./bdconfig";
import { GenericErrorResponse } from "../utils/GenericErrorResponse";
import { IUser } from "../interfaces/IUser";

export class FavouriteController {

    /**
     * GET ALL CHATS OF USER ID AND THEIR LAST MESSAGE SENDED ORDERED BY MESSAGE DATE (MESSAGE: NULL - IF NOT EXISTS)
     * @param userId 
     */
    static getMyFavourites(userId: number): Promise<IUser[]>{
        return new Promise((resolve, reject) => {
            conexion.query(`SELECT id, telephoneNumber, nick, birthdate, gender, email, lat, lng, description, job, company, school, maxDistancePreference, minAgePreference, maxAgePreference, active, disabled
                            FROM user WHERE id IN (SELECT user2 FROM favourites WHERE user1 = ?)`, [userId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result);
            });
        });
    }

    /** -- PAGO --
     * GET ALL USERS THAH HAVE ME IN THEIR FAVOURITES
     * @param userId 
     */
    static getUsersThahHaveMeInFavourites(userId: number): Promise<IUser[]>{
        return new Promise((resolve, reject) => {
            conexion.query(`SELECT id, telephoneNumber, nick, birthdate, gender, email, lat, lng, description, job, company, school, maxDistancePreference, minAgePreference, maxAgePreference, active, disabled
                            FROM user WHERE id IN (SELECT user1 FROM favourites where user2 = 8);`, [userId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
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
    static addFavourite(userId: number, userIdThatILike: number): Promise<void>{
        return new Promise((resolve, reject) => {
            conexion.query('INSERT INTO favourites SET ?', {user1: userId, user2: userIdThatILike}, (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
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
    static removeFavourite(userId: number, userIdToRemove: number): Promise<void>{
        return new Promise((resolve, reject) => {
            conexion.query('DELETE FROM favourites WHERE user1 = ? AND user2 = ?', [userId, userIdToRemove], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
    
}