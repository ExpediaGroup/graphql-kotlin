This website was created with [Docusaurus](https://docusaurus.io/).

# What's In This Document

This document is a customized version of the standard Docusaurus README, with emphasis on the things we want people
doing in our docs, and with content removed when it's things we dont want people doing in these docs.

- [What's In This Document](#whats-in-this-document)
- [Get Started in 5 Minutes](#get-started-in-5-minutes)
  - [Directory Structure](#directory-structure)
- [Editing Content](#editing-content)
  - [Editing an existing docs page](#editing-an-existing-docs-page)
- [Adding Content](#adding-content)
  - [Adding a new docs page to an existing sidebar](#adding-a-new-docs-page-to-an-existing-sidebar)
- [Full Documentation](#full-documentation)

# Get Started in 5 Minutes

- Make sure all the dependencies for the website are installed:

```sh
# Install the correct version of Node/NPM with NVM
nvm install

# Install dependencies
npm install
```
- Run your dev server:

```sh
npm start
```

## Directory Structure

Your project file structure should look something like this

```
my-docusaurus/
  docs/
    doc-1.md
    doc-2.md
    doc-3.md
  website/
    core/
    node_modules/
    pages/
    static/
      css/
      img/
    package.json
    sidebar.json
    siteConfig.js
```

If you look at Docusaurus docs, you'll notice that we're not using the blog functionality.

# Editing Content

## Editing an existing docs page

Edit docs by navigating to `docs/` and editing the corresponding document:

`docs/doc-to-be-edited.md`

```markdown
---
id: page-needs-edit
title: This Doc Needs To Be Edited
---

Edit me...
```

For more information about docs, click [here](https://docusaurus.io/docs/en/navigation)

# Adding Content

## Adding a new docs page to an existing sidebar

1. Create the doc as a new markdown file in `/docs`, example `docs/newly-created-doc.md`. (It's our practice to use an
   ID value which is the file name, minus the '.md'. There's one exception to that rule in the docs right now. Please
   don't add any more.)

```md
---
id: newly-created-doc
title: This Doc Needs To Be Edited
---

My new content here..
```

1. Refer to that doc's ID in an existing sidebar in `website/sidebar.json`:

```javascript
// Add newly-created-doc to the Getting Started category of docs
{
  "docs": {
    "Getting Started": [
      "quick-start",
      "newly-created-doc" // new doc here
    ],
    ...
  },
  ...
}
```

For more information about adding new docs, click [here](https://docusaurus.io/docs/en/navigation)

# Full Documentation

Full documentation for Docusaurus can be found on the [website](https://docusaurus.io/).
