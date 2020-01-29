export class Message{

    chat: number;
    userSender: number;
    userReceiver: number;
    message: string;
    date: string;

    constructor(chat: number, userSender: number, userReceiver: number, message: string, date?: string){
        this.chat = chat;
        this.userSender = userSender;
        this.userReceiver = userReceiver;
        this.message = message;
        this.date = date;
    }

    public getChat()
	{
		return this.chat;
	}

	public setChat(chat: number)
	{
		this.chat = chat;
	}

	public getUserSender()
	{
		return this.userSender;
	}

	public setUserSender(userSender: number)
	{
		this.userSender = userSender;
    }
    
    public getUserReceiver()
	{
		return this.userReceiver;
	}

	public setUserReceiver(userReceiver: number)
	{
		this.userReceiver = userReceiver;
	}

	public getMessage()
	{
		return this.message;
	}

	public setMessage(message: string)
	{
		this.message = message;
	}

	public getDate()
	{
		return this.date;
	}

	public setDate(date: string)
	{
		this.date = date;
	}

}