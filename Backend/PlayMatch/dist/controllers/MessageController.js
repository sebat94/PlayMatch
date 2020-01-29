"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const bdconfig_1 = require("./bdconfig");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
class MessageController {
    /**
     * GET ALL CHATS OF USER ID AND THEIR LAST MESSAGE SENDED ORDERED BY MESSAGE DATE (MESSAGE: NULL - IF NOT EXISTS)
     * @param chatId
     */
    static getMessagesFromChat(userId, chatId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('SELECT id, chat, userSender, userReceiver, message, date_format(date,"%Y-%m-%d %H:%i:%s") AS date FROM message WHERE (userSender = ? OR userReceiver = ?) AND chat = ? ORDER BY date ASC', [userId, userId, chatId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve({ 'messages': result });
            });
        });
    }
    /**
     * GET MESSAGE BY ID
     * @param messageId
     */
    static getMessageById(messageId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('SELECT id, chat, userSender, userReceiver, message, date_format(date,"%Y-%m-%d %H:%i:%s") AS date FROM message WHERE id = ?', [messageId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    //return resolve({'message': result[0]});
                    return resolve(result[0]);
            });
        });
    }
    /**
     * ADD NEW MESSAGE
     * @param message
     */
    static addMessage(message) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('INSERT INTO message SET ?', message, (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    this.getMessageById(result.insertId)
                        .then((result) => resolve(result))
                        .catch(error => reject(error));
            });
        });
    }
    /**
     * DELETE MESSAGE SENDED
     * @param idChat
     * @param idMessage
     */
    static deleteMessage(userId, idChat, idMessage) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('DELETE FROM message WHERE userSender = ? AND chat = ? AND id = ?', [userId, idChat, idMessage], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
}
exports.MessageController = MessageController;
//# sourceMappingURL=MessageController.js.map