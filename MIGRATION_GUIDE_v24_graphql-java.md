# Migration Guide: Upgrading to graphql-java v24.x and java-dataloader v4.x/v5.x

This guide covers breaking changes and required code updates when upgrading to graphql-java v24.x and java-dataloader v4.x/v5.x, as well as the impact on downstream users of this library.

## Key Breaking Changes

### 1. DataLoaderOptions is now immutable
- The mutative `setXXX` methods (e.g., `setStatisticsCollector`) now return a builder, not a DataLoaderOptions instance.
- You must call `.build()` after setting options to get a DataLoaderOptions instance.
- Example:
  ```kotlin
  val options = DataLoaderOptions.newOptions()
      .setStatisticsCollector(::SimpleStatisticsCollector)
      .build()
  ```

### 2. BatchLoader lambda typing
- The lambda passed to `DataLoaderFactory.newDataLoader` must have its parameter types specified explicitly.
- Example:
  ```kotlin
  DataLoaderFactory.newDataLoader(
      { keys: List<MyKeyType> -> ... },
      options
  )
  ```

### 3. Removal of mutative methods
- Any code using the old mutative methods (e.g., `.setStatisticsCollector(...)` without `.build()`) will break.

### 4. DataLoaderOptions builder pattern
- All customizations must use the builder pattern and call `.build()`.

## Example Migration

**Before:**
```kotlin
DataLoaderFactory.newDataLoader(
    { keys -> ... },
    DataLoaderOptions.newOptions().setStatisticsCollector(::SimpleStatisticsCollector)
)
```

**After:**
```kotlin
DataLoaderFactory.newDataLoader(
    { keys: List<MyKeyType> -> ... },
    DataLoaderOptions.newOptions()
        .setStatisticsCollector(::SimpleStatisticsCollector)
        .build()
)
```

## Impact on Downstream Users
- If you expose APIs or extension points that expect users to provide or customize DataLoader/DataLoaderOptions, or if you document/test usage patterns that are now broken, your downstream users will need to update their code to match the new APIs.
- Any direct usage of DataLoaderOptions or DataLoaderFactory will require migration as shown above.

## Recommendations
- Update your own documentation and migration guides to highlight these breaking changes.
- Provide migration examples for common usage patterns.
- If possible, add deprecation warnings or migration helpers in your own APIs to ease the transition for your users.

## References
- [graphql-java v24.0 Release Notes](https://github.com/graphql-java/graphql-java/releases/tag/v24.0)
- [java-dataloader v4.0.0 Release Notes](https://github.com/graphql-java/java-dataloader/releases/tag/v4.0.0)

---

If you have further questions or encounter issues, please open an issue in the repository or consult the official release notes linked above.
