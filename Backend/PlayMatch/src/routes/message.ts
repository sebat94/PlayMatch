const express = require('express');

import { TokenUtils } from '../utils/TokenUtils';
import { HeadersResponse } from '../utils/HeadersResponse';
import { GenericErrorResponse } from '../utils/GenericErrorResponse';
import { ITokenSuccess } from '../interfaces/IToken';
import { MessageController } from '../controllers/MessageController';
import { IImage } from '../interfaces/IImage';
import { IMessage, IMessages } from '../interfaces/IMessage';


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
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let chatId = request.params.id;
                MessageController.getMessagesFromChat(tokenInnerInfo.id, chatId).then((res: IMessages) => {
                    HeadersResponse.setHeaders(response, 200, token);
                    response.send(res);
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

/**
 * GET MESSAGES BY ID
 */
router.get('/message/:id', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let messageId = request.params.id;
                MessageController.getMessageById(messageId).then((res: IMessage) => {
                    HeadersResponse.setHeaders(response, 200, token);
                    response.send({'message': res});
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

/**
 * ADD NEW MESSAGE TO CHAT
 */
router.post('/message/add', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let newMessage: IMessage = request.body;
                MessageController.addMessage(newMessage).then((res: IMessage) => {
                    HeadersResponse.setHeaders(response, 201, token);
                    response.send(res);
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

/**
 * DELETE MESSAGE SENDED
 */
router.delete('/message/delete/:idChat/:idMessage', (request, response) => {
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                let idChat: number = request.params.idChat;
                let idMessage: number = request.params.idMessage;
                MessageController.deleteMessage(tokenInnerInfo.id, idChat, idMessage).then(() => {
                    HeadersResponse.setHeaders(response, 200);
                    response.send();
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

module.exports = router;