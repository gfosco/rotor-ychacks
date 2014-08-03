var http = require('http');
var caller = require('request');
var router = require('choreographer').router();
var socket = require('socket.io');
var url = require('url');
var qs = require('querystring');
var nodeStatic = require('node-static');
var dashboardServer = new nodeStatic.Server('../dashboard');
var webdemoServer = new nodeStatic.Server('../');
var rack = require('hat').rack();
var Parse = require('parse').Parse;
var Clients = [];
var Responses = [];

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

router.get('/generateLogData', function(req, res) {
  logEvent('in', 'test', 'id');
  logEvent('out', 'test', 'event');
  logEvent('in', 'test', 'event');
  logEvent('in', 'test', 'POST');
  logEvent('in', 'test', 'GET');
  res.end('ok');
});

router.get('/WebDemo1/**', function(req, res) {
  webdemoServer.serve(req, res);
});

router.get('/**', function(req, res) {
  dashboardServer.serve(req, res);
});

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
      if (Responses[data.contentType]) {
        Responses[data.responseId].writeHead(200, {'Content-Type':data.contentType});
      } else {
        Responses[data.responseId].writeHead(200, {'Content-Type':'text/html'});
      }
      Responses[data.responseId].end(data.body);
    }
  });
  socket.on('alias', function(data) {
    logEvent('in', socket.id, 'event');
    Clients[data] = socket;
  });
  socket.on('disconnect', function() {
    connectedClients--;
    delete Clients[socket.id];
    logEvent('disconnect', socket.id, 'disconnect');
  });
});

function logEvent(direction, client, type) {
  var d = new Date();
  console.log('Event ' + direction + ' for ' + client + ' with type ' + type);
  if (Clients['dashboard']) {
    Clients['dashboard'].emit('log', {
      direction:direction,
      client:client,
      type:type,
      time:d.getTime()
    });
  }
}