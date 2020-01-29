/**
 * This code will automatically load the .env file in the root of your project and initialize the values.
 * It will skip any variables that already have been set. You should not use .env files in your production
 * environment though and rather set the values directly on the respective host
 */
//require('dotenv').config({path: __dirname + '/.env'});

/**
 * Require
 * --------
 * CommonJS te trae una copia del módulo módulo para que lo puedas usar.
 * Carga los módulos de forma global para el proyecto.
 * Carga asíncronamente.
 *
 * Import
 * --------
 * CommonJS te trae una referencia al módulo.
 * NO carga los módulos de forma global.
 * Carga síncronamente, uno detrás de otro.
 */
// VARIABLES PACKAGES
const express = require('express');
const bodyParser= require('body-parser');
const helmet = require('helmet');
const cors = require('cors');
const https = require("https");
const http = require("http");
const fetch = require("node-fetch");
//const socketio = require('socket.io');
// VARIABLES ROUTER
const indexRoutes = require('./routes/index');
const userRoutes = require('./routes/user');
const authRoutes = require('./routes/auth');
const imageRoutes = require('./routes/image');
const matchRoutes = require('./routes/match');
const chatRoutes = require('./routes/chat');
const messageRoutes = require('./routes/message');
const favouriteRoutes = require('./routes/favourite');
const playmatch = require('./routes/playmatch');
// JOB
//import { Job } from "./job/Job";
// CONSTANTS
import { SERVER } from './utils/constants';
import { IMessage, IMessages } from './interfaces/IMessage';
import { IChat } from './interfaces/IChat';
import { Message } from './models/Message';


/**
 * Init Express And SocketIO
 */
let app = express();
let server = require('http').Server(app);
let io = require('socket.io')(server);

/**
 * Middlewares
 */
app.use(cors());
app.use('/', express.static(__dirname)); // TODO: Enrutar bien
app.use(bodyParser.json({limit: '2mb'}));
app.use(bodyParser.urlencoded({limit: '2mb', extended: true})); // parse application/x-www-form-urlencoded
app.use(helmet());

/**
 * Routes
 */
app.use('/', indexRoutes);
app.use('/', authRoutes);
app.use('/', userRoutes);
app.use('/', imageRoutes);
app.use('/', matchRoutes);
app.use('/', chatRoutes);
app.use('/', messageRoutes);
app.use('/', favouriteRoutes);
app.use('/', playmatch);

/**
 * JOBS
 */
//Job.checkImagesToDelete.start();
//Job.checkUsersToDelete.start();      // Each 5 years animymize data of user

/**
 * Start
 */
server.listen(3000);

/**
 * Socket IO
 */
io.on('connection', (socket) => {

	let actualRoom: string;

	// When Client Emit to 'room', get in second Param Name of the room and subscribe the socket to a given channel
    socket.on('room', (newRoom: string) => {
		actualRoom = newRoom;

		// Create New Room only if it isn't created yet
		findRooms().forEach((room: string) => {
			if(room !== newRoom) socket.join(newRoom);
		});
		

		console.log("New Room: " + newRoom);
		console.log(findRooms());
		console.log("Usuario: " + socket.id);

    });

	/*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
	/*-------------------------------------------------------------------------- ANDROID APP --------------------------------------------------------------------*/
	/*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * Listen for "join" event, and Emit event "userjoinedthechat" to client
	 */
	// socket.on('join', (userNickname) => {

    //     console.log(userNickname +" : has joined the chat "  );

    //     socket.broadcast.emit('userjoinedthechat',userNickname +" : has joined the chat ");
	// })
	
	/**
	 * Store Message of Provided Chat in DDBB and Emit His Value to Show in Chat
	 */
	socket.on('messageDetection', (chat: string, message: string, token: string) => {
		console.log(findRooms());
		console.log("LLEGA: " + actualRoom);
		// Parse String To Json
		let chatParsed: any = JSON.parse(chat);
		let newMessage = new Message(chatParsed.id, chatParsed.matchUser1, chatParsed.matchUser2, message);
		// Store Message in DDBB and Emit To this Room(Channel)
		addMessage(newMessage, token, actualRoom);
	});

	socket.on('disconnect', () => {
		console.log(findRooms());

		//socket.broadcast.emit('userDisconnect', 'user has left the chat');



		//socket.leave(room);
		io.emit('userDisconnect', 'user has left the chat');
		//socket.join(room).emit('userdisconnect', 'user has left the chat');				// Send to All of the Channel
		//socket.broadcast.to(room).emit('userdisconnect', 'user has left the chat');		// Send to All of the Channel, excepto to me
	});


	/*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
	/*-------------------------------------------------------------------------- UNITY GAME ---------------------------------------------------------------------*/
	/*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
	// Está en la otra BBDD por ahora --> playmatch_unity.	TODO: Juntar en la misma BBDD
	/**
	 * Player Connected To The Game
	 */
	/*socket.on('player connected', () => {	// TODO: data --> JSONObject --> Debería recibir al menos el id del usuario para validaciones, obtenido a través de la APP android.
		// Get questions to connected player
		http.get(`${SERVER}/playmatch/questions`, (res) => {
			
			res.setEncoding('utf8');
			let rawData = '';
			res.on('data', (chunk) => { rawData += chunk; });
			res.on('end', () => {
				try {
					const parsedData = JSON.parse(rawData);

					// Emit question
					socket.emit('send questions', parsedData);

					// Disconnect
					socket.on('disconnect', () => {
						console.log('Jugador desconectado');
					});

				} catch (e) {
					console.error(e.message);
				}
			});
			
		}).on('error', (error) => {
			console.log('Error: ' + error.message);
		});
	});*/

	/**
	 * Player Send Answers
	 */
	/*socket.on('send answers', (data) => {
		// Post Answes
		let options = {
			hostname: '192.168.43.4',
			port: 3000,
			path: '/playmatch/answers',
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			}
		};
		let req = http.request(options, (res) => {
			//console.log('Status: ' + res.statusCode);
			//console.log('Headers: ' + JSON.stringify(res.headers));
			// From Here We Get The Response From DDBB
			let rawData = '';
			res.setEncoding('utf8');
			res.on('data', (chunk) => { rawData += chunk; });
			res.on('end', () => {
				try {
					const parsedData = JSON.parse(rawData);
console.log("Parsed data: " + JSON.stringify(parsedData));
					// Emit question
					socket.emit('finished game', parsedData);

					// Disconnect
					// socket.on('disconnect', () => {
					// 	console.log('Jugador desconectado');
					// });

				} catch (e) {
					console.error(e.message);
				}
			});
		});
		req.on('error', (e) => {
			console.log('Problem with request: ' + e.message);
		});
		// Write Data To Request Body
		req.write(JSON.stringify(data));
		req.end();
	});*/

});

// All the rooms in the server are located in the variable io.sockets.adapter.rooms
let findRooms = () => {
	var availableRooms = [];
	var rooms = io.sockets.adapter.rooms;
	if (rooms) 
		for (var room in rooms) 
			if (!rooms[room].hasOwnProperty(room))
				availableRooms.push(room);

	return availableRooms;
}


// /**
//  * API CALL - Get All Messages Of a Chat
//  * @param chatId 
//  * @param token 
//  */
// let getPreviousMessages = (chatId: number, token: string): Promise<IMessages> => {
// 	let url = `${SERVER}/message/${chatId}`;
// 	return fetch(
// 	  url,
// 	  {
// 		method: 'GET',
// 		headers: {
// 		  'Authorization': token,
// 		  'Content-Type': 'application/json'
// 		}
// 	  }
// 	).then((response) => response.json()).then((responseJson: IMessages) => {
		
// 	})
// 	.catch(error => { throw new Error(error) });
// };

/**
 *  API CALL - Add Message
 * @param message 
 * @param token 
 */
let addMessage = (message: IMessage, token: string, room: string) => {
	let url = `${SERVER}/message/add`;
	fetch(
		url,
		{
			method: 'POST',
			headers: {
				'Authorization': token,
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(message)
		}
	).then((response) => response.json()).then((response: IMessage) => {
		// send the message to all users including the sender using io.emit()
		console.log("Emit in room: " + room + "; value: " + JSON.stringify(response));
		io.to(room).emit('message', response);
	})
	.catch(error =>  console.log("Error: " + error));
};