const path = require('path')
// const webpack = require('webpack')

const resolve = (dir) => path.join(__dirname, dir)

module.exports = {
  runtimeCompiler: true,
  lintOnSave: true,
  publicPath: process.env.NODE_ENV === 'production' ? '/' : '/',
  configureWebpack: {},
  chainWebpack: (config) => {
    config.resolve.alias
      .set('@', resolve('src'))
      .set('@c', resolve('src/components'))
      .set('@assets', resolve('src/assets'))
      .set('@utils', resolve('src/utils'))
      .set('@views', resolve('src/views'))
  },
  css: {
    loaderOptions: {
      less: {
        modifyVars: {
        },
        javascriptEnabled: true
      }
    }
  },
  devServer: {
    port: 80,
    proxy: 'http://localhost:9081',
    open: true // 项目启动时是否自动打开浏览器，我这里设置为false,不打开，true表示打开
  }
}
