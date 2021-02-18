module.exports = {
  title: "GraphQL Kotlin",
  tagline: "Libraries for running a GraphQL server in Kotlin",
  url: "https://expediagroup.github.io",
  baseUrl: "/graphql-kotlin/",
  organizationName: "ExpediaGroup",
  projectName: "graphql-kotlin",
  scripts: [
    "https://buttons.github.io/buttons.js"
  ],
  favicon: "img/favicon.ico",
  customFields: {
    repoUrl: "https://github.com/ExpediaGroup/graphql-kotlin"
  },
  onBrokenLinks: "log",
  onBrokenMarkdownLinks: "log",
  presets: [
    [
      "@docusaurus/preset-classic",
      {
        docs: {
          routeBasePath: '/',
          editUrl: "https://github.com/ExpediaGroup/graphql-kotlin/tree/master/docs/",
          showLastUpdateAuthor: true,
          showLastUpdateTime: true
        }
      }
    ]
  ],
  plugins: [],
  themeConfig: {
    navbar: {
      title: "GraphQL Kotlin",
      logo: {
        src: "img/EG_Icon_White_on_Blue.png"
      },
      items: [
        {
          href: "https://github.com/ExpediaGroup/graphql-kotlin",
          label: "GitHub",
          position: "left"
        },
        {
          type: 'docsVersionDropdown',
          position: 'right'
        }
      ]
    },
    image: "img/undraw_online.svg",
    footer: {
      links: [],
      copyright: "Copyright © 2021 Expedia, Inc.",
      logo: {
        src: "img/Expedia-Group-Logo_E-Stacked.png"
      }
    },
    algolia: {
      apiKey: "b23761059e66eefd46f5f665a2d4537a",
      indexName: "graphql-kotlin"
    }
  }
}
