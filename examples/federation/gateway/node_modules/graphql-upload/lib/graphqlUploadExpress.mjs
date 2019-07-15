import { processRequest } from './processRequest'
export const graphqlUploadExpress = options => (request, response, next) => {
  if (!request.is('multipart/form-data')) return next()
  const finished = new Promise(resolve => request.on('end', resolve))
  const { send } = response

  response.send = (...args) => {
    finished.then(() => {
      response.send = send
      response.send(...args)
    })
  }

  processRequest(request, response, options)
    .then(body => {
      request.body = body
      next()
    })
    .catch(error => {
      if (error.status && error.expose) response.status(error.status)
      next(error)
    })
}
