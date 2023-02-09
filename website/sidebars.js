module.exports = {
  docs: {
    'Introduction': [
      'getting-started',
      'examples',
      'framework-comparison',
      'blogs-and-videos'
    ],
    'Schema Generator': [
      'schema-generator/schema-generator-getting-started',
      {
        type: 'category',
        label: 'Writing schemas with Kotlin',
        items: [
          'schema-generator/writing-schemas/schema',
          'schema-generator/writing-schemas/fields',
          'schema-generator/writing-schemas/nullability',
          'schema-generator/writing-schemas/arguments',
          'schema-generator/writing-schemas/scalars',
          'schema-generator/writing-schemas/enums',
          'schema-generator/writing-schemas/lists',
          'schema-generator/writing-schemas/interfaces',
          'schema-generator/writing-schemas/unions',
          'schema-generator/writing-schemas/nested-arguments'
        ]
      },
      {
        type: 'category',
        label: 'Customizing Schema',
        items: [
          'schema-generator/customizing-schemas/annotations',
          'schema-generator/customizing-schemas/generator-config',
          'schema-generator/customizing-schemas/documenting-schema',
          'schema-generator/customizing-schemas/excluding-fields',
          'schema-generator/customizing-schemas/renaming-fields',
          'schema-generator/customizing-schemas/directives',
          'schema-generator/customizing-schemas/deprecating-schema',
          'schema-generator/customizing-schemas/custom-type-reference',
          'schema-generator/customizing-schemas/restricting-input-output',
          'schema-generator/customizing-schemas/advanced-features'
        ]
      },
      {
        type: 'category',
        label: 'Execution',
        items: [
          'schema-generator/execution/fetching-data',
          'schema-generator/execution/async-models',
          'schema-generator/execution/exceptions',
          'schema-generator/execution/data-fetching-environment',
          'schema-generator/execution/contextual-data',
          'schema-generator/execution/optional-undefined-arguments',
          'schema-generator/execution/subscriptions',
          'schema-generator/execution/introspection'
        ]
      },
      {
        type: 'category',
        label: 'Federation',
        items: [
          'schema-generator/federation/apollo-federation',
          'schema-generator/federation/federated-schemas',
          'schema-generator/federation/federated-directives',
          'schema-generator/federation/type-resolution',
          'schema-generator/federation/federation-tracing'
        ]
      }
    ],
    'Server': [
      'server/graphql-server',
      'server/graphql-request-parser',
      'server/graphql-context-factory',
      'server/graphql-request-handler',
      'server/server-subscriptions',
      'server/automatic-persisted-queries/automatic-persisted-queries',
      {
        type: 'category',
        label: 'Data Loader',
        items: [
          'server/data-loader/data-loader',
          'server/data-loader/data-loader-instrumentation'
        ]
      },
      {
        type: 'category',
        label: 'Spring Server',
        items: [
          'server/spring-server/spring-overview',
          'server/spring-server/spring-schema',
          'server/spring-server/spring-graphql-context',
          'server/spring-server/spring-http-request-response',
          'server/spring-server/spring-beans',
          'server/spring-server/spring-properties',
          'server/spring-server/spring-subscriptions'
        ]
      },
      {
        type: 'category',
        label: "Ktor Server Plugin",
        items: [
          'server/ktor-server/ktor-overview',
          'server/ktor-server/ktor-schema',
          'server/ktor-server/ktor-graphql-context',
          'server/ktor-server/ktor-http-request-response',
          'server/ktor-server/ktor-configuration'
        ]
      }
    ],
    'Client': [
      'client/client-overview',
      'client/client-features',
      'client/client-customization',
      'client/client-serialization'
    ],
    'Build Plugins': [
      {
        type: 'category',
        label: 'Gradle Plugin',
        items: [
          'plugins/gradle-plugin-tasks',
          'plugins/gradle-plugin-usage'
        ]
      },
      {
        type: 'category',
        label: 'Maven Plugin',
        items: [
          'plugins/maven-plugin-goals',
          'plugins/maven-plugin-usage'
        ]
      },
      'plugins/hooks-provider'
    ]
  }
}
