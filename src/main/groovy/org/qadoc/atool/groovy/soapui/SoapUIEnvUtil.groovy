package org.qadoc.atool.groovy.soapui


import com.eviware.soapui.model.environment.Environment

/**
 * SoapUI 环境管理工具类
 * author：liyun
 * time：2018-05-09
 */
class SoapUIEnvUtil{

    private SoapUIHelper helper

    SoapUIEnvUtil(SoapUIHelper helper){
        this.helper = helper
    }

    /**
     * 获取当前环境（即激活的环境）
     * @return 环境对象
     * <ul>
     *     <li>如果是默认环境，返回 com.eviware.soapui.model.environment.DefaultEnvironment 对象</li>
     *     <li>如果是新增的环境，返回 com.eviware.soapui.model.environment.EnvironmentImpl 对象</li>
     * </ul>
     */
    Environment getEnvironment(){
        // 获取当前工程对象，WsdlProject 是 Project 的实现类，专业版是 WsdlProjectPro
        def wsdlProject = helper.testRunner.testCase.testSuite.project
        // 获取当前激活的环境名称
        Environment environment = wsdlProject.getActiveEnvironment()
        return environment
    }

    /**
     * 获取当前环境名称
     * @return 环境名称
     */
    String getName(){
        return getEnvironment().getName()
    }

}
