"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express = require('express');
const PlayMatchController_1 = require("../controllers/PlayMatchController");
const HeadersResponse_1 = require("../utils/HeadersResponse");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
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
    PlayMatchController_1.PlayMatchController.listAllQuestions().then((res) => {
        HeadersResponse_1.HeadersResponse.setHeaders(response, 200);
        let resultJson = { questions: res };
        response.send(resultJson);
    }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});
/**
 * SAVE ALL ANSWERS
 */
router.post('/playmatch/answers', (request, response) => {
    let answers = request.body.answers;
    PlayMatchController_1.PlayMatchController.insertAllAnswers(answers).then((res) => {
        HeadersResponse_1.HeadersResponse.setHeaders(response, 201);
        console.log("QUE HAY; " + JSON.stringify(res));
        response.send(res);
    }).catch((error) => GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, error));
});
module.exports = router;
//# sourceMappingURL=playmatch.js.map