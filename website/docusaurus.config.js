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
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'throw',
  onDuplicateRoutes: 'throw',
  presets: [
    [
      "@docusaurus/preset-classic",
      {
        docs: {
          editUrl: "https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website",
          lastVersion: '6.x.x',
          remarkPlugins: [require('mdx-mermaid')],
          showLastUpdateAuthor: true,
          showLastUpdateTime: true,
          sidebarPath: require.resolve('./sidebars.js'),
          versions: {
            current: {
              label: 'pre-release',
              path: '7.x.x'
            }
          }
        }
      }
    ]
  ],
  plugins: [],
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
      copyright: "Copyright © 2022 Expedia, Inc.",
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
