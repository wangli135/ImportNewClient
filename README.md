# ImportNewClient
## 功能  
1. 解析ImportNew网站首页、所有文章、资讯等类别文章显示文章列表和伯乐在线网站Android、IOS、Web前端和Python类别文章列表，点击每篇文章后显示每篇文章的具体内容
2. 显示ImportNew年度热门文章和本月热门文章
3. 删除网页中广告等内容，使每篇文章有更好地可读性

## 技术要点
1. OkHttp进行HTTP通信，获取网页内容
2. 为了更好地在手机上显示，利用正则表达式抽取出响应中所需内容，过滤无用内容
3. 利用OkHttp自带的缓存对响应对二级缓存，对图片增加内存缓存实现三级缓存
4. 编写ImgeLoader进行图片加载
5. 利用RxAndroid进行异步的网络调用并更新界面
6. 使用MVP模式对应用进行重构  
## 展示  
![运行效果](https://github.com/wangli135/ImportNewClient/blob/master/app/screenshot/app_demo.gif)
