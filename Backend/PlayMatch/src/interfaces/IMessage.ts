interface IMessage{
    id?: number;
    chat?: number;
    userSender?: number;
    userReceiver?: number;
    message?: string;
    date?: string;
}

interface IMessages{
    messages: IMessage[]
}

export { IMessage, IMessages };