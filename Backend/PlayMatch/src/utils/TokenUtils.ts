import { AuthController } from '../controllers/AuthController';
import { GenericErrorResponse } from './GenericErrorResponse';

export class TokenUtils {

    /**
     * This function Wraps every petition with token validation and executes a real petition inside of them
     * @param request 
     * @param response 
     * @param contentPetition 
     */
    static checkRequestToken(request, response, contentPetition: Function){
        let token: string = request.headers['authorization'];
        let tokenInnerInfo = AuthController.validateToken(token);
        if( tokenInnerInfo.id )                         // If propertie 'id', 'telephoneNumber' or 'iat' exists, it means that the token was verified succesfully
            contentPetition(token, tokenInnerInfo);     // Then executes request specified in controller and pass the same 'token string'(because token never expires) and 'token information'
        else
            GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, 'El Token no es válido');
    }

}