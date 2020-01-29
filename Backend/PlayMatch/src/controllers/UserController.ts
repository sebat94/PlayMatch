import { conexion } from "./bdconfig";
import { IImage } from '../interfaces/IImage';
import { IUser } from "../interfaces/IUser";
import { GenericErrorResponse } from "../utils/GenericErrorResponse";
import { IGenderPreference } from "../interfaces/IGenderPreference";
import { userInfo } from "os";

export class UserController {

    /**
     * GET USER BY ID
     * @param id 
     */
    static findUserById(id: number): Promise<IUser>{
        return new Promise((resolve, reject) => {
            conexion.query(`SELECT id, telephoneNumber, nick, birthdate, gender, email, lat, lng, city, description, job, company, school, maxDistancePreference, minAgePreference, maxAgePreference, active, disabled FROM user WHERE id = ?`, [id], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return this.setImagesAndGenderPreferencesToUser(result[0])
                    .then((user: IUser) => resolve(user))
                    .catch(error => reject(error));
            });
        });
    }

    /**
     * GET USER BY TELEPHONE_NUMBER (ONLY USED TO CHECK IF USER EXISTS BEFORE REGISTER)
     * @param telephoneNumber 
     */
    static findUserByTelephoneNumber(telephoneNumber: string): Promise<IUser>{
        return new Promise((resolve, reject) => {
            conexion.query(`SELECT id, telephoneNumber, nick, birthdate, gender, email, lat, lng, city, description, job, company, school, maxDistancePreference, minAgePreference, maxAgePreference, active, disabled FROM user WHERE telephoneNumber = ?`, [telephoneNumber], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result[0]);
            });
        });
    }

    /**
     * REGISTER NEW USER            ---> TODO: Ver Porque no se inserta el usuario
     * @param userJson 
     */
    static registerNewUser(userJson: IUser): Promise<IUser>{
        return new Promise((resolve, reject) => {
            conexion.query('INSERT INTO user SET ?', userJson, (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return this.findUserById(result.insertId)
                    .then((res: IUser) => resolve(res))
                    .catch(error => reject(error));
            });
        });
    }

    /**
     * UPDATE USER BY ID
     * @param userJson 
     * @param userId 
     */
    static updateUser(userJson: IUser, userId: number): Promise<IUser>{
        return new Promise((resolve, reject) => {
            conexion.query('UPDATE user SET ? WHERE id = ?', [userJson, userId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else    // result --> {"fieldCount":0,"affectedRows":1,"insertId":0,"serverStatus":2,"warningCount":0,"message":"(Rows matched: 1  Changed: 0  Warnings: 0","protocol41":true,"changedRows":0}
                    return this.findUserById(userId)
                    .then((res: IUser) => resolve(res))
                    .catch(error => reject(error));
            });
        });
    }

    /**
     * DROP OUT USER BY ID - ONLY AFFECTS TO FIELD 'ACTIVE' SETTING IT TO NULL
     * @param userId 
     */
    static dropOutUser(userId: number): Promise<IUser>{
        return new Promise((resolve, reject) => {
            conexion.query('UPDATE user SET disabled = NOW() WHERE id = ?', [userId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
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
    static reRegisterUser(userId: number): Promise<void>{
        return new Promise((resolve, reject) => {
            conexion.query('UPDATE user SET disabled = null WHERE id = ?', [userId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
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
    static createGenderPreferences(userId: number, genderIds: number[]): Promise<void>{
        let values: Array<number[]> = [];
        genderIds.forEach((eachGenderId: number) => values.push([userId, eachGenderId]));
        return new Promise((resolve, reject) => {
            conexion.query('INSERT INTO gender_preference (user, gender) VALUES ?', [values], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
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
    static deleteGenderPreference(userId: number, genderId: number): Promise<void>{
        return new Promise((resolve, reject) => {
            conexion.query('DELETE FROM gender_preference WHERE user = ? AND gender = ?', [userId, genderId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }

    /**
     * GET ALL USERS
     */
    static listAllUsers(userId: number): Promise<IUser[]>{
        return new Promise((resolve, reject) => {
            conexion.query(`SELECT * FROM user WHERE id != ${userId}`, (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return this.setImagesAndGenderPreferencesToUser(result)
                    .then((usersArray: IUser[]) => resolve(usersArray))
                    .catch(error => reject(error));
            });
        });
    }

    /**
     * ADD CORRESPONDING IMAGES AND GENDER PREFERENCES TO EACH USER
     * @param result 
     */
    static setImagesAndGenderPreferencesToUser(result: IUser[] | IUser): Promise<IUser[] | IUser>{
        if(result instanceof Array){
            return Promise.all(result.map(async (user: IUser): Promise<IUser> => {
                // Add Images To User
                const imagesArray: IImage[] = await this.getImagesFromUser(user.id);
                user.images = imagesArray;
                // Add Gender Preferences To User
                const genderPreferenceArray: IGenderPreference[] = await this.getGenderPreferencesFromUser(user.id);
                user.genderPreferences = genderPreferenceArray;
                return user;
            }));
        }else{
            return (async (): Promise<IUser> => {
                // Add Images To User
                const imagesArray: IImage[] = await this.getImagesFromUser(result.id);
                result.images = imagesArray;
                // Add Gender Preferences To User
                const genderPreferenceArray: IGenderPreference[] = await this.getGenderPreferencesFromUser(result.id);
                result.genderPreferences = genderPreferenceArray;
                return result;
            })();
        }
    }

    /**
     * GET IMAGES OF USER-ID (We don't compare 'eliminationDate' field IS NULL, we return all the images, then the user can choose what image want do "Undo Delete Operation". Android only shows images with 'eliminationDate = NULL')
     * @param userId 
     */
    static getImagesFromUser(userId: number): Promise<IImage[]>{
        return new Promise((resolve, reject) => {
            conexion.query('SELECT * FROM image WHERE user = ?', [userId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result);
            });
        });
    }

    /**
     * GET GENDER PREFERENCES OF USER-ID
     * @param userId 
     */
    static getGenderPreferencesFromUser(userId: number): Promise<IGenderPreference[]>{
        return new Promise((resolve, reject) => {
            conexion.query('SELECT * FROM gender_preference WHERE user = ?', [userId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result);
            });
        });
    }

    /**
     * DELETE TOKEN OF DROP OUT USER
     * @param userId 
     */
    static deleteUserToken(userId: number): Promise<void>{
        return new Promise((resolve, reject) => {
            conexion.query('DELETE FROM auth WHERE user = ?', [userId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
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
    static deleteUnsubscribedUsers(){
        return new Promise((resolve, reject) => {
            this.getAllUnsubscribedUsers().then((users: IUser[]) => {
                // If there ara unsubscribed users
                if(users.length > 0){
                    let arrayUserIds: number[] = users.map(eachUser => eachUser.id);
                    let arrayImages: IImage[] = users.map((eachUser: IUser) => eachUser.images.map((eachImage: IImage) => eachImage)).reduce((prev, curr) => prev.concat(curr));
                    conexion.query('UPDATE user SET |||| -- anonimize data -- |||| WHERE disabled < (NOW() - INTERVAL 60 MONTH) and id IN (?)', [arrayUserIds], (error, result, fields) => {
                        if(error)
                            return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                        else
                            return resolve(arrayImages);
                    });
                }else return reject('No unsibscribed users');
            }).catch((error: string) => reject(error));
        });
    }

    /**
     * GET ALL USERS TO BE UNSUBSCRIBED - (JOB 'checkUsersToDelete')                    ---------------------------- SIN USAR
     */
    static getAllUnsubscribedUsers(): Promise<IUser[]>{
        return new Promise((resolve, reject) => {
            conexion.query('SELECT * FROM user WHERE disabled IS NOT NULL', (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return this.setImagesAndGenderPreferencesToUser(result)
                    .then((usersArray: IUser[]) => resolve(usersArray))
                    .catch(error => reject(error));
            });
        });
    }

}