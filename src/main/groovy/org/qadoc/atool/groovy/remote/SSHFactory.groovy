package org.qadoc.atool.groovy.remote

class SSHFactory {

    static final String SSHJ = "net.schmizz.sshj.SSHClient"
    static final String GANYMED_SSH2 = "ch.ethz.ssh2.Connection"

    static SSHHelper engine(SSHEngine engine){
        switch (engine){
            case SSHEngine.SSHJ:
                return new SshjUtil()
            case SSHEngine.GANYMED_SSH2:
                return new GanymedSsh2Util()
            case SSHEngine.AUTO:
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
