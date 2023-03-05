// 解决内网穿透，添加该文件
module.exports = {
  devServer: {
    host: '0.0.0.0',
    port: 9090,
    hot: true,
    disableHostCheck: true,
  },
}
