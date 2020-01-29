const express = require('express');

import { TokenUtils } from '../utils/TokenUtils';
import { HeadersResponse } from '../utils/HeadersResponse';
import { GenericErrorResponse } from '../utils/GenericErrorResponse';
import { ITokenSuccess } from '../interfaces/IToken';
import { ChatController } from '../controllers/ChatController';
import { IChatListWithLastMessageAndUserInfo } from '../interfaces/IChat';


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
    TokenUtils.checkRequestToken(
        request,
        response,
        (token: string, tokenInnerInfo: ITokenSuccess) => {
            new Promise((resolve, reject) => {
                ChatController.getAllChats(tokenInnerInfo.id).then((res: IChatListWithLastMessageAndUserInfo) => {
                    HeadersResponse.setHeaders(response, 200, token);

                    response.send(res);
                }).catch((error: string) => reject(error));
            }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error, token));
        }
    );
});

module.exports = router;