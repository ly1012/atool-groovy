package org.qadoc.atool.groovy.remote

/**
 * SSH连接工具类
 * @author LiYun
 * 2017/12/12 15:25
 */
interface SSHHelper {

    /**
     * 执行Shell命令
     * @param ip 远程机器IP
     * @param username 远程机器连接用户名
     * @param password 远程机器连接密码
     * @param script 待执行脚本
     * @return 脚本执行结果
     */
    abstract String exec(String ip, String username, String password, String script)

}
