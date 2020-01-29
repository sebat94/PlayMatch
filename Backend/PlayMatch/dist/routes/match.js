"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express = require('express');
const TokenUtils_1 = require("../utils/TokenUtils");
const HeadersResponse_1 = require("../utils/HeadersResponse");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
const MatchController_1 = require("../controllers/MatchController");
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
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let userThatILike = request.body.user;
            MatchController_1.MatchController.addAMatch(tokenInnerInfo.id, userThatILike.id).then(() => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 201, token);
                response.end();
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * UNDO A LIKE INTO A MATCH TABLE
 */
router.delete('/match/delete/:id', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let userIdThatIDontLikeAnymore = request.params.id;
            MatchController_1.MatchController.undoAMatch(tokenInnerInfo.id, userIdThatIDontLikeAnymore).then(() => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                response.send();
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
// TODO: Ver todos los que me gustan
// TODO: Ver todos a los que le gusto
module.exports = router;
//# sourceMappingURL=match.js.map