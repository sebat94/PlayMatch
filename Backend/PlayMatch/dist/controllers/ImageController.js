"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const bdconfig_1 = require("./bdconfig");
const GenericErrorResponse_1 = require("../utils/GenericErrorResponse");
class ImageController {
    /**
     * GET IMAGE BY ID
     * @param imageId
     */
    static getImageById(imageId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('SELECT * FROM image WHERE id = ?', imageId, (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    resolve(result[0]);
            });
        });
    }
    /**
     * INSERT IMAGE/S
     * @param image
     */
    static insertImage(image) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('INSERT INTO image SET ?', image, (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else // result --> {"fieldCount":0,"affectedRows":1,"insertId":41,"serverStatus":2,"warningCount":0,"message":"","protocol41":true,"changedRows":0}
                    return resolve(); // We don't need a 'result' data from insert, then just 'resolve()' empty
            });
        });
    }
    static deleteImage(imageId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('DELETE FROM image WHERE id = ?', imageId, (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else // result --> {"fieldCount":0,"affectedRows":1,"insertId":41,"serverStatus":2,"warningCount":0,"message":"","protocol41":true,"changedRows":0}
                    return resolve(); // We don't need a 'result' data from insert, then just 'resolve()' empty
            });
        });
    }
    /**
     * INDICATES IMAGE TO BE REMOVED SETTING THE 'eliminationDate' OF DELETION OF THE IMAGE/S
     * @param imageIds
     */
    // static updateImagesToBeDeleted(imageIds: number[]): Promise<void> {
    //     return new Promise((resolve, reject) => {
    //         conexion.query('UPDATE image SET eliminationDate = NOW() WHERE id IN (?)', [imageIds], (error, result, fields) => {
    //             if(error)
    //                 return reject(GenericErrorResponse.getMySqlErrorMessage(error));
    //             else   // result --> {"fieldCount":0,"affectedRows":1,"insertId":0,"serverStatus":2,"warningCount":0,"message":"","protocol41":true,"changedRows":0}
    //                 return resolve();    // We don't need a 'result' data from insert, then just 'resolve()' empty                
    //         });
    //     });
    // }
    /**
     * UNDO OPERATION OF REMOVE IMAGE/S SETTING THE 'eliminationDate' TO NULL
     * @param imageIds
     */
    // static updateImagesToUndoOperationDelete(imageIds: number[]): Promise<void> {
    //     return new Promise((resolve, reject) => {
    //         conexion.query('UPDATE image SET eliminationDate = null WHERE id IN (?)', [imageIds], (error, result, fields) => {
    //             if(error)
    //                 return reject(GenericErrorResponse.getMySqlErrorMessage(error));
    //             else
    //                 return resolve();
    //         });
    //     });
    // }
    /**
     * UPDATE IMAGE TO BE THE PROFILE PICTURE
     * @param image
     * @param userId
     */
    static updateImageToBeProfilePicture(imageId, userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('UPDATE image SET isProfile = CASE WHEN id = ? THEN 1 ELSE 0 END WHERE user = ?', [imageId, userId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
    /**
     * DELETE IMAGES OF USER ID
     * @param userId
     */
    static deleteImagesOfUserId(userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('DELETE FROM image WHERE user = ?', [userId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve();
            });
        });
    }
    /**
     * GET IMAGES TO UNLINK OF USER ID
     * @param userId
     */
    static getUrlImagesToDelete(userId) {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('SELECT id, url, user, isProfile, eliminationDate FROM image WHERE user = ?', [userId], (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result);
            });
        });
    }
    /*-- ************************************************************************************************************** --*/
    /*-- **************************************************** JOBS **************************************************** --*/
    /*-- ************************************************************************************************************** --*/
    /**
     * DELETE REGISTERS OF IMAGE/S WHEN THE USER UNSUBSCRIBES (JOB 'checkUsersToDelete')         ---------------------------- SIN USAR
     * @param arrayImageIds
     */
    static deleteImagesOnUnsubscribeAccount(arrayImageIds) {
        return new Promise((resolve, reject) => {
            if (arrayImageIds.length > 0) {
                bdconfig_1.conexion.query('DELETE FROM image WHERE id IN (?)', [arrayImageIds], (error, result, fields) => {
                    if (error)
                        return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                    else
                        return resolve();
                });
            }
            else
                return reject('No images of unsubscribed users to delete');
        });
    }
    /**
     * DELETE IMAGE/S - (JOB 'checkImagesToDelete')
     */
    static deleteImages() {
        return new Promise((resolve, reject) => {
            this.getImagesToDelete().then((images) => {
                let arrayImageIds = images.map(eachImage => eachImage.id);
                bdconfig_1.conexion.query('DELETE FROM image WHERE id IN (?)', [arrayImageIds], (error, result, fields) => {
                    if (error)
                        return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                    else
                        resolve(images);
                });
            }).catch((error) => reject(error));
        });
    }
    /**
     * GET IMAGE/S TO DELETE - (JOB 'checkImagesToDelete')
     */
    static getImagesToDelete() {
        return new Promise((resolve, reject) => {
            bdconfig_1.conexion.query('SELECT * FROM image WHERE eliminationDate IS NOT NULL AND eliminationDate < (NOW() - INTERVAL 1 MINUTE)', (error, result, fields) => {
                if (error)
                    return reject(GenericErrorResponse_1.GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    resolve(result);
            });
        });
    }
}
exports.ImageController = ImageController;
//# sourceMappingURL=ImageController.js.map