import mysql from 'mysql';

export const conexion = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'playmatch'
});