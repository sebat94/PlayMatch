import * as jwt from 'jsonwebtoken';
import { conexion } from './bdconfig';
import { IUser } from '../interfaces/IUser';
import { GenericErrorResponse } from '../utils/GenericErrorResponse';
import { ITokenSuccess, ITokenError } from '../interfaces/IToken';

const secreto: string = 'PlayMatch';

export class AuthController {

    /**
     * Check if user exists before sned the token
     * @param usuarioJSON 
     */
    static validateUser(usuarioJSON: IUser): Promise<string>{
        return new Promise((resolve, reject) => {
            conexion.query('SELECT * FROM user', (error, resultado, campos) => {        // TODO: Where telephoneNumber = xxxxxxxxx, y quitamos el filter abajo
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else{
                    let userOk = resultado.filter((user: IUser) => user.telephoneNumber === usuarioJSON.telephoneNumber);
                    if (userOk.length > 0) {
                        let token: string = this.generateToken(userOk[0].id, userOk[0].telephoneNumber);
                        return resolve(token);
                    }else{
                        let errorMessage: string = 'El Usuario no existe';
                        return reject(errorMessage);
                    }
                }
            })
        });
    }

    /**
     * Generate tokens
     * @param id 
     * @param telephoneNumber 
     */
    static generateToken(id: number, telephoneNumber: string): string {
        let token = jwt.sign({id: id, telephoneNumber: telephoneNumber}, secreto);  // , {expiresIn:'7 days'}
        return `Bearer ${token}`;
    }

    /**
     * Check if token received is valid
     * @param token 
     */
    static validateToken(token: string) {
        try {
            let isValidToken;
            jwt.verify(token.replace('Bearer ',''), secreto, (error: ITokenError, userToken: ITokenSuccess) => {
                // error --> {"name":"JsonWebTokenError","message":"invalid signature"}
                // userToken --> {"id":4,"telephoneNumber":"333333333","iat":1543147802}
                if (error) isValidToken = error;
                else isValidToken = userToken;
            });
            return isValidToken;
        } catch (e) {
            console.error('Error al validar el token: ' + e);
        }
    }

}
