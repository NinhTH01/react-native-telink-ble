const path = require('path');
const pak = require('../package.json');

module.exports = {
  presets: ['module:metro-react-native-babel-preset'],
  plugins: [
    ['macros'],
    [
      'module-resolver',
      {
        extensions: ['.ts', '.tsx', '.js', '.jsx', '.json'],
        alias: {
          [pak.name]: path.resolve(__dirname, '..', pak.source),
          example: path.resolve(__dirname),
        },
      },
    ],
  ],
};
