import { conexion } from "./bdconfig";
import { GenericErrorResponse } from "../utils/GenericErrorResponse";
import { IMessage, IMessages } from '../interfaces/IMessage';

export class MessageController {

    /**
     * GET ALL CHATS OF USER ID AND THEIR LAST MESSAGE SENDED ORDERED BY MESSAGE DATE (MESSAGE: NULL - IF NOT EXISTS)
     * @param chatId 
     */
    static getMessagesFromChat(userId: number, chatId: number): Promise<IMessages>{
        return new Promise((resolve, reject) => {
            conexion.query('SELECT id, chat, userSender, userReceiver, message, date_format(date,"%Y-%m-%d %H:%i:%s") AS date FROM message WHERE (userSender = ? OR userReceiver = ?) AND chat = ? ORDER BY date ASC', [userId, userId, chatId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve({'messages': result});
            });
        });
    }

    /**
     * GET MESSAGE BY ID
     * @param messageId 
     */
    static getMessageById(messageId: number): Promise<IMessage>{
        return new Promise((resolve, reject) => {
            conexion.query('SELECT id, chat, userSender, userReceiver, message, date_format(date,"%Y-%m-%d %H:%i:%s") AS date FROM message WHERE id = ?', [messageId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
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
    static addMessage(message: IMessage): Promise<IMessage>{
        return new Promise((resolve, reject) => {
            conexion.query('INSERT INTO message SET ?', message, (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    this.getMessageById(result.insertId)
                    .then((result: IMessage) => resolve(result))
                    .catch(error => reject(error));
            });
        });
    }

    /**
     * DELETE MESSAGE SENDED
     * @param idChat 
     * @param idMessage 
     */
    static deleteMessage(userId: number, idChat: number, idMessage: number): Promise<void>{
        return new Promise((resolve, reject) => {
            conexion.query('DELETE FROM message WHERE userSender = ? AND chat = ? AND id = ?', [userId, idChat, idMessage], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
    
}