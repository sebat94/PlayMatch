const express = require('express');

import { TokenUtils } from '../utils/TokenUtils';
import { IUser } from '../interfaces/IUser';
import { HeadersResponse } from '../utils/HeadersResponse';
import { GenericErrorResponse } from '../utils/GenericErrorResponse';
import { MatchController } from '../controllers/MatchController';
import { ITokenSuccess } from '../interfaces/IToken';


let router = express.Router();

/**
* setHeader() works on two conditions:
* 1. You haven't already used writeHead() (i.e. you can't set the header if its already been sent)
* 2. You haven't sent a piece of the body ie. res.write() or res.end
*/

/**
 * ADD A LIKE INTO MATCH TABLE
 */
router.post('/match/add', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let userThatILike: IUser = request.body.user;
                MatchController.addAMatch(tokenInnerInfo.id, userThatILike.id).then(() => {
                    HeadersResponse.setHeaders(response, 201, token);
                    response.end();
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

/**
 * UNDO A LIKE INTO A MATCH TABLE
 */
router.delete('/match/delete/:id', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let userIdThatIDontLikeAnymore: number = request.params.id;
                MatchController.undoAMatch(tokenInnerInfo.id, userIdThatIDontLikeAnymore).then(() => {
                    HeadersResponse.setHeaders(response, 200, token);
                    response.send();
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

// TODO: Ver todos los que me gustan
// TODO: Ver todos a los que le gusto

module.exports = router;