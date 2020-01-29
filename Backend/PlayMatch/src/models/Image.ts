export class Image{

    url: string;
    user: number;
	isProfile: boolean;
	eliminationDate: string;

    constructor(url?: string, user?: number, isProfile?: boolean, eliminationDate?: string){
        this.url = url;
        this.user = user;
		this.isProfile = isProfile;
		this.eliminationDate = eliminationDate;
	}
	
	public getUrl()
	{
		return this.url;
	}

	public setUrl(url: string)
	{
		this.url = url;
	}

	public getUser()
	{
		return this.user;
	}

	public setUser(user: number)
	{
		this.user = user;
	}

	public getIsprofile()
	{
		return this.isProfile;
	}

	public setIsprofile(isProfile: boolean)
	{
		this.isProfile = isProfile;
	}

	public getEliminationDate()
	{
		return this.eliminationDate;
	}

	public setEliminationDate(eliminationDate: string)
	{
		this.eliminationDate = eliminationDate;
	}

}