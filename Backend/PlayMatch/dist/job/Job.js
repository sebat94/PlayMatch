"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const crontab = require('node-cron');
const UserController_1 = require("../controllers/UserController");
const ImageController_1 = require("../controllers/ImageController");
const ImageUtils_1 = require("../utils/ImageUtils");
class Job {
}
/**
 * Check images to delete each 1-min
 */
Job.checkImagesToDelete = crontab.schedule('* * * * *', () => {
    ImageController_1.ImageController.deleteImages().then((res) => {
        let arrayImageUrls = res.map(eachImage => eachImage.url);
        ImageUtils_1.ImageUtils.unlinkImages(arrayImageUrls);
        console.log('Cleaned images from database');
    }).catch((error) => console.log(error));
});
/**
 * Each end month check for users that are disabled, and 'anonymize' users that are disabled >= 50 MONTHS (5 years)     ---------------------------- SIN USAR
 */
Job.checkUsersToDelete = crontab.schedule('* * * Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec *', () => {
    UserController_1.UserController.deleteUnsubscribedUsers().then((arrayImages) => {
        // Delete image registers from database
        let arrayImageIds = arrayImages.map((eachImage) => eachImage.id);
        ImageController_1.ImageController.deleteImagesOnUnsubscribeAccount(arrayImageIds).then(() => {
            // When images are deleted in database, then delete images from disk
            let arrayImageUrls = arrayImages.map(eachImage => eachImage.url);
            ImageUtils_1.ImageUtils.unlinkImages(arrayImageUrls);
        }).catch((error) => console.log(error));
    }).catch((error) => console.log(error));
});
exports.Job = Job;
//# sourceMappingURL=Job.js.map