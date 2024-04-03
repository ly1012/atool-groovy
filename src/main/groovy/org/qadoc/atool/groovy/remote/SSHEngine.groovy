package org.qadoc.atool.groovy.remote

/**
 * ReadyAPI 中无法识别内部枚举类，所以枚举类放在和 SSHFactory 同级的地方
 */
enum SSHEngine {
    SSHJ,           //group: 'com.hierynomus', name: 'sshj'
    GANYMED_SSH2,   //group: 'ch.ethz.ganymed', name: 'ganymed-ssh2'
    AUTO            //自动选择
}
