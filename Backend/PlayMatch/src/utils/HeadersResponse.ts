export class HeadersResponse{

    /**
     * SET HEADERS
     */
    static setHeaders(response, code: number, token?: string){
        if(token) response.setHeader('Authorization', token);
        response.setHeader('Content-Type', 'application/json');
        response.statusCode = code;
    }

}