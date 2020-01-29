"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express = require('express');
const sha256 = require('sha256');
const UserController_1 = require("../controllers/UserController");
const HeadersResponse_1 = require("../utils/HeadersResponse");
const TokenUtils_1 = require("../utils/TokenUtils");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
const ImageController_1 = require("../controllers/ImageController");
const ImageUtils_1 = require("../utils/ImageUtils");
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
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            UserController_1.UserController.listAllUsers(tokenInnerInfo.id).then((res) => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                let resultJson = { users: res };
                response.send(resultJson);
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * GET USER BY ID
 */
router.get('/user/:id', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token) => {
        new Promise((resolve, reject) => {
            let id = request.params.id;
            UserController_1.UserController.findUserById(id).then((res) => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                let resultJson = { user: res };
                response.send(resultJson);
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * UPDATE INFO USER
 */
router.patch('/user/me/update', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let userJson = request.body.user;
            // Encrypt password if exists
            if (userJson.password)
                userJson.password = sha256(userJson.password);
            UserController_1.UserController.updateUser(userJson, tokenInnerInfo.id).then((res) => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                let resultJson = { user: res };
                response.send(resultJson);
            }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        });
    });
});
/**
 * DROP OUT USER - EMPTY BODY BECAUSE SEND 'ACTIVE' PROPERTIE TO NULL IS REDUNDANT
 */
router.patch('/user/me/dropOut', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            UserController_1.UserController.dropOutUser(tokenInnerInfo.id).then(() => {
                ImageController_1.ImageController.getUrlImagesToDelete(tokenInnerInfo.id).then((res) => {
                    ImageController_1.ImageController.deleteImagesOfUserId(tokenInnerInfo.id).then(() => {
                        let arrayUrlImages = res.map((eachUrlObj) => eachUrlObj.url);
                        ImageUtils_1.ImageUtils.unlinkImages(arrayUrlImages); // Unlink deleted images from public folder
                        HeadersResponse_1.HeadersResponse.setHeaders(response, 200);
                        response.send();
                    }).catch((error) => reject(error));
                }).catch((error) => reject(error));
            }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        });
    });
});
/**
 * GENDER PREfERENCES - IT'S USED WHEN USER CREATES AN ACCOUNT, BEFORE SET 'ACTIVE' PROPERTIE TO TRUE
 */
router.post('/user/me/add/genderPreferences', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let genderPreferences = request.body.genderPreferences;
            let genderIds = genderPreferences.map((eachGenderPreference) => eachGenderPreference.gender);
            UserController_1.UserController.createGenderPreferences(tokenInnerInfo.id, genderIds).then(() => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 201, token);
                response.send();
            }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        });
    });
});
/**
 * UPDATE GENDER PREfERENCES - ':id' can be --> 1(Male), 2(Female) ó 3(Other).
 */
router.delete('/user/me/delete/genderPreferences/:id', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        let genderId = request.params.id;
        new Promise((resolve, reject) => {
            UserController_1.UserController.deleteGenderPreference(tokenInnerInfo.id, genderId).then(() => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                response.send();
            }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        });
    });
});
module.exports = router;
//# sourceMappingURL=user.js.map