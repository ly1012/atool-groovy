package org.qadoc.atool.groovy.time

import org.qadoc.atool.groovy.TG

class DateTimeUtilTest extends GroovyTestCase {

    // 2016-10-17 14:33:14
    Date when = new Date(1476685994000)
    Date whenAfter = new Date(1476686004000)
    String whenDateStr = '2016-10-17'

    void testGetExpectedDate() {
        def out = TG.time.getExpectedDate(when, Calendar.SECOND,10)
        // 2016-10-17 14:33:24
        assert out.getTime() == 1476686004000
    }

    void testAddMonthsAndDays() {
        def out = TG.time.addMonthsAndDays(whenDateStr, 1, 1)
        assert out == '2016-11-18'
    }

    void testAddMonths() {
        def out = TG.time.addMonths(whenDateStr, 2)
        assert out == '2016-12-17'
    }

    void testAddDays() {
        def out = TG.time.addDays(whenDateStr, 6)
        assert out == '2016-10-23'
    }

    void testString2Date() {
        def out = TG.time.string2Date("yyyy-MM-dd HH:mm:ss", "2016-10-17 14:33:14")
        assert out.getTime() == 1476685994000
    }

    void testDate2String() {
        def out =  TG.time.date2String("yyMMddHHmmss",new Date())
        assert out ==~ '[0-9]{12}'
    }

    void testSecondsBetween() {
        def out = TG.time.secondsBetween(when, whenAfter)
        assert out == 10
    }

    void testSqlDate2String() {
        def out = TG.time.sqlDate2String('yyyy-MM-dd HH:mm:ss','2018-12-20T14:53:18+0000')
        assert out == '2018-12-20 22:53:18'
    }
}
