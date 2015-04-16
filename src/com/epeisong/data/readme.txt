界面获取数据，以消息界面为例：

**********CommonMsgFragment********

** layer01 是某一列表对不同业务的数据进行合并（CommonMsgProvider）
** layer02 是某一业务不同数据来源的合并（FreightForwardProvider）
** layer03 是某一业务的具体来源，一般是网络数据。本地数据库数据在dao包下提供。
			（FreightForwardNetProvider,FreightForwardDao)

1、AsyncTask，开启异步任务，
	doInBackground方法中调用3层框架（layer01、02、03）获取数据
	onPostExecute方法中对返回的数据处理
	
2、创建CommonMsgProvider对象，根据不同的动作（loadFirst、loadOlder、loadNewer）调用
	对应方法。创建FreightForwardProvider对象（如果该列表存在其他数据，需要创建其他的Provider）

3、在FreightForwardProvider中，先调用了dao层查询本地数据，若本地没有，根据不同业务不同处理；
	本地有数据，对数据进行检漏；检漏完成后，如果数据个数不够，会去调用layer03下
	FreightForwardNetProvider获取网络数据