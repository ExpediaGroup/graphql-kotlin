/**
 * Copyright (c) 2017-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

// See https://docusaurus.io/docs/site-config for all the possible
// site configuration options.

const siteConfig = {
    title: 'graphql-kotlin User Documentation', // Title for your website.
    tagline: 'Simplifying GraphQL integration for Kotlin applications',

    // temporarily publishing at EXPErdelfavero for development purposes. Will publish at ExpediaGroup when ready.
    //     url: 'https://ExpediaGroup.github.io',
    url: 'https://Experdelfavero.github.io', // Your website URL
    baseUrl: '/graphql-kotlin/', // Base URL for your project */
    projectName: 'graphql-kotlin',
    // organizationName: 'ExpediaGroup',
    organizationName: 'Experdelfavero',

    // For no header links in the top nav bar -> headerLinks: [],
    headerLinks: [
        { doc: 'doc-main', label: 'Docs' },
        // {doc: 'doc4', label: 'API'},
        // {page: 'help', label: 'Help'},
        // {blog: true, label: 'Blog'},
    ],

    /* path to images for header/footer */
    headerIcon: 'img/EG_Icon_White_on_Blue.png',
    footerIcon: 'img/Expedia-Group-Logo_E-Stacked.png',
    favicon: 'img/favicon.ico',

    /* Colors for website */
    colors: {
        primaryColor: '#000099',
        secondaryColor: '#01325A',
    },

    // This copyright info is used in /core/Footer.js and blog RSS/Atom feeds.
    copyright: `Copyright © ${new Date().getFullYear()} Expedia Group`,

    highlight: {
        // Highlight.js theme to use for syntax highlighting in code blocks.
        theme: 'default',
    },

    // Add custom scripts here that would be placed in <script> tags.
    scripts: ['https://buttons.github.io/buttons.js'],

    // On page navigation for the current documentation page.
    onPageNav: 'separate',
    // No .html extensions for paths.
    cleanUrl: true,

    // Open Graph and Twitter card images.
    ogImage: 'img/undraw_online.svg',
    twitterImage: 'img/undraw_tweetstorm.svg',

    // For sites with a sizable amount of content, set collapsible to true.
    // Expand/collapse the links and subcategories under categories.
    docsSideNavCollapsible: true,

    // Show documentation's last contributor's name.
    // enableUpdateBy: true,

    // Show documentation's last update time.
    // enableUpdateTime: true,

    // You may provide arbitrary config keys to be used as needed by your
    // template. For example, if you need your repo's URL...
    repoUrl: 'https://github.com/ExpediaGroup/graphql-kotlin',
};

module.exports = siteConfig;