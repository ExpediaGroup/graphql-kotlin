package test.data.queries.simple

/*
type Query {
  hello: String!
}
 */
class SimpleQuery {
    fun hello(name: String): String = "Hello $name"
}
