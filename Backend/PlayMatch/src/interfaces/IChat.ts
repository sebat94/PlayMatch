import { IUser } from "./IUser";
import { IMessage } from "./IMessage";

interface IChat{
    id: number;
    matchUser1: number;
    matchUser2: number;
}

interface IChatWithLastMessage{
    chatId?: number;
    matchUser1?: number;
    matchUser2?: number;
    messageId?: number;
    chat?: number;
    userSender?: number;
    userReceiver?: number;
    message?: string;
    date?: string;
}

interface IChatWithLastMessageAndUserInfo{
    chat: IChat;
    message?: IMessage;
    user: IUser;
}

interface IChatListWithLastMessageAndUserInfo{
    chats: IChatWithLastMessageAndUserInfo[]
}

export { IChat, IChatWithLastMessage, IChatWithLastMessageAndUserInfo, IChatListWithLastMessageAndUserInfo };