const vueConfig = {
  devServer: {
    port: 5000,
    proxy: {
      '^/': {
        target: 'http://localhost:8086',
        ws: false,
        changeOrigin: true
      }
    },
    disableHostCheck: true
  },
};
module.exports = vueConfig;
