"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const express = require('express');
const sha256 = require('sha256');
const UserController_1 = require("../controllers/UserController");
const HeadersResponse_1 = require("../utils/HeadersResponse");
const AuthController_1 = require("../controllers/AuthController");
const GenericErrorResponse_1 = require("./../utils/GenericErrorResponse");
const TokenUtils_1 = require("../utils/TokenUtils");
const SmsRetrieverUtils_1 = require("./../utils/SmsRetrieverUtils");
// const accountSid = process.env.TWILIO_ACCOUNT_SID;
// const authToken = process.env.TWILIO_AUTH_TOKEN;
// const from = process.env.FROM;
// Twilio Credentials
// -- LOS CREDENCIALES DE PAGO
const accountSid = 'ACb211aa99ba22536e529001f9091f1959';
const authToken = 'c2e62f867681dcf03d86bfb9fe41f4ce';
const from = '+34911062167'; // --> Phone offered to me by Twilio to Send the Messages
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
        let body = request.body;
        // Get Random Key To Send To The Client
        let oneTimeCode = SmsRetrieverUtils_1.SmsRetrieverUtils.generateOneTimeCode();
        // Create and send message
        client.messages.create({
            to: body.telephoneNumber,
            from: from,
            body: `<#> Tu código de PlayMatch es ${oneTimeCode}
                    ${body.hash}`
        }, (err, message) => {
            if (err)
                reject('Error enviando el mensaje desde Node.js - ' + err);
            else {
                // Check if Previous SMS Was Sended before and then Delete from Cache previous data
                SmsRetrieverUtils_1.SmsRetrieverUtils.cacheRemoveItem(body.telephoneNumber);
                // Save "telephoneNumber" and "OneTimeCode" in the Cache to be checked  when user introduces the OneTimeCode of The Message Generated
                SmsRetrieverUtils_1.SmsRetrieverUtils.cacheStoreItem(body.telephoneNumber, oneTimeCode);
                response.send();
            }
        });
    }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});
/**
 * SMS Retriever API - User Phone Send a OneTimeCode Received in the SMS To The Server To Validate the Phone
 */
router.post('/auth/sms/verify', (request, response) => {
    new Promise((resolve, reject) => {
        // We Receive the "OneTimeCode" and the "TelephoneNumber"
        let body = request.body;
        console.log(body.oneTimeCode + " - " + SmsRetrieverUtils_1.SmsRetrieverUtils.cacheGetItem(body.telephoneNumber));
        // If OneTimeCode Generated is Equals to Sended For User is a Verified Phone
        if (SmsRetrieverUtils_1.SmsRetrieverUtils.cacheGetItem(body.telephoneNumber) === body.oneTimeCode) {
            // Clear Cache
            SmsRetrieverUtils_1.SmsRetrieverUtils.cacheRemoveItem(body.telephoneNumber);
            // Send Response
            response.send();
        }
        else
            reject('El OneTimeCode no coincide');
    }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});
/**
 * Check If is a Valid Token    -   When User Enters to App, Check If Have a Valid Token, And If the Data Inside is Equals to the Data Passed From Url Params
 */
router.get('/auth/token/:id/:telephoneNumber', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let id = parseInt(request.params.id);
            let telephoneNumber = request.params.telephoneNumber;
            // IF The ID And telephoneNumber of User MATCHES with the ID And telephoneNumber Retrieved from Token, then is a Valid Token And Correct User
            if (id === tokenInnerInfo.id && telephoneNumber === tokenInnerInfo.telephoneNumber) {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200);
                response.send();
            }
            else
                reject('Usuario no válido, logueate de nuevo');
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * REGISTER NEW USER
 *      1) Comprobamos Si existe o no el usuario, y devolvemos el nuevo User Creado o el User que ya existía.
 *      2) Desde Android podremos diferenciar si está haciendo Login/Register comprobando si la propiedad 'Disabled' es Null o no
 */
router.post('/auth/register', (request, response) => {
    new Promise((resolve, reject) => {
        let newUser = request.body.user;
        // Check If User Exists And Creates It If Doesn't Exists
        UserController_1.UserController.findUserByTelephoneNumber(newUser.telephoneNumber).then((res) => {
            // If user exists, generate a new token for him, (set propertie 'disabled' to NULL if it was), and return User object.
            if (res) {
                // Generate a New Token
                let token = AuthController_1.AuthController.generateToken(res.id, res.telephoneNumber);
                let tokenInnerInfo = AuthController_1.AuthController.validateToken(token);
                // IF User Exists and was not disabled, return Logged User
                if (res.disabled === null) {
                    UserController_1.UserController.findUserById(res.id).then((res) => {
                        HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                        let resultJson = { user: res };
                        response.send(resultJson);
                    });
                }
                // If User Exists and was disabled, Set propertie 'disabled' to NULL and return Logged User
                else {
                    let userJson = { 'disabled': null };
                    UserController_1.UserController.updateUser(userJson, tokenInnerInfo.id).then((res) => {
                        HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                        let resultJson = { user: res };
                        response.send(resultJson);
                    });
                }
                //If User Doesn't Exists, Then Create It
            }
            else {
                // Register New User
                UserController_1.UserController.registerNewUser(newUser).then((res) => {
                    // Generate token
                    AuthController_1.AuthController.validateUser(res).then((token) => {
                        HeadersResponse_1.HeadersResponse.setHeaders(response, 201, token);
                        let resultJson = { user: res };
                        response.send(resultJson);
                    }).catch((error) => reject(error));
                }).catch((error) => reject(error));
            }
        }).catch((error) => reject(error));
    }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});
/**
 * Login User
 *
 * - El login ya no lo utilizamos, ya que con el Registro comprobamos Login/Registro de usuario
 */
router.post('/auth/login', (request, response) => {
    new Promise((resolve, reject) => {
        UserController_1.UserController.findUserByTelephoneNumber(request.body.telephoneNumber).then((res) => __awaiter(this, void 0, void 0, function* () {
            // If User Was Unsubscribed, Then Re-Register The User
            if (res.disabled !== null)
                yield UserController_1.UserController.reRegisterUser(res.id).then(() => res.disabled = null).catch((error) => reject(error));
            let token = AuthController_1.AuthController.generateToken(res.id, res.telephoneNumber);
            HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
            response.send(res);
        })).catch((error) => reject(error));
    }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});
module.exports = router;
//# sourceMappingURL=auth.js.map