const express = require('express');
const sha256 = require('sha256');

import { IUser, IUserResponse, IUsersResponse } from '../interfaces/IUser';
import { UserController } from '../controllers/UserController';
import { HeadersResponse } from '../utils/HeadersResponse';
import { TokenUtils } from '../utils/TokenUtils';
import { ITokenSuccess } from '../interfaces/IToken';
import { GenericErrorResponse } from '../utils/GenericErrorResponse';
import { IGenderPreference } from '../interfaces/IGenderPreference';
import { ImageController } from '../controllers/ImageController';
import { ImageUtils } from '../utils/ImageUtils';
import { IImage } from '../interfaces/IImage';

let router = express.Router();

/**
* setHeader() works on two conditions:
* 1. You haven't already used writeHead() (i.e. you can't set the header if its already been sent)
* 2. You haven't sent a piece of the body ie. res.write() or res.end
*/

/**
 * GET ALL USERS
 */
router.get('/user/all', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {    // In this case we omit the "telephoneNumber: number" param because here we don't need
            new Promise((resolve, reject) => {
                UserController.listAllUsers(tokenInnerInfo.id).then((res: IUser[]) => {
                    HeadersResponse.setHeaders(response, 200, token);
                    let resultJson: IUsersResponse = {users: res};
                    response.send(resultJson);
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

/**
 * GET USER BY ID
 */
router.get('/user/:id', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string) => {
            new Promise((resolve, reject) => {
                let id: number = request.params.id;
                UserController.findUserById(id).then((res: IUser) => {
                    HeadersResponse.setHeaders(response, 200, token);
                    let resultJson: IUserResponse = {user: res};
                    response.send(resultJson);
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

/**
 * UPDATE INFO USER
 */
router.patch('/user/me/update', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string,  tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let userJson: IUser = request.body.user;
                // Encrypt password if exists
                if(userJson.password) userJson.password = sha256(userJson.password);
                UserController.updateUser(userJson, tokenInnerInfo.id).then((res: IUser) => {
                    HeadersResponse.setHeaders(response, 200, token);
                    let resultJson: IUserResponse = {user: res};
                    response.send(resultJson);
                }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
            });
        }
    );
});

/**
 * DROP OUT USER - EMPTY BODY BECAUSE SEND 'ACTIVE' PROPERTIE TO NULL IS REDUNDANT
 */
router.patch('/user/me/dropOut', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                UserController.dropOutUser(tokenInnerInfo.id).then(() => {
                    ImageController.getUrlImagesToDelete(tokenInnerInfo.id).then((res: IImage[]) => {
                        ImageController.deleteImagesOfUserId(tokenInnerInfo.id).then(() => {
                            let arrayUrlImages: string[] = res.map((eachUrlObj: IImage) => eachUrlObj.url);
                            ImageUtils.unlinkImages(arrayUrlImages);    // Unlink deleted images from public folder
                            HeadersResponse.setHeaders(response, 200);
                            response.send();
                        }).catch((error: string) => reject(error));
                    }).catch((error: string) => reject(error));  
                }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
            });
        }
    );
});

/**
 * GENDER PREfERENCES - IT'S USED WHEN USER CREATES AN ACCOUNT, BEFORE SET 'ACTIVE' PROPERTIE TO TRUE
 */
router.post('/user/me/add/genderPreferences', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string,  tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let genderPreferences: IGenderPreference[] = request.body.genderPreferences;
                let genderIds: number[] = genderPreferences.map((eachGenderPreference: IGenderPreference) => eachGenderPreference.gender);
                UserController.createGenderPreferences(tokenInnerInfo.id, genderIds).then(() => {
                    HeadersResponse.setHeaders(response, 201, token);
                    response.send();
                }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
            });
        }
    );
});

/**
 * UPDATE GENDER PREfERENCES - ':id' can be --> 1(Male), 2(Female) ó 3(Other).
 */
router.delete('/user/me/delete/genderPreferences/:id', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string,  tokenInnerInfo: ITokenSuccess) => {
            let genderId: number = request.params.id;
            new Promise((resolve, reject) => {
                UserController.deleteGenderPreference(tokenInnerInfo.id, genderId).then(() => {
                    HeadersResponse.setHeaders(response, 200, token);
                    response.send();
                }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
            });
        }
    );
});

module.exports = router;