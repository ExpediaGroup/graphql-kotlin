var log = require('../')('http')
, http = require('http')
, name = 'My App';

// fake app

log('booting %s', name);

http.createServer(function(req, res){
  log(req.method + ' ' + req.url);
  res.end('hello\n');
}).listen(3000, function(){
  log.info('listening');
});

// fake worker of some kind

require('./worker');
