"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express = require('express');
const TokenUtils_1 = require("../utils/TokenUtils");
const HeadersResponse_1 = require("../utils/HeadersResponse");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
const FavouriteController_1 = require("../controllers/FavouriteController");
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
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            FavouriteController_1.FavouriteController.getMyFavourites(tokenInnerInfo.id).then((res) => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200);
                response.send(res);
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * GET ALL THE PEOPLE THAT HAVE ME IN THEIR FAVOURITES
 */
router.get('/favourite/yours', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            FavouriteController_1.FavouriteController.getUsersThahHaveMeInFavourites(tokenInnerInfo.id).then((res) => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200);
                response.send(res);
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * ADD PERSON TO FAVOURITES
 */
router.post('/favourite/add', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let newFavouriteUser = request.body.user;
            FavouriteController_1.FavouriteController.addFavourite(tokenInnerInfo.id, newFavouriteUser.id).then(() => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 201, token);
                response.end();
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * DELETE PERSON OF FAVOURITES
 */
router.delete('/favourite/delete/:id', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let favouriteToDelete = request.params.id;
            FavouriteController_1.FavouriteController.removeFavourite(tokenInnerInfo.id, favouriteToDelete).then(() => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200);
                response.send();
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
module.exports = router;
//# sourceMappingURL=favourite.js.map