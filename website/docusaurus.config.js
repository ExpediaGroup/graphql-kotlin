module.exports = {
  title: "GraphQL Kotlin",
  tagline: "Libraries for running a GraphQL server in Kotlin",
  url: "https://expediagroup.github.io",
  baseUrl: "/graphql-kotlin/",
  organizationName: "ExpediaGroup",
  projectName: "graphql-kotlin",
  favicon: "img/favicon.ico",
  customFields: {
    repoUrl: "https://github.com/ExpediaGroup/graphql-kotlin"
  },
  markdown: {
    mermaid: true
  },
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'throw',
  onDuplicateRoutes: 'throw',
  presets: [
    [
      "@docusaurus/preset-classic",
      {
        docs: {
          editUrl: "https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website",
          lastVersion: 'current',
          showLastUpdateAuthor: true,
          showLastUpdateTime: true,
          sidebarPath: require.resolve('./sidebars.js'),
          versions: {
            current: {
              label: '7.x.x',
              path: ''
            }
          }
        }
      }
    ]
  ],
  plugins: [],
  scripts: [
    "https://buttons.github.io/buttons.js"
  ],
  themes: ['@docusaurus/theme-mermaid'],
  themeConfig: {
    image: "img/undraw_online.svg",
    colorMode: {
      defaultMode: 'dark',
    },
    prism: {
      defaultLanguage: 'kotlin',
      additionalLanguages: ['kotlin', 'groovy'],
      theme: require('prism-react-renderer/themes/github'),
      darkTheme: require('prism-react-renderer/themes/dracula')
    },
    navbar: {
      title: "GraphQL Kotlin",
      logo: {
        src: "img/EG_Icon_White_on_Blue.png",
        href: "/docs"
      },
      items: [
        {
          href: "https://github.com/ExpediaGroup/graphql-kotlin",
          label: "GitHub",
          position: "right"
        },
        {
          type: 'docsVersionDropdown',
          position: 'right'
        }
      ]
    },
    footer: {
      links: [],
      copyright: "Copyright © 2023 Expedia, Inc.",
      logo: {
        src: "img/Expedia-Group-Logo_E-Stacked.png"
      }
    },
    algolia: {
      appId: 'B2J35I9RRE',
      apiKey: 'f040c6941342dc872146a008495313a2',
      indexName: 'expediagroup-graphql-kotlin'
    }
  }
}
