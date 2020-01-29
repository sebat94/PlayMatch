export class Chat{

    matchUser1: number;
    matchUser2: number;

    constructor(matchUser1?: number, matchUser2?: number){
        this.matchUser1 = matchUser1;
        this.matchUser2 = matchUser2;
    }

    public getMatchUser1()
	{
		return this.matchUser1;
	}

	public setMatchUser1(matchUser1: number)
	{
		this.matchUser1 = matchUser1;
	}

	public getMatchUser2()
	{
		return this.matchUser2;
	}

	public setMatchUser2(matchUser2: number)
	{
		this.matchUser2 = matchUser2;
    }

}