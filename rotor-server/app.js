

var http = require('http');
var caller = require('request');
var router = require('choreographer').router();
var socket = require('socket.io');
var url = require('url');
var buffet = require('buffet')({
  root:'./',
  poweredBy:false
});

var Parse = require('parse').Parse;

var Clients = [];
var Identity = [];


router.get('/', function(req, res) {
  res.end('Hi');
});



var app = http.createServer(router);
var io = socket.listen(app);
app.listen(80);

var connectedClients = 0;

io.sockets.on('connection', function(socket) {
  Clients[socket.id] = socket;
  connectedClients++;

});
