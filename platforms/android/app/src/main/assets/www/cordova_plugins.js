cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
  {
    "id": "com.example.hello.hello",
    "file": "plugins/com.example.hello/www/hello.js",
    "pluginId": "com.example.hello",
    "clobbers": [
      "hello"
    ]
  }
];
module.exports.metadata = 
// TOP OF METADATA
{
  "cordova-plugin-whitelist": "1.3.3",
  "com.example.hello": "0.7.0"
};
// BOTTOM OF METADATA
});