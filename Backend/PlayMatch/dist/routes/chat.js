"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express = require('express');
const TokenUtils_1 = require("../utils/TokenUtils");
const HeadersResponse_1 = require("../utils/HeadersResponse");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
const ChatController_1 = require("../controllers/ChatController");
let router = express.Router();
/**
* setHeader() works on two conditions:
* 1. You haven't already used writeHead() (i.e. you can't set the header if its already been sent)
* 2. You haven't sent a piece of the body ie. res.write() or res.end
*/
/**
 * GET ALL CHATS
 */
router.get('/chat/all', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            ChatController_1.ChatController.getAllChats(tokenInnerInfo.id).then((res) => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                response.send(res);
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
module.exports = router;
//# sourceMappingURL=chat.js.map