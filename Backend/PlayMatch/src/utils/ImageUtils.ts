const fs = require('fs');

import { Image } from '../models/Image';
import { IImage } from '../interfaces/IImage';
import { ImageController } from '../controllers/ImageController';

export class ImageUtils {

    /**
     * Write image in route folder from Base64
     * @param datosBase64 
     * @param nombreImagen 
     * @param ruta 
     */
    static createImageFromBase64(imageBase64: string, imageName: string, ruta: string) {
        let data = imageBase64.replace(/^data:image\/\w+;base64,/, "");
        const buffer = Buffer.from(data, 'base64');
        fs.writeFileSync(ruta + imageName, buffer);
    }

    /**
     * Save register in DDBB
     * @param url 
     * @param idUser 
     */
    static insertRowAfterSaveImage(url: string, idUser: number): Promise<void> {
        return new Promise((resolve, reject) => {
            let data: IImage = new Image(url, idUser, false);
            return ImageController.insertImage(data).then(() => resolve()).catch((error: string) => {
                // If there was an error when inserting the image in the database, then delete previous writed image
                this.unlinkImages(url);
                return reject(error)
            });
        });
    }

    /**
     * Delete image from public folder
     * @param data 
     */
    static unlinkImages(data: string[] | string) {
        let unlink = (url: string[] | string) => {
            fs.unlink(url, (error) => {
                if (error) console.log("Unlink error");
                else console.log(`${url} was deleted`);
            });
        }
        if(data instanceof Array) data.forEach(eachUrl => unlink(eachUrl));
        else unlink(data);
    }

}