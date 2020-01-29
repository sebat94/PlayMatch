import { UserController } from './UserController';
import { conexion } from "./bdconfig";
import { GenericErrorResponse } from "../utils/GenericErrorResponse";
import { IUser } from '../interfaces/IUser';
import { IChatWithLastMessageAndUserInfo, IChatWithLastMessage, IChatListWithLastMessageAndUserInfo } from '../interfaces/IChat';

export class ChatController {

    /**
     * GET ALL CHATS OF USER ID AND THEIR LAST MESSAGE SENDED ORDERED BY MESSAGE DATE (MESSAGE: NULL - IF NOT EXISTS)
     * @param userId 
     */
    static getAllChats(userId: number): Promise<IChatListWithLastMessageAndUserInfo>{
        return new Promise((resolve, reject) => {
            conexion.query(`SELECT 
                            c.id as 'chatId', 
                            c.matchUser1, 
                            c.matchUser2, 
                            m.id as 'messageId', 
                            m.chat,
                            m.userSender,
                            m.userReceiver,
                            m.message, 
                            m.date 
                            FROM \`chat\` as c
                            LEFT JOIN \`message\` as m ON c.id = m.chat 
                            WHERE c.matchUser1 = ? OR c.matchUser2 = ? 
                            ORDER BY m.date DESC LIMIT 1`, [userId, userId], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else{
                    let arrayMyChats: IChatWithLastMessage[] = result;
                    // We collect the id of the user contrary to ours
                    let arrayUserIds = arrayMyChats.map((eachChat: IChatWithLastMessage) => (eachChat.matchUser1 === userId) ? eachChat.matchUser2 : eachChat.matchUser1);
                    Promise.all(arrayUserIds.map(async (eachUserId: number, i: number) => {
                        return await UserController.findUserById(eachUserId).then((res: IUser) => {
                            let responseJson: IChatWithLastMessageAndUserInfo = {chat: null, message: null, user: null};
                            // Chat
                            responseJson.chat = {id: arrayMyChats[i].chatId, matchUser1: arrayMyChats[i].matchUser1, matchUser2: arrayMyChats[i].matchUser2};
                            // Message (Show last message if exists)
                            if(arrayMyChats[i].messageId !== null) responseJson.message = {id: arrayMyChats[i].messageId, chat: arrayMyChats[i].chat, userSender: arrayMyChats[i].userSender, userReceiver: arrayMyChats[i].userReceiver, message: arrayMyChats[i].message, date: arrayMyChats[i].date};
                            // User
                            responseJson.user = res;
                            return responseJson;
                        }).catch((error: string) => reject(error));
                    })).then((res: IChatWithLastMessageAndUserInfo[]) => {
                        // Wrap Raw Array into Json Object With a propertie called 'chats'
                        let jsonResponse: IChatListWithLastMessageAndUserInfo = {'chats': res};
                        resolve(jsonResponse);
                    }).catch((error: string) => reject('Some promise has not been made correctly: ' + error));
                }
            });
        });
    }
    
}