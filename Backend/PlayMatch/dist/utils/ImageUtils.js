"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const fs = require('fs');
const Image_1 = require("../models/Image");
const ImageController_1 = require("../controllers/ImageController");
class ImageUtils {
    /**
     * Write image in route folder from Base64
     * @param datosBase64
     * @param nombreImagen
     * @param ruta
     */
    static createImageFromBase64(imageBase64, imageName, ruta) {
        let data = imageBase64.replace(/^data:image\/\w+;base64,/, "");
        const buffer = Buffer.from(data, 'base64');
        fs.writeFileSync(ruta + imageName, buffer);
    }
    /**
     * Save register in DDBB
     * @param url
     * @param idUser
     */
    static insertRowAfterSaveImage(url, idUser) {
        return new Promise((resolve, reject) => {
            let data = new Image_1.Image(url, idUser, false);
            return ImageController_1.ImageController.insertImage(data).then(() => resolve()).catch((error) => {
                // If there was an error when inserting the image in the database, then delete previous writed image
                this.unlinkImages(url);
                return reject(error);
            });
        });
    }
    /**
     * Delete image from public folder
     * @param data
     */
    static unlinkImages(data) {
        let unlink = (url) => {
            fs.unlink(url, (error) => {
                if (error)
                    console.log("Unlink error");
                else
                    console.log(`${url} was deleted`);
            });
        };
        if (data instanceof Array)
            data.forEach(eachUrl => unlink(eachUrl));
        else
            unlink(data);
    }
}
exports.ImageUtils = ImageUtils;
//# sourceMappingURL=ImageUtils.js.map