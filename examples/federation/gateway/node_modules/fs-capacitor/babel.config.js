module.exports = {
  comments: false,
  presets: [
    [
      "@babel/env",
      {
        modules: process.env.BABEL_ESM ? false : "commonjs",
        shippedProposals: true,
        loose: true
      }
    ]
  ]
};
