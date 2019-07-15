'use strict'

exports.__esModule = true
exports.graphqlUploadExpress = void 0

var _processRequest = require('./processRequest')

const graphqlUploadExpress = options => (request, response, next) => {
  if (!request.is('multipart/form-data')) return next()
  const finished = new Promise(resolve => request.on('end', resolve))
  const { send } = response

  response.send = (...args) => {
    finished.then(() => {
      response.send = send
      response.send(...args)
    })
  }

  ;(0, _processRequest.processRequest)(request, response, options)
    .then(body => {
      request.body = body
      next()
    })
    .catch(error => {
      if (error.status && error.expose) response.status(error.status)
      next(error)
    })
}

exports.graphqlUploadExpress = graphqlUploadExpress
