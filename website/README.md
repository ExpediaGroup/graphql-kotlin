# GraphQL Kotlin Docs

This website is built with [Docusaurus](https://docusaurus.io/).

## Running Locally

Install dependencies and start the dev server:

```sh
bun install
bun run start
```

## Cutting a New Major Version

When starting development on a new major version (e.g. going from v10 to v11):

1. **Snapshot the current docs** using the version script. This copies `docs/` into `versioned_docs/` and creates a versioned sidebar:

   ```sh
   cd website
   bun run version 11.x.x
   ```

2. **Update `docusaurus.config.js`**:
   - Set `lastVersion` to the version you just created (e.g. `'10.x.x'`)
   - Update the `current` version path to the new major (e.g. `path: '11.x.x'`)

3. **Bump the project version** in `/gradle.properties` (e.g. `12.0.0-SNAPSHOT`)
