---
id: renaming-classes
title: Renaming Classes
---
The schema generator uses the simple name of the class for type names and function/property names for fields by default. You can override this behavior using the `@GraphQLName` annotation with a `target` value. The `GraphQLNameTarget` accepts three values:

- `GraphQLNameTarget.INPUT`
- `GraphQLNameTarget.OUTPUT`
- `GraphQLNameTarget.BOTH` (default)

Where `GraphQLNameTarget.BOTH` is the default value.

Here's an example of renaming a Kotlin `Widget` class to `MyCustomName` GraphQL type:

```kotlin
@GraphQLName("MyCustomName", target=GraphQLNameTarget.INPUT)
data class Widget(
    @GraphQLName("myCustomField")
    val value: Int?
)
```

```graphql
input MyCustomName {
  myCustomField: Int
}
```

### Exceptions
You might encounter the following exceptions while renaming classes:  
1. `InvalidGraphQLTypeException`: This occurs when the target is not valid for the class. For example, defining an `INPUT` target with output classes or an `OUTPUT` target with input classes.  
2. `CouldNotGetNameOfKClassException`: This is thrown when neither the `GraphQL name` of the class nor the `simple name` of the class is available.

### Implementation Details
1. The function retrieves the GraphQL name of the class if it exists, otherwise, it uses the simple name of the class. If neither is available, it throws a `CouldNotGetNameOfKClassException`.  

2. Depending on the `GraphQLNameTarget` of the class, the function behaves differently:  
* `INPUT`: Returns the name if the class is an input class, otherwise throws an `InvalidGraphQLTypeException`.
* `OUTPUT`: Returns the name if the class is not an input class, otherwise throws an `InvalidGraphQLTypeException`.
* `BOTH` or not specified: Returns the name as is for output classes. For input classes, it appends the `Input` suffix to the name if it doesn't already have it.

## Note
Previously, clients couldn't override the names of `Input` classes and the `Input` suffix was forcibly appended to the class name. Now, with the `@GraphQLName` annotation, clients can override the names of Input classes and append the `Input` suffix to the class name if it doesn't already have it.
