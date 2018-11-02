# DocCloud
项目背景：
在一些大型企事业单位，工作中存在各种各样的工作文档，技术文档，规范等等。这些文档以word,xls,ppt,wps,pdf,txt存在。在此项目之前，文档的分享主要靠单位内部人员的互相发送。没有一个统一的平台对企业现存的各种文档进行统一管理。DocCloud项目提供了统一的文档管理平台。用户可以将文档上传至平台，所有其他用户可以在线查看此文档。同时满足搜索文档，分享，收藏等等一系列需求。在实践中，有百度文库，doc88，豆丁等公网项目。但是没有一个专门为企业用户服务的一个文档管理平台。
### 项目需求：
1. 文档的统一存储
2. 文档的检索
3. 文档的在线预览
4. 文档分享
5. 文档推荐
6. 文档上传下载
7. 用户的注册，登录
8. 文档权限管理
### 项目架构：
HDFS+LibreOffice6.0+solr+nginx+flume+hive+springboot+jpa+js+html+css
文档存储： HDFS
文档格式转换： LibreOffice6.0
进程间通信：hadoop ipc
全文检索： solr
日志记录服务器：ngnix
web日志采集：flume
日志分析：hive
webMvc:springboot
持久层框架：jpa
单元测试：junit4
前端：css+html+js+jquery+bootstrap
版本管理：git
依赖管理：maven
开发环境：idea
部署环境：linux
数据库：mysql
### 项目具体设计：
1. 文档的上传
		a.用户在前端点击上传按钮
		b.在本地选择上传文档
		c.开始上传
		b.服务端校验文件后缀是否符合文档格式。
			允许格式：doc,docx,ppt,pptx,xls,xlsx,pdf,txt
			目的：避免上传不能转码的文档如：exe,zip,….
		e.校验文档大小，允许128兆以下的文档上传。
		f.计算文档的md5值，判断文档是否在文库中已经存在，如果存在，告知用户已经存在。
		g.不存在，则上传至hdfs，同时数据库中保存用户上传文档信息。
		h.客户端开始提交格式转化job。原因：（考虑到数据量比较大，转换任务需要的内存，比较大，放在一台主机上，速度和性能都打不到要求。我们利用了分布式计算，同时利用hadoop短路本地读的特性，将任务发送至该文件块所在主机的文档转换守护进程。）
		I.job元数据信息保存到数据库。开启定时调度，对执行失败的任务（网络失败，内存溢出，节点故障）再次提交到集群运行。提交的方式采用hadoop ipc。提交过程中，需要将job信息序列化。（需要实现hadoop writable接口）
		j.定义文档转换守护进程。专门用来处理本节点上的文档转换（在线预览，全文检索）。该进程是hadoop ipc的服务端。客户端（web）发送rpc请求到服务端，服务端将任务元数据信息持久化BDB（小型高性能嵌入式kv数据库，利用其实现持久化队列）。进行任务的异步处理。提升客户端性能。（客户端提交任务元数据后可以马上返回，不用阻塞等待）。
		k.定义线程不断地从持久化队列中获取任务元数据。提交给JOBHandler进行任务的处理。任务处理分以下几大步骤：
	1).创建临时工作目录
	2).下载hdfs上的文档。
	3).调用libreoffice进行文档转换。
	4).提取页码，缩略图，文本信息
	5).利用文本信息在solr上建立全文索引
	6).上传结果数据
	7).删除临时目录
	8).任务回调（通过rpc告诉web任务处理的结果）
2. 下载
	客户端发送文档md5值到服务端。服务端根据md5去数据库查询该文档在hdfs上的地址。
设置响应类型：application/octet-stream
3. 预览
	客户端发送文档md5值到服务端。服务端根据md5去数据库查询该文档在hdfs上的地址。将此文档下载到web服务器的临时目录，返回客户端路径。客户端进行查看。
	服务端需要定义线程，定时清理临时目录。
4. 推荐
	相关文档（根据文档相似度）推荐：
		原理:先倒排索引，然后根据文档的频次，计算文档的余弦距离。
	协同过滤（猜您喜欢）推荐：
		先计算两两用户之间的相似度，给相似度较高的用户推荐另外一个用户购买的或查看的物品。
	因为评分数据比较少，现在一般利用用户对文档的访问次数/页面停留时间作为用户对文档的评分。
	
5. 用户行为分析
	参考用户行为分析文档。
	主要明白数据的采集，传输，存储流程。
	
6. 用户注册，登录，退出，短信通知，分享，收藏，评论，文档权限等模块不详述。

### 其他信息：
集群：7-20台之间。16 core 8G   主节点：32 core 16G 。
	数据量：10T-100T之间。
	单次任务运行时间：视文档大小而定。一般1分钟左右。
	遇到的困难：持久化队列，solr建索引，文档路径找不到。。。
	项目开发人员：一般6个人。4个后台，两个前台。
	项目开发周期：半年
	项目职责：文档上传解析（关键）+bug处理+集群搭建+数据库+CURD。

	

