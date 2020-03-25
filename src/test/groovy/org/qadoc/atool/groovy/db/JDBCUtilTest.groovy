package org.qadoc.atool.groovy.db

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.qadoc.atool.groovy.TG


class JDBCUtilTest{

    final String url = "jdbc:mysql://localhost:3306/test?serverTimezone=Asia/Shanghai"
    final String user = "root"
    final String password = "xxxxx"
    final String driver = "com.mysql.jdbc.Driver"

    static JDBCUtil jdbc

    @Before
    void beforeClass(){
        jdbc = TG.jdbc(url, user, password, driver)
    }

    @After
    void afterClass(){
        jdbc.close()
    }

    void testExecuteBatchWithTransaction() {
    }

    void testToJsonObject() {
    }

    @Test
    public void testCurDate() {
        println jdbc.curDate()
    }

    @Test
    void testCurTime() {
        println jdbc.curTime()
    }

    @Test
    void testNow() {
        println jdbc.now()
    }

    void testWait() {
    }
}
