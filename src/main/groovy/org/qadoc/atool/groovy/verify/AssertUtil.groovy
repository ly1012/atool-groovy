package org.qadoc.atool.groovy.verify

import io.restassured.path.json.JsonPath
import org.qadoc.atool.groovy.TG

/**
 * 断言工具类
 * author：liyun
 * time：2018-05-09
 */
class AssertUtil {

    /**
     * 断言List<GroovyRowResult>指定列的值和预期文本。自动比较所有行的该列列值。
     * @param actualRows  List<GroovyRowResult>对象（如来自groovy.sql.sqlInstance.rows(querysql)查询结果）
     * @param columnName 列名
     * @param expectedText 预期列的文本值
     */
    void equalsInRowsKey(actualRows,columnName,expectedText){
        for(def i = 0; i < actualRows.size(); i++){
            assert actualRows.get(i).get(columnName).toString() == expectedText
        }
    }

    /**
     * 断言两个json的指定key的值，支持JSONPath（用法：person.name）
     * @param actual 实际值，JSON格式
     * @param expected 预期值，JSON格式
     * @param compareKeys 需要比较的key，多个使用英文逗号","分隔
     * @return 如果两个json所有指定key的值都相等，断言成功，否则断言失败
     */
    void equalsInJsonKey(actual,expected,compareKeys){
        def actual_txt = TG.json.toJson(actual)
        def expected_txt = TG.json.toJson(expected)
        def actual_obj = TG.json.parse(actual_txt)
        def expected_obj = TG.json.parse(expected_txt)
        if(actual_obj == expected_obj){
            return
        }
        def obj1 = JsonPath.from(actual_txt)
        def obj2 = JsonPath.from(expected_txt)
        def keys = compareKeys.split(",")
        def key
        for(def i = 0; i < keys.length; i++){
            key = keys[i]
            assert obj1.get(key) == obj2.get(key)
        }
    }

    /**
     * 断言两个json除去指定key之后的值，支持JSONPath（用法：person.name）
     * @param actual 实际值，JSON格式
     * @param expected 预期值，JSON格式
     * @param uncompareKyes 需要不比较的key，多个使用英文逗号","分隔
     * @return 如果两个json除去所有指定key之后的值相等，断言成功，否则断言失败
     */
    void equalsUnJsonKey(actual,expected,uncompareKyes){
        //转换数据到相应类型
        def actual_txt = TG.json.toJson(actual)
        def expected_txt = TG.json.toJson(expected)
        def actual_obj = TG.json.parse(actual_txt)
        def expected_obj = TG.json.parse(expected_txt)
        def keys = uncompareKyes.split(",")
        def key
        for(def i = 0; i < keys.length; i++){
            key = keys[i]
            TG.json.remove(actual_obj,key)
            TG.json.remove(expected_obj,key)
        }
        equalsInJson(actual_obj,expected_obj)
    }

    /**
     * 两个JSON相等断言。如果相等，断言成功，否则断言失败。
     * @param json1 第一个JSON字符串或JSON对象：实际值
     * @param json2 第二个JSON字符串或JSON对象：预期值
     */
    void equalsInJson(actual,expected){
        def actual_obj = TG.json.parse(actual)
        def expected_obj = TG.json.parse(expected)
        assert actual_obj == expected_obj
    }

}
