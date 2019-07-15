var BROWSER = typeof window !== 'undefined';

/**
 * Exports
 */
module.exports = loglevelDebug;
loglevelDebug.getMode = getMode;
loglevelDebug.enable = enable;
loglevelDebug.enabled = enabled;
loglevelDebug.disable = disable;

/**
 * Internal States
 */
var skips = [];
var names = [];
var _loggers = {};

/**
 * Composit a new object.
 *
 * Links all attributes of source object to
 * target object.
 *
 * @param {Object} src
 * @param {Object} target
 */
function composit(src, target) {
  var o = {};
  Object.keys(src).forEach(function(k) {
    var attr = src[k];
    target[k] = attr.bind ? attr.bind(k) : attr;
  });
}

/**
 * -----------------
 * Package level API
 * -----------------
 */

/**
 * Read environment variable.
 *
 * @param {String} key
 * @param {Any} defVal default value
 * @returns {Any}
 * @throws {Error}
 * @api private
 */
function readEnv(key, defVal) {
  var env = BROWSER ? global : process.env;
  var v = env[key];
  return v ? v : defVal;
}

/**
 * Set environment variable
 *
 * @param {String} key
 * @param {Any} val
 * @api private
 */
function setEnv(key, val) {
  var env = BROWSER ? global : process.env;
  env[key] = val;
}

/** To know which mode we are running.
 *
 * @return {String} debug or production
 * @api public
 */
function getMode() {
  return (names.length > 0 || skips.length >0) ?  'debug' : 'production';
}

/**
 * Enables a debug mode by namespaces. This can include modes
 * separated by a colon and wildcards.
 *
 * @param {String} namespaces
 * @api public
 */
function enable(namespaces) {
  if (!readEnv('DEBUG'))
    setEnv('DEBUG', namespaces);

  var split = (namespaces || '').split(/[\s,]+/);
  var len = split.length;

  for (var i = 0; i < len; i++) {
    if (!split[i]) continue; // ignore empty strings
    namespaces = split[i].replace(/\*/g, '.*?');
    if (namespaces[0] === '-') {
      skips.push(new RegExp('^' + namespaces.substr(1) + '$'));
    } else {
      names.push(new RegExp('^' + namespaces + '$'));
    }
  }

  for(var k in _loggers) {
    var logger = _loggers[k];
    if (enabled(k)) {
      logger.setLevel(logger.levels.DEBUG);
    }
    else {
      logger.setLevel(logger.levels.INFO);
    }
  }
}

/**
 * Returns true if the given mode name is enabled, false otherwise.
 *
 * @param {String} name
 * @return {Boolean}
 * @api public
 */
function enabled(name) {
  if (getMode() !== 'debug') return true;

  var i, len;
  for (i = 0, len = skips.length; i < len; i++) {
    if (skips[i].test(name)) {
      return false;
    }
  }
  for (i = 0, len = names.length; i < len; i++) {
    if (names[i].test(name)) {
      return true;
    }
  }
  return false;
}

/**
 * Disable debug mode.
 *
 * @api public
 */
function disable() {
  names = [];
  skips = [];
  for(var k in _loggers) {
    var logger = _loggers[k];
    logger.setLevel(logger.levels.INFO);
  }
}

/** Plugin itself
 *
 * Given a logger name, it returns a logger.
 * Given a loglevel logger, it replaced its original methodFactory.
 *
 * @param {String|Object} nameOrLogger logger name or a loglevel logger.
 * @return {Object} loglevel logger
 * @api public
 */
function loglevelDebug(nameOrLogger) {
  var DEBUG = readEnv('DEBUG');
  var log;

  if (typeof nameOrLogger === 'string') {
    log = _loggers[nameOrLogger] = require('loglevel').getLogger(nameOrLogger);
  }
  else if (typeof nameOrLogger === 'object') {
    log = nameOrLogger;
  }
  else {
    log = require('loglevel');
  }

  var originalFactory = log.methodFactory;
  log.methodFactory = function(methodName, logLevel, loggerName) {
    var rawMethod = originalFactory(methodName, logLevel, loggerName);
    var prefix = [
      '[' + methodName.toUpperCase() + ']',
      new Date(),
      loggerName
    ].join(' ');
    return function(message) {
      var args = Array.prototype.slice.call(arguments);
      args[0] = prefix + ' ' + args[0];
      return rawMethod.apply(this, args);
    };
  };

  if (DEBUG) {
    disable();
    enable(DEBUG);
  }
  else {
    log.setLevel(log.levels.INFO);
  }

  var callableLogger = function() {
    return log.debug.apply(this, arguments);
  };

  composit(log, callableLogger);
  return callableLogger;
}
