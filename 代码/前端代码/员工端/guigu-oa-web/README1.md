## 下载并安装node

```shell
# 如果都能显示版本号就没问题了
node -v
npm -v
# 配置npm淘宝镜像
npm config set registry http://registry.npm.taobao.org/
```



## 下载Vue脚手架

```shell
# 全局下载
npm install -g @vue/cli
# 查看版本
vue --version
```



## 利用Vue脚手架创建项目

```shell
# 在cmd窗口中执行启动命令， 第三部分是项目名称
vue create vue-app
=> 选择 第三项（Manually select features）
=》 通过space键勾选Router和Vuex
=> 选择 2.x
=> 后面都按enter键选择默认的
=》 等待下载项目及其依赖包
```



## 运行启动项目

```shell
# 进行项目根目标, 启动项目
npm run serve
# 访问项目
http://glkt-api.atguigu.cn:8080/
```



## 创建项目的配置文件：vue.config.js

```js
module.exports= {

	lintOnSave: false, // 禁用eslint
	
	devServer: {
		open: true,
		// 配置代理服务器
		proxy:{
			"/api": {
				target: "http://39.98.123.211",
				changeOrigin: true,
				pathRewrite: {
					"^/api" : ""
				}
			}
		}
	},
}

/* 注意： 配置后需要重新启动 */
```

