const express = require('express');
const sha256 = require('sha256');

import { IUser, IUserResponse } from '../interfaces/IUser';
import { UserController } from '../controllers/UserController';
import { HeadersResponse } from '../utils/HeadersResponse';
import { AuthController } from '../controllers/AuthController';
import { GenericErrorResponse } from './../utils/GenericErrorResponse';
import { ITokenSuccess } from '../interfaces/IToken';
import { TokenUtils } from '../utils/TokenUtils';

import { ISMSRetrieverApi } from '../interfaces/ISMSRetrieverApi';
import { SmsRetrieverUtils } from './../utils/SmsRetrieverUtils';
import { ISMSRetrieverApiVerify } from '../interfaces/ISMSRetrieverApiVerify';


// const accountSid = process.env.TWILIO_ACCOUNT_SID;
// const authToken = process.env.TWILIO_AUTH_TOKEN;
// const from = process.env.FROM;
// Twilio Credentials

// -- LOS CREDENCIALES DE PAGO
const accountSid = 'ACb211aa99ba22536e529001f9091f1959';
const authToken = 'c2e62f867681dcf03d86bfb9fe41f4ce';
const from ='+34911062167';         // --> Phone offered to me by Twilio to Send the Messages

// require the Twilio module and create a REST client
const client = require('twilio')(accountSid, authToken);


let router = express.Router();

/**
* setHeader() works on two conditions:
* 1. You haven't already used writeHead() (i.e. you can't set the header if its already been sent)
* 2. You haven't sent a piece of the body ie. res.write() or res.end
*/

/**
 * SMS Retriever API - Phone Request a SMS with OneTimeCode
 */
router.post('/auth/sms', (request, response) => {
    new Promise((resolve, reject) => {
        // We Receive the "Hash" and the "TelephoneNumber"
        let body: ISMSRetrieverApi = request.body;
        // Get Random Key To Send To The Client
        let oneTimeCode: string = SmsRetrieverUtils.generateOneTimeCode();
        // Create and send message
        client.messages.create(  
            {
              to: body.telephoneNumber,
              from: from,
              body: `<#> Tu código de PlayMatch es ${oneTimeCode}
                    ${body.hash}`
            },
            (err, message) => {     // Only need check error, but message have a lot of properties that we can use
                if(err) reject('Error enviando el mensaje desde Node.js - ' + err);
                else {
                    // Check if Previous SMS Was Sended before and then Delete from Cache previous data
                    SmsRetrieverUtils.cacheRemoveItem(body.telephoneNumber);
                    // Save "telephoneNumber" and "OneTimeCode" in the Cache to be checked  when user introduces the OneTimeCode of The Message Generated
                    SmsRetrieverUtils.cacheStoreItem(body.telephoneNumber, oneTimeCode);

                    response.send();
                }
            }
        );

    }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});

/**
 * SMS Retriever API - User Phone Send a OneTimeCode Received in the SMS To The Server To Validate the Phone
 */
router.post('/auth/sms/verify', (request, response) => {
    new Promise((resolve, reject) => {

        // We Receive the "OneTimeCode" and the "TelephoneNumber"
        let body: ISMSRetrieverApiVerify = request.body;

        console.log(body.oneTimeCode + " - " + SmsRetrieverUtils.cacheGetItem(body.telephoneNumber));
        // If OneTimeCode Generated is Equals to Sended For User is a Verified Phone
        if(SmsRetrieverUtils.cacheGetItem(body.telephoneNumber) === body.oneTimeCode){
            // Clear Cache
            SmsRetrieverUtils.cacheRemoveItem(body.telephoneNumber);
            // Send Response
            response.send();
        }
        else
            reject('El OneTimeCode no coincide');

    }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});

/**
 * Check If is a Valid Token    -   When User Enters to App, Check If Have a Valid Token, And If the Data Inside is Equals to the Data Passed From Url Params
 */
router.get('/auth/token/:id/:telephoneNumber', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                
                let id: number = parseInt(request.params.id);
                let telephoneNumber: string = request.params.telephoneNumber;

                // IF The ID And telephoneNumber of User MATCHES with the ID And telephoneNumber Retrieved from Token, then is a Valid Token And Correct User
                if(id === tokenInnerInfo.id && telephoneNumber === tokenInnerInfo.telephoneNumber){
                    HeadersResponse.setHeaders(response, 200);
                    response.send();
                }else reject('Usuario no válido, logueate de nuevo');

            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

/**
 * REGISTER NEW USER
 *      1) Comprobamos Si existe o no el usuario, y devolvemos el nuevo User Creado o el User que ya existía.
 *      2) Desde Android podremos diferenciar si está haciendo Login/Register comprobando si la propiedad 'Disabled' es Null o no
 */
router.post('/auth/register', (request, response) => {
    new Promise((resolve, reject) => {
        let newUser: IUser = request.body.user;
        // Check If User Exists And Creates It If Doesn't Exists
        UserController.findUserByTelephoneNumber(newUser.telephoneNumber).then((res: IUser) => {
            // If user exists, generate a new token for him, (set propertie 'disabled' to NULL if it was), and return User object.
            if(res) {
                // Generate a New Token
                let token: string = AuthController.generateToken(res.id, res.telephoneNumber);
                let tokenInnerInfo: ITokenSuccess = AuthController.validateToken(token);
                // IF User Exists and was not disabled, return Logged User
                if(res.disabled === null){
                    UserController.findUserById(res.id).then((res: IUser) => {
                        HeadersResponse.setHeaders(response, 200, token);
                        let resultJson: IUserResponse = {user: res};
                        response.send(resultJson);
                    });
                }
                // If User Exists and was disabled, Set propertie 'disabled' to NULL and return Logged User
                else{
                    let userJson: IUser = {'disabled': null};
                    UserController.updateUser(userJson, tokenInnerInfo.id).then((res: IUser) => {
                        HeadersResponse.setHeaders(response, 200, token);
                        let resultJson: IUserResponse = {user: res};
                        response.send(resultJson);
                    });
                }

            //If User Doesn't Exists, Then Create It
            }else{
                // Register New User
                UserController.registerNewUser(newUser).then((res: IUser) => {
                    // Generate token
                    AuthController.validateUser(res).then((token: string) => {
                        HeadersResponse.setHeaders(response, 201, token);
                        let resultJson: IUserResponse = {user: res};
                        response.send(resultJson);
                    }).catch((error: string) => reject(error));
                }).catch((error: string) => reject(error));
            }
        }).catch((error: string) => reject(error));
    }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});

/**
 * Login User
 * 
 * - El login ya no lo utilizamos, ya que con el Registro comprobamos Login/Registro de usuario
 */
router.post('/auth/login', (request, response) => {
    new Promise((resolve, reject) => {
        UserController.findUserByTelephoneNumber(request.body.telephoneNumber).then(async (res: IUser) => {
            // If User Was Unsubscribed, Then Re-Register The User
            if(res.disabled !== null) await UserController.reRegisterUser(res.id).then(() => res.disabled = null).catch((error: string) => reject(error));
            let token: string = AuthController.generateToken(res.id, res.telephoneNumber);
            HeadersResponse.setHeaders(response, 200, token);
            response.send(res);
        }).catch((error: string) => reject(error));
    }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});

module.exports = router;