const express = require('express');

import { User } from '../models/User';
import { Gender } from '../models/Gender';
import { IUser, IUserResponse, IUsersResponse } from '../interfaces/IUser';
import { UserController } from '../controllers/UserController';
import { HeadersResponse } from '../utils/HeadersResponse';


let router = express.Router();

/**
* setHeader() works on two conditions:
* 1. You haven't already used writeHead() (i.e. you can't set the header if its already been sent)
* 2. You haven't sent a piece of the body ie. res.write() or res.end
*/



module.exports = router;