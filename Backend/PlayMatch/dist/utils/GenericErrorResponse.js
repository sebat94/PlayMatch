"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const HeadersResponse_1 = require("./HeadersResponse");
class GenericErrorResponse {
    /**
     * Get error of any query and returns error value as a string
     * @param error
     */
    static getMySqlErrorMessage(error) {
        return `Sql error --> State: ${error.sqlState}, Message: ${error.sqlMessage}, Sql: ${error.sql}`;
    }
    /**
     * Response Error, Token is optional because if register fails you will not receive the token.
     * @param response
     * @param statusCode
     * @param errorMessage
     */
    static setHeadersAndSendErrorResponse(response, statusCode, errorMessage, token) {
        HeadersResponse_1.HeadersResponse.setHeaders(response, statusCode, token);
        let resultJson = { errorMessage: errorMessage };
        response.send(resultJson);
    }
}
exports.GenericErrorResponse = GenericErrorResponse;
//# sourceMappingURL=GenericErrorResponse.js.map