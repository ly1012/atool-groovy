package org.qadoc.atool.groovy.remote

class SSHFactory {

    static final String SSHJ = "net.schmizz.sshj.SSHClient"
    static final String GANYMED_SSH2 = "ch.ethz.ssh2.Connection"

    public enum Engine{
        SSHJ,   // group: 'com.hierynomus', name: 'sshj'
        GANYMED_SSH2,  //group: 'ch.ethz.ganymed', name: 'ganymed-ssh2'
        AUTO    // 自动选择
    }

    static SSHHelper engine(Engine engine){
        switch (engine){
            case Engine.SSHJ:
                return new SshjUtil()
            case Engine.GANYMED_SSH2:
                return new GanymedSsh2Util()
            case Engine.AUTO:
            default:
                if (hasClass(GANYMED_SSH2)){
                    return new GanymedSsh2Util()
                }
                if (hasClass(SSHJ)){
                    return new SshjUtil()
                }
                return new GanymedSsh2Util()
        }
    }

    private static boolean hasClass(String className){
        try{
            Class.forName(className)
            return true
        }catch(ClassNotFoundException e){
            return false
        }
    }

}
