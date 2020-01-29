const express = require('express');

import { PlayMatchController } from '../controllers/PlayMatchController';
import { HeadersResponse } from '../utils/HeadersResponse';
import { GenericErrorResponse } from '../utils/GenericErrorResponse';
import { IAnswer } from '../interfaces/IAnswer';

let router = express.Router();

/**
* setHeader() works on two conditions:
* 1. You haven't already used writeHead() (i.e. you can't set the header if its already been sent)
* 2. You haven't sent a piece of the body ie. res.write() or res.end
*/

/**
 * GET ALL QUESTIONS
 */
router.get('/playmatch/questions', (request, response) => { 
    PlayMatchController.listAllQuestions().then((res: [{id: number, question: string}]) => {
        HeadersResponse.setHeaders(response, 200);
        let resultJson = {questions: res};
        response.send(resultJson);
    }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});

/**
 * SAVE ALL ANSWERS
 */
router.post('/playmatch/answers', (request, response) => {
    let answers: IAnswer[] = request.body.answers;
    PlayMatchController.insertAllAnswers(answers).then((res: any) => {
        HeadersResponse.setHeaders(response, 201);
        console.log("QUE HAY; " + JSON.stringify(res));
        response.send(res);
    }).catch((error: string) => GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});

module.exports = router;