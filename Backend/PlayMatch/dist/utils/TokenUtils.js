"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const AuthController_1 = require("../controllers/AuthController");
const GenericErrorResponse_1 = require("./GenericErrorResponse");
class TokenUtils {
    /**
     * This function Wraps every petition with token validation and executes a real petition inside of them
     * @param request
     * @param response
     * @param contentPetition
     */
    static checkRequestToken(request, response, contentPetition) {
        let token = request.headers['authorization'];
        let tokenInnerInfo = AuthController_1.AuthController.validateToken(token);
        if (tokenInnerInfo.id) // If propertie 'id', 'telephoneNumber' or 'iat' exists, it means that the token was verified succesfully
            contentPetition(token, tokenInnerInfo); // Then executes request specified in controller and pass the same 'token string'(because token never expires) and 'token information'
        else
            GenericErrorResponse_1.GenericErrorResponse.setHeadersAndSendErrorResponse(response, 409, 'El Token no es válido');
    }
}
exports.TokenUtils = TokenUtils;
//# sourceMappingURL=TokenUtils.js.map