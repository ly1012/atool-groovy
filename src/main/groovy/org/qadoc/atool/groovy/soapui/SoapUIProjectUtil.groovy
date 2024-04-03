package org.qadoc.atool.groovy.soapui

import com.eviware.soapui.model.project.Project
import com.eviware.soapui.support.GroovyUtils
import groovy.json.JsonSlurper
import org.qadoc.atool.groovy.TG

import java.security.InvalidParameterException

/**
 * SoapUI 项目管理工具类
 * author：liyun
 * time：2018-05-09
 */
class SoapUIProjectUtil {

    private SoapUIHelper helper

    SoapUIProjectUtil(SoapUIHelper helper){
        this.helper = helper
    }

    /**
     * 获取当前项目对象
     * @return
     */
    Project getProject(){
        return helper.testRunner.testCase.testSuite.project
    }

    /**
     * 获取当前项目所在目录（不包含项目本身）
     * @return 当前项目文件或文件夹所在目录。
     * <ul>
     * <li>对于单文件项目 E:\workspace\Code\auto\ProjectName.xml 的返回值是 E:\workspace\Code\auto</li>
     * <li>对于复合项目 E:\workspace\Code\auto\ProjectName 的返回值是 E:\workspace\Code\auto</li>
     * </ul>
     */
    String getDir(){
        def groovyUtils = new GroovyUtils(helper.context)
        return groovyUtils.projectPath
        // 或者使用 context.expand("\${projectDir}") 获取项目所在目录
    }

    /**
     * 获取当前项目路径（包含项目本身）
     * @return 当前项目路径。
     * <ul>
     * <li>对于单文件项目 E:\workspace\Code\auto\ProjectName.xml 的返回值是 E:\workspace\Code\auto\ProjectName.xml</li>
     * <li>对于复合项目 E:\workspace\Code\auto\ProjectName 的返回值是 E:\workspace\Code\auto\ProjectName</li>
     * </ul>
     */
    String getPath(){
        def project = helper.context.getTestCase().getTestSuite().getProject()
        return project.getPath()
    }

    /**
     * 获取当前项目名称
     * @return 当前项目名称
     */
    String getName(){
        return helper.testRunner.testCase.testSuite.project.name
    }

    /**
     * 设置项目属性值
     */
    void setPropertyValue(String propertyName,String propertyValue){
        helper.testRunner.testCase.testSuite.project.setPropertyValue(propertyName, propertyValue)
    }

    /**
     * 获取项目属性值
     * @return 指定 key 的项目属性值
     */
    String getPropertyValue(String propertyName){
        return helper.testRunner.testCase.testSuite.project.getPropertyValue(propertyName)
    }

    /**
     * 相对路径（根路径不包含项目名称）指定的文件转JSON对象，默认文件编码UTF-8
     * @param relativePath 相对路径：根路径不包含项目名称，前面不需要加路径分隔符“\\”或者“/”
     * @return JSON对象
     */
    Object parseByDir(String relativePath){
        getJsonObject(getDir(), relativePath)
    }

    /**
     * 相对路径（根路径包含项目名称）指定的文件转JSON对象，默认文件编码UTF-8
     * @param relativePath 相对路径：根路径包含项目名称，前面不需要加路径分隔符“\\”或者“/”
     * @return JSON对象
     */
    Object parseByPath(String relativePath){
        getJsonObject(getPath(), relativePath)
    }

    private String getJsonObject(String base, String relativePath){
        if (relativePath == null){
            throw new InvalidParameterException("relative path should not be null")
        }
        relativePath = relativePath.trim()
        if (relativePath.isEmpty()){
            throw new InvalidParameterException("relative path should not be empty")
        }
        boolean flag = true
        while (relativePath.startsWith("/") | relativePath.startsWith("\\")){
            if (relativePath.length() == 1){
                flag = false
                break
            }
            relativePath = relativePath.substring(1)
        }
        if (!flag){
            throw new InvalidParameterException("relative path should have a value, like 'a/b/response.json'")
        }
        def filepath = "$base/$relativePath"
        filepath = TG.file.getRealFilePath(filepath)
        return new JsonSlurper().parse(new File(filepath),"utf-8")
    }

}