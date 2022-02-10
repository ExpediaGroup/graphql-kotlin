We are using parameterized tests to verify code generation logic.

Code is generated based on the `src/test/testSchema.graphql` and `*.graphql` queries provided in the subdirectories. Generated
code is then compared against expected results and verified that it compiles. Serialization unit tests are run as part of
the client serializer projects and E2E integration tests are run inside plugin projects.
