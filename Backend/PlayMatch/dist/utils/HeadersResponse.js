"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class HeadersResponse {
    /**
     * SET HEADERS
     */
    static setHeaders(response, code, token) {
        if (token)
            response.setHeader('Authorization', token);
        response.setHeader('Content-Type', 'application/json');
        response.statusCode = code;
    }
}
exports.HeadersResponse = HeadersResponse;
//# sourceMappingURL=HeadersResponse.js.map