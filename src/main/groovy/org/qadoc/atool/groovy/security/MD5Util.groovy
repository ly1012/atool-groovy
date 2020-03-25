package org.qadoc.atool.groovy.security

import java.security.MessageDigest

/**
 * MD5 工具类
 */
class MD5Util {

    /**
     * 获取文件的 MD5 校验和
     * @param file 文件
     * @return 文件的 MD5 校验和
     */
    String getFileMD5(File file){
        MessageDigest digest = MessageDigest.getInstance("MD5")
        file.withInputStream(){is->
            byte[] buffer = new byte[8192]
            int read = 0
            while( (read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read)
            }
            is.close()
        }
        byte[] md5sum = digest.digest()
        BigInteger bigInt = new BigInteger(1, md5sum)
        return bigInt.toString(16).padLeft(32, '0')
    }

    /**
     * 获取文件的 MD5 校验和
     * @param filepath 文件路径
     * @return 文件的 MD5 校验和
     */
    String getFileMD5(String filepath){
        return getFileMD5(new File(filepath))
    }

    /**
     * 判断两个文件的 MD5 校验和是否一致
     * @param filepath1 文件1的路径
     * @param filepath2 文件2的路径
     * @return 如果相等返回true，否则返回false
     */
    boolean isEquals(String filepath1,String filepath2){
        return getFileMD5(filepath1) == getFileMD5(filepath2)
    }

}
