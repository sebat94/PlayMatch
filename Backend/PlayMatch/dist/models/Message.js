"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Message {
    constructor(chat, userSender, userReceiver, message, date) {
        this.chat = chat;
        this.userSender = userSender;
        this.userReceiver = userReceiver;
        this.message = message;
        this.date = date;
    }
    getChat() {
        return this.chat;
    }
    setChat(chat) {
        this.chat = chat;
    }
    getUserSender() {
        return this.userSender;
    }
    setUserSender(userSender) {
        this.userSender = userSender;
    }
    getUserReceiver() {
        return this.userReceiver;
    }
    setUserReceiver(userReceiver) {
        this.userReceiver = userReceiver;
    }
    getMessage() {
        return this.message;
    }
    setMessage(message) {
        this.message = message;
    }
    getDate() {
        return this.date;
    }
    setDate(date) {
        this.date = date;
    }
}
exports.Message = Message;
//# sourceMappingURL=Message.js.map