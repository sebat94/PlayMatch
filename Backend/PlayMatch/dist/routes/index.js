"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express = require('express');
let router = express.Router();
/**
* setHeader() works on two conditions:
* 1. You haven't already used writeHead() (i.e. you can't set the header if its already been sent)
* 2. You haven't sent a piece of the body ie. res.write() or res.end
*/
module.exports = router;
//# sourceMappingURL=index.js.map