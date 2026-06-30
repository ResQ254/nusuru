module.exports = {
  root: true,
  env: {
    node: true,
    es2020: true
  },
  extends: [
    "eslint:recommended"
  ],
  parserOptions: {
    ecmaVersion: 2020
  },
  rules: {
    "indent": "off",
    "object-curly-spacing": "off",
    "comma-dangle": "off",
    "padded-blocks": "off",
    "arrow-parens": "off",
    "require-jsdoc": "off",
    "valid-jsdoc": "off",
    "eol-last": "off",
    "semi": "off"
  }
};
