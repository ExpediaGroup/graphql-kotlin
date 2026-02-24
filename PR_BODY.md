## Summary

Upgrades graphql-java from 23.1 to 26.0.beta-2, along with required transitive dependency bumps:

| Dependency | Old Version | New Version | Reason |
|-----------|-------------|-------------|--------|
| graphql-java | 23.1 | 26.0.beta-2 | Target upgrade |
| java-dataloader | 4.0.0 | 6.0.0 | Required by graphql-java 26.0.beta-2 |
| federation-graphql-java-support | 5.2.0 | 6.0.0 | Compatibility with graphql-java 25+ |

## Scope

- **Compile-only verification** -- no test execution in this beta pass
- All modules compile cleanly with `./gradlew assemble`
- Beta version: `9.0.0-beta-gj26`

## Breaking Changes Fixed

### API Migration Table

| Old Pattern | New Pattern | Reason | Modules Affected |
|---|---|---|---|
| `generateAdditionalTypes(): Set<GraphQLType>` | `generateAdditionalTypes(): Set<GraphQLNamedType>` | gj26 `additionalTypes()` accepts `Set<GraphQLNamedType>` | schema-generator |
| `KotlinDataLoader<K, V>` | `KotlinDataLoader<K : Any, V>` | java-dataloader 6.0 JSpecify non-null K bound | dataloader |
| `GraphQLTypeUtil.isList(graphQLType)` on nullable | `val type = graphQLType ?: return false; isList(type)` | JSpecify makes return types nullable | dataloader-instrumentation |
| `executionInput.executionId` (platform type) | `executionInput.executionId ?: return` (nullable) | JSpecify @Nullable on ExecutionId | dataloader-instrumentation |
| `StringValue.value` (platform type) | `StringValue.value ?: throw` (nullable) | JSpecify @Nullable on getValue() | federation |
| `TypeDefinitionRegistry.getType(name).get()` | `getTypeOrNull(name) ?: error(...)` | `getType()` deprecated since gj26 | client-generator |
| `persistedQueryError.message` (platform type) | `persistedQueryError.message ?: "fallback"` | JSpecify @Nullable on getMessage() | APQ |

### graphql-kotlin Public API Signature Changes

| Class | Change | Impact |
|---|---|---|
| `SchemaGenerator.generateAdditionalTypes()` | Return type: `Set<GraphQLType>` -> `Set<GraphQLNamedType>` | Internal method, minimal external impact |
| `KotlinDataLoader<K, V>` | Type bound: `K` -> `K : Any` | Breaking: implementations must use non-null K type (all existing usages already do) |

### JSpecify Nullability Fixes (17+)

| Category | Count | Fix Pattern |
|---|---|---|
| GraphQLType? (was platform) | 7 | Local val + null check for smart-cast |
| ExecutionId? (was platform) | 6 | Early-return with NOOP context |
| String? from StringValue.getValue() | 8 | Null check with error throw or fallback |
| K type bounds on DataLoader | 1 | Added `: Any` bound |
| Deprecated getType -> getTypeOrNull | 3 | Direct replacement |

### Files Modified (11 files across 7 modules)

- **schema-generator:** SchemaGenerator.kt
- **federation:** FederatedSchemaGeneratorHooks.kt, LinkImport.kt
- **dataloader:** KotlinDataLoader.kt
- **dataloader-instrumentation:** ExecutionInputState.kt, ExecutionStrategyState.kt, SyncExecutionExhaustedState.kt
- **APQ:** AutomaticPersistedQueriesProvider.kt
- **client-generator:** GraphQLClientGenerator.kt, DocumentExtensions.kt, generateTypeName.kt

## Federation Compatibility

- federation-graphql-java-support 6.0.0 is binary-compatible with graphql-java 26.0.beta-2 at compile time
- Federation module compiled without any graphql-java-related errors
- Only JSpecify nullability fixes needed in FederatedSchemaGeneratorHooks and LinkImport

## Known Runtime Risks

These are deferred to the testing pass (v2 requirements):
- FetchedValue duck-typing in FlowSubscriptionExecutionStrategy
- JSpecify nullability enforcement at runtime
- federation-graphql-java-support 6.0.0 forward-compatibility with gj26

## Test Plan

- [x] `./gradlew assemble` passes (compile-only)
- [x] `./gradlew publishToMavenLocal` succeeds
- [ ] Downstream weaver-kotlin-sdk compiles against these beta artifacts
- [ ] Downstream stark-graphql-sdk compiles against the full chain
