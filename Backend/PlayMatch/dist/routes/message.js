"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express = require('express');
const TokenUtils_1 = require("../utils/TokenUtils");
const HeadersResponse_1 = require("../utils/HeadersResponse");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
const MessageController_1 = require("../controllers/MessageController");
let router = express.Router();
/**
* setHeader() works on two conditions:
* 1. You haven't already used writeHead() (i.e. you can't set the header if its already been sent)
* 2. You haven't sent a piece of the body ie. res.write() or res.end
*/
/**
 * GET ALL CHAT MESSAGES
 */
router.get('/message/chat/:id', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let chatId = request.params.id;
            MessageController_1.MessageController.getMessagesFromChat(tokenInnerInfo.id, chatId).then((res) => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                response.send(res);
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * GET MESSAGES BY ID
 */
router.get('/message/:id', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let messageId = request.params.id;
            MessageController_1.MessageController.getMessageById(messageId).then((res) => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200, token);
                response.send({ 'message': res });
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * ADD NEW MESSAGE TO CHAT
 */
router.post('/message/add', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let newMessage = request.body;
            MessageController_1.MessageController.addMessage(newMessage).then((res) => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 201, token);
                response.send(res);
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
/**
 * DELETE MESSAGE SENDED
 */
router.delete('/message/delete/:idChat/:idMessage', (request, response) => {
    TokenUtils_1.TokenUtils.checkRequestToken(request, response, (token, tokenInnerInfo) => {
        new Promise((resolve, reject) => {
            let idChat = request.params.idChat;
            let idMessage = request.params.idMessage;
            MessageController_1.MessageController.deleteMessage(tokenInnerInfo.id, idChat, idMessage).then(() => {
                HeadersResponse_1.HeadersResponse.setHeaders(response, 200);
                response.send();
            }).catch((error) => reject(error));
        }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
    });
});
module.exports = router;
//# sourceMappingURL=message.js.map