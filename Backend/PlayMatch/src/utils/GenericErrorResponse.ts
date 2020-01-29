import { MysqlError } from "mysql";
import { HeadersResponse } from "./HeadersResponse";
import { IGenericError } from "../interfaces/IError";

export class GenericErrorResponse {

    /**
     * Get error of any query and returns error value as a string
     * @param error
     */
    static getMySqlErrorMessage(error: MysqlError): string {
        return `Sql error --> State: ${error.sqlState}, Message: ${error.sqlMessage}, Sql: ${error.sql}`;
    }

    /**
     * Response Error, Token is optional because if register fails you will not receive the token.
     * @param response 
     * @param statusCode 
     * @param errorMessage 
     */
    static setHeadersAndSendErrorResponse(response, statusCode: number, errorMessage: string, token?: string){
        HeadersResponse.setHeaders(response, statusCode, token);
        let resultJson: IGenericError = {errorMessage: errorMessage};
        response.send(resultJson);
    }

}