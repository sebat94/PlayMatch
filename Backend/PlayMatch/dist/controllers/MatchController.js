"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const bdconfig_1 = require("./bdconfig");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
class MatchController {
    /**
     * Add Like
     * @param userId
     * @param userIdThatILike
     */
    static addAMatch(userId, userIdThatILike) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('INSERT INTO \`match\` SET ?', { user1: userId, user2: userIdThatILike }, (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
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
    static undoAMatch(userId, userIdThatIDontLikeAnymore) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('DELETE FROM \`match\` WHERE user1 = ? AND user2 = ?', [userId, userIdThatIDontLikeAnymore], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
}
exports.MatchController = MatchController;
//# sourceMappingURL=MatchController.js.map