const express = require('express');

import { TokenUtils } from '../utils/TokenUtils';
import { HeadersResponse } from '../utils/HeadersResponse';
import { GenericErrorResponse } from '../utils/GenericErrorResponse';
import { ITokenSuccess } from '../interfaces/IToken';
import { IUser } from '../interfaces/IUser';
import { FavouriteController } from '../controllers/FavouriteController';


let router = express.Router();

/**
* setHeader() works on two conditions:
* 1. You haven't already used writeHead() (i.e. you can't set the header if its already been sent)
* 2. You haven't sent a piece of the body ie. res.write() or res.end
*/

/**
 * GET ALL MY FAVOURITES
 */
router.get('/favourite/mine', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                FavouriteController.getMyFavourites(tokenInnerInfo.id).then((res: IUser[]) => {
                    HeadersResponse.setHeaders(response, 200);
                    response.send(res);
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

/**
 * GET ALL THE PEOPLE THAT HAVE ME IN THEIR FAVOURITES
 */
router.get('/favourite/yours', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                FavouriteController.getUsersThahHaveMeInFavourites(tokenInnerInfo.id).then((res: IUser[]) => {
                    HeadersResponse.setHeaders(response, 200);
                    response.send(res);
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

/**
 * ADD PERSON TO FAVOURITES
 */
router.post('/favourite/add', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let newFavouriteUser: IUser = request.body.user;
                FavouriteController.addFavourite(tokenInnerInfo.id, newFavouriteUser.id).then(() => {
                    HeadersResponse.setHeaders(response, 201, token);
                    response.end();
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

/**
 * DELETE PERSON OF FAVOURITES
 */
router.delete('/favourite/delete/:id', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let favouriteToDelete: number = request.params.id;
                FavouriteController.removeFavourite(tokenInnerInfo.id, favouriteToDelete).then(() => {
                    HeadersResponse.setHeaders(response, 200);
                    response.send();
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

module.exports = router;