package org.qadoc.atool.groovy.math


/**
 * 数字类型处理工具类
 * time：2018-07-10
 */
class MathUtil {

    /**
     * 数字字符串转int
     * @param str 需要转换的数字
     * @return int类型
     */
    int toInt(String str){
        return Integer.valueOf(str)
    }
	
    /**
     * 数字字符串转double,四舍五入
     * @param obj 需要转换的数字
	 * @param i 需要保留的位数
     * @return double类型
     */
    double toDoubleHalf(Object obj,int i){
		BigDecimal bg = new BigDecimal(obj)
		def d = bg.setScale(i, BigDecimal.ROUND_HALF_UP).doubleValue()
        return d
    }
	
	/**
     * 数字字符串转double，进一
     * @param obj 需要转换的数字
	 * @param i 需要保留的位数
     * @return double类型
     */
    double toDoubleUp(Object obj,int i){
		BigDecimal bg = new BigDecimal(obj)
		def d = bg.setScale(i, BigDecimal.ROUND_UP).doubleValue()
        return d
    }

	/**
     * 数字字符串转double，去尾
     * @param obj 需要转换的数字
	 * @param i 需要保留的位数
     * @return double类型
     */
    double toDoubleDown(Object obj,int i){
		BigDecimal bg = new BigDecimal(obj)
		def d = bg.setScale(i, BigDecimal.ROUND_DOWN).doubleValue()
        return d
    }



}