import { IGender } from "./IGender";
import { IImage } from "./IImage";
import { IGenderPreference } from "./IGenderPreference";

interface IUser{
    id?: number;
    telephoneNumber?: string;
    nick?: string;
    gender?: IGender | number;      // "IGender" when we receive data from DDBB. And "number" when we insert data in DDBB
    birthdate?: string;
    email?: string;
    password?: string;
    lat?: number;
    lng?: number;
    city?: string;
    description?: string;
	job?: string;
	company?: string;
    school?: string;
    maxDistancePreference?: number;
	minAgePreference?: number;
    maxAgePreference?: number;
    active?: boolean;
    disabled?: boolean;
    images?: IImage[];              // It isn't a field of entity "User" in DDBB
    genderPreferences?: IGenderPreference[];
}

interface IUserResponse{
    user: IUser;
}

interface IUsersResponse{
    users: IUser[];
}

export { IUser, IUserResponse, IUsersResponse };