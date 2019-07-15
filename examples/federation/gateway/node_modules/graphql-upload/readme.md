![graphql-upload logo](https://cdn.jsdelivr.net/gh/jaydenseric/graphql-upload@8.0.0/graphql-upload-logo.svg)

# graphql-upload

[![npm version](https://badgen.net/npm/v/graphql-upload)](https://npm.im/graphql-upload) [![Build status](https://travis-ci.org/jaydenseric/graphql-upload.svg?branch=master)](https://travis-ci.org/jaydenseric/graphql-upload)

Middleware and an [`Upload` scalar](#class-graphqlupload) to add support for [GraphQL multipart requests](https://github.com/jaydenseric/graphql-multipart-request-spec) (file uploads via queries and mutations) to various Node.js GraphQL servers.

⚠️ Previously published as [`apollo-upload-server`](https://npm.im/apollo-upload-server).

## Support

The following environments are known to be compatible:

- Node.js v8.5+
  - CJS
  - Native ESM with [`--experimental-modules`](https://nodejs.org/api/esm.html#esm_enabling)
- [Koa](https://koajs.com)
  - [`graphql-api-koa`](https://npm.im/graphql-api-koa)
  - [`apollo-server-koa`](https://npm.im/apollo-server-koa) (inbuilt)
- [Express](https://expressjs.com)
  - [`express-graphql`](https://npm.im/express-graphql)
  - [`apollo-server-express`](https://npm.im/apollo-server-express) (inbuilt)

See also [GraphQL multipart request spec server implementations](https://github.com/jaydenseric/graphql-multipart-request-spec#server).

## Setup

Setup is necessary if your environment doesn’t feature this package inbuilt (see **_[Support](#support)_**).

To install [`graphql-upload`](https://npm.im/graphql-upload) and the [`graphql`](https://npm.im/graphql) peer dependency from [npm](https://npmjs.com) run:

```shell
npm install graphql-upload graphql
```

Use the [`graphqlUploadKoa`](#function-graphqluploadkoa) or [`graphqlUploadExpress`](#function-graphqluploadexpress) middleware just before GraphQL middleware. Alternatively, use [`processRequest`](#function-processrequest) to create custom middleware.

A schema built with separate SDL and resolvers (e.g. using [`makeExecutableSchema`](https://apollographql.com/docs/graphql-tools/generate-schema#makeExecutableSchema)) requires the [`Upload` scalar](#class-graphqlupload) to be setup.

## Usage

[Clients implementing the GraphQL multipart request spec](https://github.com/jaydenseric/graphql-multipart-request-spec#client) upload files as [`Upload` scalar](#class-graphqlupload) query or mutation variables. Their resolver values are promises that resolve [file upload details](#type-fileupload) for processing and storage. Files are typically streamed into cloud storage but may also be stored in the filesystem.

See the [example API and client](https://github.com/jaydenseric/apollo-upload-examples).

### Tips

- The process must have both read and write access to the directory identified by [`os.tmpdir()`](https://nodejs.org/api/os.html#os_os_tmpdir).
- The device requires sufficient disk space to buffer the expected number of concurrent upload requests.
- Promisify and await file upload streams in resolvers or the server will send a response to the client before uploads are complete, causing a disconnect.
- Handle file upload promise rejection and stream errors; uploads sometimes fail due to network connectivity issues or impatient users disconnecting.
- Process multiple uploads asynchronously with [`Promise.all`](https://developer.mozilla.org/docs/web/javascript/reference/global_objects/promise/all) or a more flexible solution where an error in one does not reject them all.
- Only use [`createReadStream()`](#type-fileupload) _before_ the resolver returns; late calls (e.g. in an unawaited async function or callback) throw an error. Existing streams can still be used after a response is sent, although there are few valid reasons for not awaiting their completion.
- Use [`stream.destroy()`](https://nodejs.org/api/stream.html#stream_readable_destroy_error) when an incomplete stream is no longer needed, or temporary files may not get cleaned up.

## Architecture

The [GraphQL multipart request spec](https://github.com/jaydenseric/graphql-multipart-request-spec) allows a file to be used for multiple query or mutation variables (file deduplication), and for variables to be used in multiple places. GraphQL resolvers need to be able to manage independent file streams. As resolvers are executed asynchronously, it’s possible they will try to process files in a different order than received in the multipart request.

[`busboy`](https://npm.im/busboy) parses multipart request streams. Once the `operations` and `map` fields have been parsed, [`Upload` scalar](#class-graphqlupload) values in the GraphQL operations are populated with promises, and the operations are passed down the middleware chain to GraphQL resolvers.

[`fs-capacitor`](https://npm.im/fs-capacitor) is used to buffer file uploads to the filesystem and coordinate simultaneous reading and writing. As soon as a file upload’s contents begins streaming, its data begins buffering to the filesystem and its associated promise resolves. GraphQL resolvers can then create new streams from the buffer by calling [`createReadStream()`](#type-fileupload). The buffer is destroyed once all streams have ended or closed and the server has responded to the request. Any remaining buffer files will be cleaned when the process exits.

## API

### Table of contents

- [class GraphQLUpload](#class-graphqlupload)
  - [Examples](#examples)
- [function graphqlUploadExpress](#function-graphqluploadexpress)
  - [Examples](#examples-1)
- [function graphqlUploadKoa](#function-graphqluploadkoa)
  - [Examples](#examples-2)
- [function processRequest](#function-processrequest)
  - [Examples](#examples-3)
- [type FileUpload](#type-fileupload)
- [type GraphQLOperation](#type-graphqloperation)
  - [See](#see)
- [type UploadOptions](#type-uploadoptions)

### class GraphQLUpload

A GraphQL `Upload` scalar that can be used in a [`GraphQLSchema`](https://graphql.org/graphql-js/type/#graphqlschema). It’s value in resolvers is a promise that resolves [file upload details](#type-fileupload) for processing and storage.

#### Examples

_Setup for a schema built with [`makeExecutableSchema`](https://apollographql.com/docs/graphql-tools/generate-schema#makeExecutableSchema)._

> ```js
> import { makeExecutableSchema } from 'graphql-tools'
> import { GraphQLUpload } from 'graphql-upload'
>
> const typeDefs = `
>   scalar Upload
> `
>
> const resolvers = {
>   Upload: GraphQLUpload
> }
>
> export const schema = makeExecutableSchema({ typeDefs, resolvers })
> ```

_A manually constructed schema with an image upload mutation._

> ```js
> import { GraphQLSchema, GraphQLObjectType, GraphQLBoolean } from 'graphql'
> import { GraphQLUpload } from 'graphql-upload'
>
> export const schema = new GraphQLSchema({
>   mutation: new GraphQLObjectType({
>     name: 'Mutation',
>     fields: {
>       uploadImage: {
>         description: 'Uploads an image.',
>         type: GraphQLBoolean,
>         args: {
>           image: {
>             description: 'Image file.',
>             type: GraphQLUpload
>           }
>         },
>         async resolve(parent, { image }) {
>           const { filename, mimetype, createReadStream } = await image
>           const stream = createReadStream()
>           // Promisify the stream and store the file, then…
>           return true
>         }
>       }
>     }
>   })
> })
> ```

---

### function graphqlUploadExpress

Creates [Express](https://expressjs.com) middleware that processes GraphQL multipart requests using [`processRequest`](#function-processrequest), ignoring non-multipart requests. It sets the request body to be [similar to a conventional GraphQL POST request](#type-graphqloperation) for following GraphQL middleware to consume.

| Parameter | Type                                 | Description             |
| :-------- | :----------------------------------- | :---------------------- |
| `options` | [UploadOptions](#type-uploadoptions) | GraphQL upload options. |

**Returns:** function — Express middleware.

#### Examples

_Basic [`express-graphql`](https://npm.im/express-graphql) setup._

> ```js
> import express from 'express'
> import graphqlHTTP from 'express-graphql'
> import { graphqlUploadExpress } from 'graphql-upload'
> import schema from './schema'
>
> express()
>   .use(
>     '/graphql',
>     graphqlUploadExpress({ maxFileSize: 10000000, maxFiles: 10 }),
>     graphqlHTTP({ schema })
>   )
>   .listen(3000)
> ```

---

### function graphqlUploadKoa

Creates [Koa](https://koajs.com) middleware that processes GraphQL multipart requests using [`processRequest`](#function-processrequest), ignoring non-multipart requests. It sets the request body to be [similar to a conventional GraphQL POST request](#type-graphqloperation) for following GraphQL middleware to consume.

| Parameter | Type                                 | Description             |
| :-------- | :----------------------------------- | :---------------------- |
| `options` | [UploadOptions](#type-uploadoptions) | GraphQL upload options. |

**Returns:** function — Koa middleware.

#### Examples

_Basic [`graphql-api-koa`](https://npm.im/graphql-api-koa) setup._

> ```js
> import Koa from 'koa'
> import bodyParser from 'koa-bodyparser'
> import { errorHandler, execute } from 'graphql-api-koa'
> import { graphqlUploadKoa } from 'graphql-upload'
> import schema from './schema'
>
> new Koa()
>   .use(errorHandler())
>   .use(bodyParser())
>   .use(graphqlUploadKoa({ maxFileSize: 10000000, maxFiles: 10 }))
>   .use(execute({ schema }))
>   .listen(3000)
> ```

---

### function processRequest

Processes a [GraphQL multipart request](https://github.com/jaydenseric/graphql-multipart-request-spec). Used in [`graphqlUploadKoa`](#function-graphqluploadkoa) and [`graphqlUploadExpress`](#function-graphqluploadexpress) and can be used to create custom middleware.

| Parameter | Type | Description |
| :-- | :-- | :-- |
| `request` | IncomingMessage | [Node.js HTTP server request instance](https://nodejs.org/api/http.html#http_class_http_incomingmessage). |
| `response` | ServerResponse | [Node.js HTTP server response instance](https://nodejs.org/api/http.html#http_class_http_serverresponse). |
| `options` | [UploadOptions](#type-uploadoptions)? | GraphQL upload options. |

**Returns:** Promise&lt;[GraphQLOperation](#type-graphqloperation) | Array&lt;[GraphQLOperation](#type-graphqloperation)>> — GraphQL operation or batch of operations for a GraphQL server to consume (usually as the request body).

#### Examples

_How to import._

> ```js
> import { processRequest } from 'graphql-upload'
> ```

---

### type FileUpload

File upload details, resolved from an [`Upload` scalar](#class-graphqlupload) promise.

**Type:** Object

| Property | Type | Description |
| :-- | :-- | :-- |
| `filename` | string | File name. |
| `mimetype` | string | File MIME type. Provided by the client and can’t be trusted. |
| `encoding` | string | File stream transfer encoding. |
| `createReadStream` | function | Returns a Node.js readable stream of the file contents, for processing and storing the file. Multiple calls create independent streams. Throws if called after all resolvers have resolved, or after an error has interrupted the request. |

---

### type GraphQLOperation

A GraphQL operation object in a shape that can be consumed and executed by most GraphQL servers.

**Type:** Object

| Property | Type | Description |
| :-- | :-- | :-- |
| `query` | string | GraphQL document containing queries and fragments. |
| `operationName` | string \| null? | GraphQL document operation name to execute. |
| `variables` | object \| null? | GraphQL document operation variables and values map. |

#### See

- [GraphQL over HTTP spec](https://github.com/APIs-guru/graphql-over-http#request-parameters).
- [Apollo Server POST requests](https://www.apollographql.com/docs/apollo-server/requests#postRequests).

---

### type UploadOptions

GraphQL upload server options, mostly relating to security, performance and limits.

**Type:** Object

| Property | Type | Description |
| :-- | :-- | :-- |
| `maxFieldSize` | number? = `1000000` | Maximum allowed non-file multipart form field size in bytes; enough for your queries. |
| `maxFileSize` | number? = `Infinity` | Maximum allowed file size in bytes. |
| `maxFiles` | number? = `Infinity` | Maximum allowed number of files. |
