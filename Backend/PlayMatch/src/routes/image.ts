const express = require('express');

import { IUser, IUserResponse } from '../interfaces/IUser';
import { HeadersResponse } from '../utils/HeadersResponse';
import { TokenUtils } from '../utils/TokenUtils';
import { ITokenSuccess } from '../interfaces/IToken';
import { ImageUtils } from '../utils/ImageUtils';
import { UserController } from '../controllers/UserController';
import { ROUTE_USER_IMAGES, MAX_NUM_USER_IMAGES } from '../utils/constants';
import { GenericErrorResponse } from '../utils/GenericErrorResponse';
import { IImage } from '../interfaces/IImage';
import { ImageController } from '../controllers/ImageController';

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
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                // If 'images' propertie in the body exists then...
                let data: string[] = request.body.images;
                if(data){
                    // Write and Save image/s
                    if(data instanceof Array){
                        // Rescue User To See How Many Images Have Already Stored And Allow / Deny This Operation
                        UserController.findUserById(tokenInnerInfo.id).then((res: IUser) => {
                            const totalNumImages: number = data.length + res.images.length;
                            if(totalNumImages <= MAX_NUM_USER_IMAGES){
                                let statePromiseAll: {exit: boolean, errorMessage: string} = {exit: false, errorMessage: ""};
                                Promise.all(data.map(async (eachImageBase64) => {
                                    const imageName = `${tokenInnerInfo.id}_${+new Date()}.png`;
                                    ImageUtils.createImageFromBase64(eachImageBase64, imageName, ROUTE_USER_IMAGES);
                                    await ImageUtils.insertRowAfterSaveImage(`${imageName}`, tokenInnerInfo.id).then(() => console.log("Correctly inserted image")).catch((error: string) => {
                                        // We need to save error because 'reject()' is not stopping 'Promise.all()'. Then, when Promise.all() enter to 'then()' clause, just check boolean and reject there.
                                        statePromiseAll.exit = true;
                                        statePromiseAll.errorMessage = error;
                                    });
                                })).then(() => {
                                    // If there are any error with some image inserted in DDBB, we reject promise and show errorMessage
                                    if(statePromiseAll.exit) return reject(statePromiseAll.errorMessage);
                                    // After inserting the image/s, if they haven't returned an error we send the user with the images
                                    // We Need To Take Again The User, the previous lambda with User it isn't usefull because we need obtain the newly added images
                                    UserController.findUserById(tokenInnerInfo.id).then((res: IUser) => {
                                        HeadersResponse.setHeaders(response, 201, token);
                                        let resultJson: IUserResponse = {user: res};
                                        response.send(resultJson);
                                    }).catch((error: string) => reject(error));
                                }).catch((error: string) => reject('Some promise has not been made correctly: ' + error));
                            }else return reject(`Limit of images exceeded - ${totalNumImages}/${MAX_NUM_USER_IMAGES}`);
                        }).catch((error: string) => reject('Error Getting Stored Images From User: ' + error));
                    }else return reject('The image/s need to be pushed as an array');
                }else return reject('Send image/s');
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

/**
 * DELETE IMAGE
 */
router.delete('/user/me/images/delete/:id', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let imageId: number = request.params.id;
                ImageController.getImageById(imageId).then((res: IImage) => {
                    ImageController.deleteImage(imageId).then(() => {
                        // Unlink deleted image from public folder
                        ImageUtils.unlinkImages(ROUTE_USER_IMAGES + res.url);
                        // Get Complete User To Add It In Response
                        UserController.findUserById(tokenInnerInfo.id).then((res: IUser) => {
                            HeadersResponse.setHeaders(response, 200, token);
                            let resultJson: IUserResponse = {user: res};
                            response.send(resultJson);
                        }).catch((error: string) => reject(error));
                    }).catch((error: string) => reject(error));
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
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
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let data: IImage = request.body.image;
                if(data){
                    ImageController.updateImageToBeProfilePicture(data.id, tokenInnerInfo.id).then(() => {
                        UserController.findUserById(tokenInnerInfo.id).then((res: IUser) => {
                            HeadersResponse.setHeaders(response, 200, token);
                            let resultJson: IUserResponse = {user: res};
                            response.send(resultJson);
                        }).catch((error: string) => reject(error));
                    }).catch((error: string) => reject(error));
                }else return reject('Image not sended');
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

module.exports = router;