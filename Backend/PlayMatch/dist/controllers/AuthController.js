"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const jwt = __importStar(require("jsonwebtoken"));
const bdconfig_1 = require("./bdconfig");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
const secreto = 'PlayMatch';
class AuthController {
    /**
     * Check if user exists before sned the token
     * @param usuarioJSON
     */
    static validateUser(usuarioJSON) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('SELECT * FROM user', (error, resultado, campos) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else {
                    let userOk = resultado.filter((user) => user.telephoneNumber === usuarioJSON.telephoneNumber);
                    if (userOk.length > 0) {
                        let token = this.generateToken(userOk[0].id, userOk[0].telephoneNumber);
                        return resolve(token);
                    }
                    else {
                        let errorMessage = 'El Usuario no existe';
                        return reject(errorMessage);
                    }
                }
            });
        });
    }
    /**
     * Generate tokens
     * @param id
     * @param telephoneNumber
     */
    static generateToken(id, telephoneNumber) {
        let token = jwt.sign({ id: id, telephoneNumber: telephoneNumber }, secreto); // , {expiresIn:'7 days'}
        return `Bearer ${token}`;
    }
    /**
     * Check if token received is valid
     * @param token
     */
    static validateToken(token) {
        try {
            let isValidToken;
            jwt.verify(token.replace('Bearer ', ''), secreto, (error, userToken) => {
                // error --> {"name":"JsonWebTokenError","message":"invalid signature"}
                // userToken --> {"id":4,"telephoneNumber":"333333333","iat":1543147802}
                if (error)
                    isValidToken = error;
                else
                    isValidToken = userToken;
            });
            return isValidToken;
        }
        catch (e) {
            console.error('Error al validar el token: ' + e);
        }
    }
}
exports.AuthController = AuthController;
//# sourceMappingURL=AuthController.js.map