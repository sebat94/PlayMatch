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
const UserController_1 = require("./UserController");
const bdconfig_1 = require("./bdconfig");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
class ChatController {
    /**
     * GET ALL CHATS OF USER ID AND THEIR LAST MESSAGE SENDED ORDERED BY MESSAGE DATE (MESSAGE: NULL - IF NOT EXISTS)
     * @param userId
     */
    static getAllChats(userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query(`SELECT 
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
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else {
                    let arrayMyChats = result;
                    // We collect the id of the user contrary to ours
                    let arrayUserIds = arrayMyChats.map((eachChat) => (eachChat.matchUser1 === userId) ? eachChat.matchUser2 : eachChat.matchUser1);
                    Promise.all(arrayUserIds.map((eachUserId, i) => __awaiter(this, void 0, void 0, function* () {
                        return yield UserController_1.UserController.findUserById(eachUserId).then((res) => {
                            let responseJson = { chat: null, message: null, user: null };
                            // Chat
                            responseJson.chat = { id: arrayMyChats[i].chatId, matchUser1: arrayMyChats[i].matchUser1, matchUser2: arrayMyChats[i].matchUser2 };
                            // Message (Show last message if exists)
                            if (arrayMyChats[i].messageId !== null)
                                responseJson.message = { id: arrayMyChats[i].messageId, chat: arrayMyChats[i].chat, userSender: arrayMyChats[i].userSender, userReceiver: arrayMyChats[i].userReceiver, message: arrayMyChats[i].message, date: arrayMyChats[i].date };
                            // User
                            responseJson.user = res;
                            return responseJson;
                        }).catch((error) => reject(error));
                    }))).then((res) => {
                        // Wrap Raw Array into Json Object With a propertie called 'chats'
                        let jsonResponse = { 'chats': res };
                        resolve(jsonResponse);
                    }).catch((error) => reject('Some promise has not been made correctly: ' + error));
                }
            });
        });
    }
}
exports.ChatController = ChatController;
//# sourceMappingURL=ChatController.js.map