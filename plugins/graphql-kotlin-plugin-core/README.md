### Limitations

* same GraphQL objects cannot be selected multiple times with different fields.
* GraphQL ID can only be a String as it is currently just a String type alias. If custom types (e.g. Int or UUID) are
to be supported we would need to generate wrapper similar to scalars.
* due to the custom logic required for deserialization of polymorphic types and default enum values only Jackson is
currently supported.
