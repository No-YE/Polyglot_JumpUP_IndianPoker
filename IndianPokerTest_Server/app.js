var app = require('express')();
const http = require('http');
const socketIo = require('socket.io');
const socketEvents = require('./routes/note');
var path = require('path');
var express = require('express');

//const server = http.createServer(app).listen(8080, function () {
//    console.log('Server Started at 8080')
//});

var httpServer =http.createServer(app).listen(process.env.PORT || 3000, function(){
    console.log('Socket IO server has been started');
});
  
var io = require('socket.io').listen(httpServer);

//var io = new socketIo(server);
socketEvents(io);