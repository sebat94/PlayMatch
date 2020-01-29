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
const HeadersResponse_1 = require("../utils/HeadersResponse");
const TokenUtils_1 = require("../utils/TokenUtils");
const ImageUtils_1 = require("../utils/ImageUtils");
const UserController_1 = require("../controllers/UserController");
const constants_1 = require("../utils/constants");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
const ImageController_1 = require("../controllers/ImageController");
let router = express.Router();
/**
* setHeader() works on two conditions:
* 1. You haven't already used writeHead() (i.e. you can't set the header if its already been sent)
* 2. You haven't sent a piece of the body ie. res.write() or res.end
*/
/**
 * ADD IMAGE/S TO USER
 */
router.post('/user/me/add/images', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            // If 'images' propertie in the body exists then...
            let data = request.body.images;
            if (data) {
                // Write and Save image/s
                if (data instanceof Array) {
                    // Rescue User To See How Many Images Have Already Stored And Allow / Deny This Operation
                    UserController_1.UserController.findUserById(tokenInnerInfo.id).then((res) => {
                        const totalNumImages = data.length + res.images.length;
                        if (totalNumImages <= constants_1.MAX_NUM_USER_IMAGES) {
                            let statePromiseAll = { exit: false, errorMessage: "" };
                            Promise.all(data.map((eachImageBase64) => __awaiter(this, void 0, void 0, function* () {
                                const imageName = `${tokenInnerInfo.id}_${+new Date()}.png`;
                                ImageUtils_1.ImageUtils.createImageFromBase64(eachImageBase64, imageName, constants_1.ROUTE_USER_IMAGES);
                                yield ImageUtils_1.ImageUtils.insertRowAfterSaveImage(`${imageName}`, tokenInnerInfo.id).then(() => console.log("Correctly inserted image")).catch((error) => {
                                    // We need to save error because 'reject()' is not stopping 'Promise.all()'. Then, when Promise.all() enter to 'then()' clause, just check boolean and reject there.
                                    statePromiseAll.exit = true;
                                    statePromiseAll.errorMessage = error;
                                });
                            }))).then(() => {
                                // If there are any error with some image inserted in DDBB, we reject promise and show errorMessage
                                if (statePromiseAll.exit)
                                    return reject(statePromiseAll.errorMessage);
                                // After inserting the image/s, if they haven't returned an error we send the user with the images
                                // We Need To Take Again The User, the previous lambda with User it isn't usefull because we need obtain the newly added images
                                UserController_1.UserController.findUserById(tokenInnerInfo.id).then((res) => {
                                    HeadersResponse_1.HeadersResponse.setHeaders(response, 201, token);
                                    let resultJson = { user: res };
                                    response.send(resultJson);
                                }).catch((error) => reject(error));
                            }).catch((error) => reject('Some promise has not been made correctly: ' + error));
                        }
                        else
                            return reject(`Limit of images exceeded - ${totalNumImages}/${constants_1.MAX_NUM_USER_IMAGES}`);
                    }).catch((error) => reject('Error Getting Stored Images From User: ' + error));
                }
                else
                    return reject('The image/s need to be pushed as an array');
            }
            else
                return reject('Send image/s');
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * DELETE IMAGE
 */
router.delete('/user/me/images/delete/:id', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let imageId = request.params.id;
            ImageController_1.ImageController.getImageById(imageId).then((res) => {
                ImageController_1.ImageController.deleteImage(imageId).then(() => {
                    // Unlink deleted image from public folder
                    ImageUtils_1.ImageUtils.unlinkImages(constants_1.ROUTE_USER_IMAGES + res.url);
                    // Get Complete User To Add It In Response
                    UserController_1.UserController.findUserById(tokenInnerInfo.id).then((res) => {
                        HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                        let resultJson = { user: res };
                        response.send(resultJson);
                    }).catch((error) => reject(error));
                }).catch((error) => reject(error));
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * UPDATE IMAGE/S TO BE DELETED
 */
// router.patch('/user/me/images/delete', (request, response) => {
//     TokenUtils.checkRequestToken(
//         request,
//         response,
//         (token: string, tokenInnerInfo: ITokenSuccess) => {
//             new Promise((resolve, reject) => {
//                 let data: IImage[] = request.body.images;
//                 if(data){
//                     if(data instanceof Array){
//                         let imageIds: number[] = [];
//                         data.forEach(eachImage => imageIds.push(eachImage.id));
//                         ImageController.updateImagesToBeDeleted(imageIds).then(() => {
//                             UserController.findUserById(tokenInnerInfo.id).then((res: IUser) => {
//                                 HeadersResponse.setHeaders(response, 201, token);
//                                 let resultJson: IUserResponse = {user: res};
//                                 response.send(resultJson);
//                             }).catch((error: string) => reject(error));
//                         }).catch((error: string) => reject(error));
//                     }else return reject('The image/s need to be passed through QueryString as an array');
//                 }else return reject('Image/s array not sended');
//             }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
//         }
//     );
// });
/**
 * UPDATE IMAGE/S TO UNDO OPERATION DELETE
 */
// router.patch('/user/me/images/undoDelete', (request, response) => {
//     TokenUtils.checkRequestToken(
//         request,
//         response,
//         (token: string, tokenInnerInfo: ITokenSuccess) => {
//             new Promise((resolve, reject) => {
//                 let data: IImage[] = request.body.images;
//                 if(data){
//                     if(data instanceof Array){
//                         let imageIds: number[] = [];
//                         data.forEach(eachImage => imageIds.push(eachImage.id));
//                         ImageController.updateImagesToUndoOperationDelete(imageIds).then(() => {
//                             UserController.findUserById(tokenInnerInfo.id).then((res: IUser) => {
//                                 HeadersResponse.setHeaders(response, 201, token);
//                                 let resultJson: IUserResponse = {user: res};
//                                 response.send(resultJson);
//                             }).catch((error: string) => reject(error));
//                         }).catch((error: string) => reject(error));
//                     }else return reject('The image/s need to be passed as an array');
//                 }else return reject('Image/s array not sended');
//             }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
//         }
//     );
// });
/**
 * UPDATE IMAGE/S TO SET PROFILE IMAGE
 */
router.patch('/user/me/images/setProfileImage', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let data = request.body.image;
            if (data) {
                ImageController_1.ImageController.updateImageToBeProfilePicture(data.id, tokenInnerInfo.id).then(() => {
                    UserController_1.UserController.findUserById(tokenInnerInfo.id).then((res) => {
                        HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                        let resultJson = { user: res };
                        response.send(resultJson);
                    }).catch((error) => reject(error));
                }).catch((error) => reject(error));
            }
            else
                return reject('Image not sended');
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
module.exports = router;
//# sourceMappingURL=image.js.map