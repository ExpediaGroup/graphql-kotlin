'use strict'

exports.__esModule = true
exports.GraphQLUpload = void 0

var _graphql = require('graphql')

const GraphQLUpload = new _graphql.GraphQLScalarType({
  name: 'Upload',
  description: 'The `Upload` scalar type represents a file upload.',
  parseValue: value => value,

  parseLiteral() {
    throw new Error('‘Upload’ scalar literal unsupported.')
  },

  serialize() {
    throw new Error('‘Upload’ scalar serialization unsupported.')
  }
})
exports.GraphQLUpload = GraphQLUpload
