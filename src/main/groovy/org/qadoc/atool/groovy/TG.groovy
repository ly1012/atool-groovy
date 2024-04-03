package org.qadoc.atool.groovy

import com.eviware.soapui.model.testsuite.TestCaseRunContext
import com.eviware.soapui.model.testsuite.TestCaseRunner
import org.apache.log4j.Logger
import org.qadoc.atool.groovy.db.JDBCUtil
import org.qadoc.atool.groovy.excel.ExcelPOIUtil
import org.qadoc.atool.groovy.expand.LinkUtil
import org.qadoc.atool.groovy.file.FileUtil
import org.qadoc.atool.groovy.json.JsonUtil
import org.qadoc.atool.groovy.math.MathUtil
import org.qadoc.atool.groovy.random.RandomData
import org.qadoc.atool.groovy.remote.SSHFactory
import org.qadoc.atool.groovy.remote.SSHEngine
import org.qadoc.atool.groovy.remote.SSHHelper
import org.qadoc.atool.groovy.security.SecurityHelper
import org.qadoc.atool.groovy.soapui.SoapUIHelper
import org.qadoc.atool.groovy.time.DateTimeUtil
import org.qadoc.atool.groovy.verify.AssertUtil

/**
 * 工具入口类
 */
public class TG {

     // SoapUI 工具类
     public static SoapUIHelper soapui
     // Excel 工具类
     public static ExcelPOIUtil excel = new ExcelPOIUtil()
     // 随机数工具类
     public static RandomData random = new RandomData()
     // 日期时间工具类
     public static DateTimeUtil time = new DateTimeUtil()
     // 加解密、安全相关工具类
     public static SecurityHelper security = new SecurityHelper()
     // 数学工具类
     public static MathUtil math = new MathUtil()
     // 文件工具类
     public static FileUtil file = new FileUtil()
     // SSH 工具类
     public static SSHHelper ssh = SSHFactory.engine(SSHEngine.AUTO)
     // AutoLink 工具类
     public static LinkUtil link = new LinkUtil()
     // Json 工具类
     public static JsonUtil json = new JsonUtil()
     // 断言工具类
     public static AssertUtil assert_ = new AssertUtil()

     /**
      * 返回 SoapUI 工具类对象
      * @param testRunner SoapUI 中支持的 testRunner 变量，即 MockTestRunner（虚拟 TestRunner，该类实现了 TestCaseRunner 接口） 对象。
      * @param context SoapUI 中支持的 context 变量，testcase 运行上下文。
      * @param log SoapUI 中支持的 log 变量，{@link org.apache.log4j.Logger}对象
      * @return SoapUI 工具类对象
      */
     static SoapUIHelper soapui(TestCaseRunner testRunner, TestCaseRunContext context, Logger log){
          if (soapui){
               return soapui
          }
          return soapui = new SoapUIHelper(testRunner, context, log)
     }

     /**
      * 返回 SSH 工具类的一个具体实现
      * @param engine
      * @return
      */
     static SSHHelper ssh(SSHEngine engine){
          return SSHFactory.engine(engine)
     }

     /**
      * 返回 JDBCUtil 对象
      * @param url 连接字符串，jdbc:mysql://localhost:3306/test
      * @param username 用户名
      * @param password 密码
      * @param driver JDBC 驱动，com.mysql.jdbc.Driver
      * @return JDBCUtil 对象
      */
     static JDBCUtil jdbc(String url, String username, String password, String driver){
          new JDBCUtil(url, username, password, driver)
     }


}
