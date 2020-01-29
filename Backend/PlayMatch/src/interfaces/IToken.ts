interface ITokenSuccess{
    id: number;
    telephoneNumber: string;
    iat: number;
    exp: number;
}

interface ITokenError{
    name: string;
    message: string;
}

export { ITokenSuccess, ITokenError };