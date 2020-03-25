package org.qadoc.atool.groovy.security

import org.qadoc.atool.groovy.security.MD5Util

public class SecurityHelper {

    public static MD5Util md5 = new MD5Util()

    MD5Util getMd5(){ md5 }

}
