import { IGender } from "../interfaces/IGender";
import { Gender } from "./Gender";

export class User {
	
	telephoneNumber: string;
	nick: string;
	gender: IGender | number;    // "IGender" when we receive data from DDBB to send JSON. And "number" when we insert data in DDBB because don't accept an object but string yes
	birthdate: string;
	email: string;
	password: string;
	lat: number;
	lng: number;
	city: string;
	description: string;
	job: string;
	company: string;
	school: string;
	maxDistancePreference: number;
	minAgePreference: number;
	maxAgePreference: number;
	active: boolean;
	disabled: boolean;

    constructor(telephoneNumber?: string, nick?: string, birthdate?: string, gender?: Gender | number, email?: string, password?: string, lat?: number, lng?: number, city?: string, description?: string, job?: string, company?: string, school?: string, maxDistancePreference?: number, minAgePreference?: number, maxAgePreference?: number, active?: boolean, disabled?: boolean){
		this.telephoneNumber = telephoneNumber;
		this.nick = nick;
		this.birthdate = birthdate;
		this.gender = gender;
		this.email = email;
		this.password = password;
		this.lat = lat;
		this.lng = lng;
		this.city = city;
		this.description = description;
		this.job = job;
		this.company = company;
		this.school = school;
		this.maxDistancePreference = maxDistancePreference;
		this.minAgePreference = minAgePreference;
		this.maxAgePreference = maxAgePreference;
		this.active = active;
		this.disabled = disabled;
	}

    public getTelephoneNumber()
	{
		return this.telephoneNumber;
	}

	public setTelephoneNumber(telephoneNumber: string)
	{
		this.telephoneNumber = telephoneNumber;
	}
	
	public getNick()
	{
		return this.nick;
	}

	public setNick(nick: string)
	{
		this.nick = nick;
	}

	public getBirthdate()
	{
		return this.birthdate;
	}

	public setBirthdate(birthdate: string)
	{
		this.birthdate = birthdate;
	}

	public getGender()
	{
		return this.gender;
	}

	public setGender(gender: Gender | number)
	{
		this.gender = gender;
	}
    
    public getEmail()
	{
		return this.email;
	}

	public setEmail(email: string)
	{
		this.email = email;
	}
	
	public getPassword()
	{
		return this.password;
	}

	public setPassword(password: string)
	{
		this.password = password;
    }

	public getLat()
	{
		return this.lat;
	}

	public setLat(lat: number)
	{
		this.lat = lat;
	}

	public getLng()
	{
		return this.lng;
	}

	public setLng(lng: number)
	{
		this.lng = lng;
	}

	public getCity()
	{
		return this.city;
	}

	public setCity(city: string)
	{
		this.city = city;
	}

	public getDescription()
	{
		return this.description;
	}

	public setDescription(description: string)
	{
		this.description = description;
	}
	
	public getJob()
	{
		return this.job;
	}

	public setJob(job: string)
	{
		this.job = job;
	}

	public getCompany()
	{
		return this.company;
	}

	public setCompany(company: string)
	{
		this.company = company;
	}

	public getSchool()
	{
		return this.school;
	}

	public setSchool(school: string)
	{
		this.school = school;
	}

	public getMaxdistancepreference()
	{
		return this.maxDistancePreference;
	}

	public setMaxdistancepreference(maxDistancePreference: number)
	{
		this.maxDistancePreference = maxDistancePreference;
	}

	public getMinagepreference()
	{
		return this.minAgePreference;
	}

	public setMinagepreference(minAgePreference: number)
	{
		this.minAgePreference = minAgePreference;
	}

	public getMaxagepreference()
	{
		return this.maxAgePreference;
	}

	public setMaxagepreference(maxAgePreference: number)
	{
		this.maxAgePreference = maxAgePreference;
	}

	public getActive()
	{
		return this.active;
	}

	public setActive(active: boolean)
	{
		this.active = active;
	}

	public getDisabled()
	{
		return this.disabled;
	}

	public setDisabled(disabled: boolean)
	{
		this.disabled = disabled;
	}

}