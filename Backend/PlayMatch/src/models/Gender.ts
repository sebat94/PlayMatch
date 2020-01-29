export class Gender{

    gender: string;

    constructor(gender?: string){
        this.gender = gender;
    }

    public getGender()
	{
		return this.gender;
	}

	public setGender(gender: string){
        this.gender = gender;
    }

}