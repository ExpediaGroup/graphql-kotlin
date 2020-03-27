### Limitations

* same GraphQL objects cannot be selected multiple times with different fields.
* due to the custom logic required for deserialization of polymorphic types and default enum values only Jackson is
currently supported.
