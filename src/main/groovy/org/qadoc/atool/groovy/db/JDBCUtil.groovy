package org.qadoc.atool.groovy.db

import groovy.sql.Sql
import org.qadoc.atool.groovy.TG

/**
 * JDBC操作工具类
 * author：liyun
 * time：2018-05-08
 */
//Demo
//获取SQL实例
//def sql = new JDBCUtil(url,user,password,driver)
//to do something...
//关闭实例，释放资源
//sql.close()
class JDBCUtil {

	Sql sqlInstance

	JDBCUtil(String url, String user, String password, String driver){
		this.sqlInstance = Sql.newInstance(url, user, password, driver)
	}

	/**
	 * 批量执行SQL语句（带有事务管理），多条语句使用英文分号";" 隔开
	 * @param sqls SQL语句字符串
	 * @return 批量执行结果数组，如执行两条SQL，第一条执行成功影响了 3 行，第二条失败将返回：[3,0]
	 */
	 int[] executeBatchWithTransaction(String sqls){
		def sqls_arr = sqls.split(";")
		def result
		sqlInstance.withTransaction{
			result = sqlInstance.withBatch { stmt ->
				sqls_arr.each{
					if(it.trim().length() > 0){
						stmt.addBatch  it
					}
				}
			}
		}
		return result
	}

	/**
	 * 执行查询SQL，并将结果转换为JSON对象
	 * @param querySql 查询SQL
	 * @return JSON对象
	 */
	Object toJsonObject(String querySql){
		def rows = sqlInstance.rows(querySql)
		return TG.json.parse(rows)
	}

	/**
	 * 获取SQL连接对应的数据库的当前时间：年-月-日。示例：2018-07-16
	 * @return 当前时间，字符串
	 */
	String curDate(){
		def curdate = sqlInstance.rows("select curdate() as curdate").get(0).get("curdate")
		return curdate.toString()
	}

	/**
	 * 获取SQL连接对应的数据库的当前时间：时:分:秒。示例：00:37:16
	 * @return 当前时间，字符串
	 */
	String curTime(){
		def curtime = sqlInstance.rows("select curtime() as curtime").get(0).get("curtime")
		return curtime.toString()
	}

	/**
	 * 获取SQL连接对应的数据库的当前时间：年-月-日 时:分:秒.毫秒。示例：2018-07-16 00:37:16.0
	 * @return 当前时间，字符串
	 */
	String now(){
		def now = sqlInstance.rows("select now() as now").get(0).get("now")
		return now.toString()
	}

	/**
	 * 执行查询SQL，并比较实际结果和预期结果，按照指定列顺序比较。
	 * 1、如果超时时间内比较结果相等，结束方法。
	 * 2、如果超出超时时间，抛出异常信息。
	 * @param sql 查询SQL
	 * @param expectedList 预期结果，注意值的类型和数据库列类型保持一致。示例：[5,"返现券"]
	 * @param intervalInSeconds 执行间隔，单位：秒
	 * @param timeoutInSeconds 超时时间，单位：秒
	 */
	void wait(String sql, ArrayList expectedList,int intervalInSeconds, int timeoutInSeconds){
		long begin = System.currentTimeMillis()
		def flag
		def errorMsg
		//判断是否超时
		while(System.currentTimeMillis()-begin < timeoutInSeconds*1000){
			flag = true
			def rows = sqlInstance.rows(sql)
			if(rows.size() == 0){
				flag = false
				errorMsg = "the query results of SQL[${sql}] is empty"
				sleep(intervalInSeconds*1000)
				continue
			}
			//遍历行
			labelA:
			for(def i = 0; i < rows.size(); i++){
				def grs = rows.get(i)
				//遍历列
				for(def j = 0; j < grs.size(); j++){
					//比较实际值和预期值
					def actual = grs.getAt(j)
					def expected = expectedList[j]
					if(actual != expected){
						flag = false
						def rowIndex = i+1
						def columnIndex = j+1
						errorMsg = "the expected value is not present in ${timeoutInSeconds} seconds. ${rowIndex} row, ${columnIndex} column,actual：${actual} ，expect：${expected}"
						break labelA
					}
				}
			}
			if (flag){
				break
			}
			sleep(intervalInSeconds*1000)
		}
		if (!flag){
			throw new RuntimeException(errorMsg.toString())
		}
	}

	/**
	 * 释放资源
	 */
	void close(){
		sqlInstance.close()
	}

}
