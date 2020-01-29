const crontab = require('node-cron');
import { UserController } from '../controllers/UserController';
import { ImageController } from "../controllers/ImageController";
import { IImage } from "../interfaces/IImage";
import { ImageUtils } from "../utils/ImageUtils";

export class Job{

    /**
     * Check images to delete each 1-min
     */
    static checkImagesToDelete = crontab.schedule('* * * * *', () => {
        ImageController.deleteImages().then((res: IImage[]) => {
            let arrayImageUrls: string[] = res.map(eachImage => eachImage.url);
            ImageUtils.unlinkImages(arrayImageUrls);
            console.log('Cleaned images from database');
        }).catch((error: string) => console.log(error));
    });

    /**
     * Each end month check for users that are disabled, and 'anonymize' users that are disabled >= 50 MONTHS (5 years)     ---------------------------- SIN USAR                 
     */
    static checkUsersToDelete = crontab.schedule('* * * Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec *', () => {
        UserController.deleteUnsubscribedUsers().then((arrayImages: IImage[]) => {
            // Delete image registers from database
            let arrayImageIds: number[] = arrayImages.map((eachImage: IImage) => eachImage.id);
            ImageController.deleteImagesOnUnsubscribeAccount(arrayImageIds).then(() => {
                // When images are deleted in database, then delete images from disk
                let arrayImageUrls: string[] = arrayImages.map(eachImage => eachImage.url);
                ImageUtils.unlinkImages(arrayImageUrls);
            }).catch((error: string) => console.log(error));
        }).catch((error: string) => console.log(error));
    });


}


