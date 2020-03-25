package org.qadoc.atool.groovy.soapui

import com.eviware.soapui.config.DatabaseConnectionConfig
import com.eviware.soapui.model.environment.Environment
import com.eviware.soapui.model.testsuite.TestCaseRunContext
import com.eviware.soapui.model.testsuite.TestCaseRunner
import org.apache.log4j.Logger
import org.qadoc.atool.groovy.db.JDBCUtil

import java.security.InvalidParameterException

/**
 * SoapUI 工具入口类
 */
public class SoapUIHelper {

    protected TestCaseRunner testRunner;
    protected TestCaseRunContext context;
    protected Logger log;

    public static SoapUIEnvUtil env
    public static SoapUIProjectUtil project

    /**
     * SoapUIHelper 构造器
     * @param testRunner SoapUI 中支持的 testRunner 变量，即 MockTestRunner（虚拟 TestRunner，该类实现了 TestCaseRunner 接口） 对象。
     * @param context SoapUI 中支持的 context 变量，testcase 运行上下文。
     * @param log SoapUI 中支持的 log 变量，{@link org.apache.log4j.Logger}对象
     */
    SoapUIHelper(TestCaseRunner testRunner, TestCaseRunContext context, Logger log){
        if (testRunner == null || context == null || log == null){
            throw new InvalidParameterException("Parameters[testRunner/context/log] should be not null")
        }
        this.testRunner = testRunner
        this.context = context
        this.log = log
        env = new SoapUIEnvUtil(this)
        project = new SoapUIProjectUtil(this)
    }

    SoapUIEnvUtil getEnv(){ env }
    SoapUIProjectUtil getProject(){ project }

    /**
     * 获取当前环境指定配置项的 JDBCUtil 实例。
     * 警告：通告本方法获取实例，用完后记得调用该实例的 close() 方法释放资源。
     * @param dbname 对应 SoapUI 中 DataBases 中的 Name
     * @return {@link org.qadoc.atool.groovy.db.JDBCUtil} 实例
     */
    static JDBCUtil jdbc(String dbname){
        if (env == null){
            throw new IllegalStateException("please call TG.soapui(testRunner,context,log) first")
        }
        // 获取当前环境下所有的数据库连接配置
        Environment environment = env.getEnvironment()
        List<DatabaseConnectionConfig> dbconfigs = environment.getDatabaseConnectionContainer().getConfig().getDatabaseConnectionList()
        // 遍历数据库连接配置，找出自己想要的那个配置项
        DatabaseConnectionConfig dbconfig
        def flag = false
        for(int i = 0; i < dbconfigs.size(); i++){
            dbconfig = dbconfigs.get(i)
            if(dbconfig.getName().equals(dbname)){
                flag = true
                break
            }
        }
        //如果存在指定的数据库配置项，返回SQL实例
        if (flag){
            // jdbc:mysql://127.0.0.1:3306/mysql?user=root&password=PASS_VALUE
            def conns = dbconfig.getConnectionString().split('\\?')
            def url = conns[0]
            def user = conns[1].split('&')[0].split('=')[1]
            def password = dbconfig.getPassword()
            def driver = dbconfig.getDriver()
            return new JDBCUtil(url, user, password, driver)
        }else {
            throw new RuntimeException("JDBC Connections ${dbname} not found.".toString())
        }
    }

}
