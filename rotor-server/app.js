

var http = require('http');
var caller = require('request');
var router = require('choreographer').router();
var socket = require('socket.io');
var url = require('url');
var qs = require('querystring');
var buffet = require('buffet')({
  root:'./',
  poweredBy:false
});
var rack = require('hat').rack();

var Parse = require('parse').Parse;

var Clients = [];
var Responses = [];

router.get('/', function(req, res) {
  res.writeHead(301, {Location: '/public/index.html'});
  res.end();
});

router.get('/public/**', function(req, res) {
  buffet(req, res, function() {
    buffet.notFound(req, res);
  });
});

router.get('/client/*/**', function(req, res, client, urldata) {
  if (Clients[client]) {
    var url_parts = url.parse(req.url);
    var query_data = qs.parse(url_parts.query);
    var response_id = rack();
    Responses[response_id] = res;
    Clients[client].emit('get', {
      path:urldata,
      queryData:query_data,
      responseId:response_id
    });
  }
  res.end('Not Ok.');
});

var app = http.createServer(router);
var io = socket.listen(app);
app.listen(80);

var connectedClients = 0;

io.sockets.on('connection', function(socket) {
  Clients[socket.id] = socket;
  connectedClients++;
  socket.emit('id', socket.id);
  socket.on('response', function(data) {
    if (Responses[data.responseId]) {
      Responses[data.responseId].end(data.body);
    }
  })
});

