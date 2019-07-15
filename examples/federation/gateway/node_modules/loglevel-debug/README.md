# Loglevel Debug Plugin

Plugin for JS logger [loglevel](https://github.com/pimterry/loglevel) which allows enable/disable debug output dynamically and draws inspiration from TJ Hollowaychuk's debug.

## Features

- Ability to change logging levels of specific modules.
- Ability to turn on/off debug output for specific modules in development.
- Production logging would be all modules with warn, info, error levels,

## Installation 

```
npm install loglevel-debug
```

```
bower install loglevel-debug
```

## Usage

This plugin is deigned to be used standalone.

```js
var log = require('loglevel-debug')('http')
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
```

Example worker.js:

```js
var log = require('loglevel-debug')('worker');

setInterval(function(){
  log('doing some work');
}, 1000);
```

Use *DEBUG* environment variable to control debug output.

```bash
$ DEBUG=http,worker:* node example/app
[DEBUG] http booting %s
[DEBUG] worker:a doing lots of uninteresting work
[DEBUG] worker:b doing some work
[INFO] http listening
[DEBUG] worker:a doing lots of uninteresting work
[DEBUG] worker:a doing lots of uninteresting work
[DEBUG] worker:b doing some work
[DEBUG] worker:a doing lots of uninteresting work
[DEBUG] worker:a doing lots of uninteresting work
```

## Logging Methods

[loglevel][1] methods are all supported.

```js
# creates a logger for Module1.
var log = require('loglevel-debug')('Module1');

log('this is a debug message');
log.debug('this is a debug message');
log.info('this is a info message');
log.warn('this is a warring message');
log.error('this is a errror message');
log.trace('this is a trace message');
```

you can dynamic change specific logger's logging level.

```
log.setLevel(log.levels.INFO)
```

## Browser support

This plugin works on browser as well. To enable debug output, you can use its `enable` public api,

```
loglevelDebug.enable('worker:*');
```

## As a loglevel plugin

```js
var loglevel = require('loglevel');
var loglevelDebug = require('loglevel');

loglevelDebug(loglevel);

log.debug('Test');
```

[1]: https://github.com/pimterry/loglevel