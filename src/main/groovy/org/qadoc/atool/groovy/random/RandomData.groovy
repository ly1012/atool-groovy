package org.qadoc.atool.groovy.random


import org.qadoc.atool.groovy.TG

/**
 * 数据生成器
 * author：liyun
 * time：2018-05-10
 */
class RandomData {

    /**
     * 生成32位随机大写 ID
     */
    String getID_U32(){
        return UUID.randomUUID().toString().replace("-","").toUpperCase()
    }

    /**
     * 随机字符串
     * 格式：yyMMddHHmmss+三位随机数字
     * 示例：180907123728381
     */
    String yyMMddHHmmssxxx(){
        def no = (int)(Math.random()*1000)
        def d = TG.time.date2String("yyMMddHHmmss",new Date())
        def random = "$d$no"
        return random
    }

    /**
     * 获取当前时间的字符串信息，格式：yyyy-MM-dd HH:mm:ss
     */
    String now(){
        return TG.time.date2String("yyyy-MM-dd HH:mm:ss",new Date())
    }

}