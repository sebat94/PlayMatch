import { conexion } from "./bdconfig";
import { GenericErrorResponse } from "../utils/GenericErrorResponse";
import { IUser } from "../interfaces/IUser";

export class MatchController {

    /**
     * Add Like
     * @param userId 
     * @param userIdThatILike 
     */
    static addAMatch(userId: number, userIdThatILike: number): Promise<void>{
        return new Promise((resolve, reject) => {
            conexion.query('INSERT INTO \`match\` SET ?', {user1: userId, user2: userIdThatILike}, (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }

    /**
     * Remove Like
     * @param userId 
     * @param userIdThatIDontLikeAnymore 
     */
    static undoAMatch(userId: number, userIdThatIDontLikeAnymore: number): Promise<void>{
        return new Promise((resolve, reject) => {
            conexion.query('DELETE FROM \`match\` WHERE user1 = ? AND user2 = ?', [userId, userIdThatIDontLikeAnymore], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
    
}
