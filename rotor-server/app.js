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

var getForClient = function(req, res, client, urldata) {
  logEvent('in', client, 'GET');
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
    logEvent('out', client, 'event');
  } else {
    res.end('Not Ok.');
  }
};

router.get('/client/*', getForClient);
router.get('/client/*/**', getForClient);

var postForClient = function(req, res, client, urldata) {
    logEvent('in', client, 'POST');
    var body = '';
    req.on('data', function(chunk) {
      body += chunk;
    });
    req.on('end', function() {
        if (Clients[client]) {
            var url_parts = url.parse(req.url);
            var query_data = qs.parse(url_parts.query);
            var response_id = rack();
            Responses[response_id] = res;
            Clients[client].emit('post', {
                path:urldata,
                queryData:query_data,
                body:body,
                responseId:response_id
            });
            logEvent('out', client, 'event');
        } else {
            res.end('Not Ok.');
        }
    });
};

router.post('/client/*', postForClient);
router.post('/client/*/**', postForClient);

var app = http.createServer(router);
var io = socket.listen(app);
app.listen(80);

var connectedClients = 0;

io.sockets.on('connection', function(socket) {
  Clients[socket.id] = socket;
  connectedClients++;
  console.log('New client with id: ' + socket.id);
  logEvent('out', socket.id, 'id');
  socket.emit('id', socket.id);
  socket.on('response', function(data) {
    logEvent('in', socket.id, 'event');
    if (Responses[data.responseId]) {
      Responses[data.responseId].end(data.body);
    }
  });
  socket.on('alias', function(data) {
    logEvent('in', socket.id, 'event');
    Clients[data] = socket;
  });
});

function logEvent(direction, client, type) {
  if (Clients['dashboard']) {
    Clients['dashboard'].emit('log', {
      direction:direction,
      client:client,
      type:type
    });
  }
}