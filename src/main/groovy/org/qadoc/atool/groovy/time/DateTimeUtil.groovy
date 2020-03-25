package org.qadoc.atool.groovy.time


import java.text.SimpleDateFormat
import java.text.ParseException

/**
 * 日期时间工具类
 * author：liyun
 * time：2018-05-14
 */
class DateTimeUtil {

    public static final String YYYY_MM_DD = "yyyy-MM-dd"
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"

    /**
     * 获取指定时间点：自定义时间度量单位<br>
     * 示例：<pre class="code">
     * //获取当前时间点10分钟后的时间点（当前时间点向前10分钟）
     * DateUtil.getSpecifiedTime(new Date(),Calendar.MINUTE,10);
     * </pre>
     * @param when 基准时间点
     * @param calendar 时间度量单位，如Calendar.MINUTE
     * @param add 向前或向后的时间长度，正值表示向前，负值表示向后
     * @return 符合条件的指定时间点
     */
    Date getExpectedDate(Date when, int calendar, int add){
        Calendar cal = Calendar.getInstance()
        cal.setTime(when)
        cal.add(calendar, add)
        return cal.getTime()
    }

    /**
     * 基准时间上加减月数和天数。入参出参都为字符串，格式："yyyy-MM-dd"，如："2018-05-14"。
     * @param when 基准时间
     * @param months_add 向前或向后的月份长度，正值表示向前，负值表示向后
     * @param days_add 向前或向后的天数长度，正值表示向前，负值表示向后
     * @return 计算后的时间
     */
    String addMonthsAndDays(String when,int months_add,int days_add){
        return addDays(addMonths(when,months_add),days_add)
    }

    /**
     * 基准时间上加减月数。入参出参都为字符串，格式："yyyy-MM-dd"，如："2018-05-14"。
     * @param when 基准时间
     * @param months_add 向前或向后的月份长度，正值表示向前，负值表示向后
     * @return 计算后的时间
     */
    String addMonths(String when, int months_add){
        Date whenDate = string2Date(YYYY_MM_DD,when)
        Date dateAfterMonths = getExpectedDate(whenDate,Calendar.MONTH,months_add)
        return date2String(YYYY_MM_DD,dateAfterMonths)
    }

    /**
     * 基准时间上加减天数。入参出参都为字符串，格式："yyyy-MM-dd"，如："2018-05-14"。
     * @param when 基准时间
     * @param days_add 向前或向后的天数长度，正值表示向前，负值表示向后
     * @return 计算后的时间
     */
    String addDays(String when, int days_add){
        Date whenDate = string2Date(YYYY_MM_DD,when)
        Date dateAfterDays = getExpectedDate(whenDate,Calendar.DAY_OF_MONTH,days_add)
        return date2String(YYYY_MM_DD,dateAfterDays)
    }

    /**
     * 日期类型转换：String -&gt; Date
     * @param pattern 指定的格式，如：yyyy-MM-dd HH:mm:ss
     * @param dateString 时间字符串，如：2016-10-17 14:33:14
     * @return Date对象
     */
    Date string2Date(String pattern, String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern)
        try {
            return sdf.parse(dateString)
        } catch (ParseException e) {
            throw new RuntimeException("format fail. pattern=$pattern,dateString=$dateString", e)
        }
    }

    /**
     * 日期类型转换：Date -&gt; String
     * @param pattern 日期格式，如：yyyy-MM-dd HH:mm:ss
     * @param date Date对象
     * @return 指定格式的日期字符串
     */
    String date2String(String pattern, Date date){
        SimpleDateFormat format = new SimpleDateFormat(pattern)
        return format.format(date)
    }

    /**
	 * 计算两个时间之间相差的秒数，“结束时间”减去“开始时间”之间的秒数.
	 * 
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @return int 秒数。如果开始时间或者结束时间为 null 的话，返回 0 。
	 */
	int secondsBetween(Date beginTime, Date endTime) {
		if (beginTime == null || endTime == null) {
			return 0;
		}
		long diff = endTime.getTime() - beginTime.getTime();
		return (int) diff / 1000;
	}
	
	
    /**
     * 数据库时间转换：2018-12-20T14:53:18+0000 转成 2018-12-20 22:53:18
     * @param pattern 日期格式，如：yyyy-MM-dd HH:mm:ss
     * @param date 数据库查询出来的日期String
     * @return 指定格式的日期字符串
     */
	String sqlDate2String(String pattern, String date){
		def date1=date.split("T")[0]+" "+date.split("T")[1].split("\\+")[0]
		def date2= string2Date(pattern,date1)
		def date3= getExpectedDate(date2, Calendar.HOUR, 8)
		return date2String(pattern,date3)
    }
}
