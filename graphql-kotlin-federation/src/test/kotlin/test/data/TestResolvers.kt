package test.data

import com.expedia.graphql.federation.execution.FederatedTypeResolver
import test.data.queries.federated.Book
import test.data.queries.federated.User

internal class BookResolver : FederatedTypeResolver<Book> {
    override suspend fun resolve(representations: List<Map<String, Any>>): List<Book?> {
        val results = mutableListOf<Book?>()
        for (keys in representations) {
            val book = Book(keys["id"].toString())
            keys["weight"]?.toString()?.toDoubleOrNull()?.let {
                book.weight = it
            }
            results.add(book)
        }

        return results
    }
}

internal class UserResolver : FederatedTypeResolver<User> {
    override suspend fun resolve(representations: List<Map<String, Any>>): List<User?> {
        val results = mutableListOf<User?>()
        for (keys in representations) {
            val id = keys["userId"].toString().toInt()
            val name = keys["name"].toString()
            results.add(User(id, name))
        }
        return results
    }
}
