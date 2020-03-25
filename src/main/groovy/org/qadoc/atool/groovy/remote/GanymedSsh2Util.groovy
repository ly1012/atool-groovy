package org.qadoc.atool.groovy.remote

import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.Session

class GanymedSsh2Util implements SSHHelper{


    @Override
    String exec(String ip, String username, String password, String script) {
        Connection conn = new Connection(ip)
        Session sess = null
        try {
            conn.connect()
            boolean isAuthenticated = conn.authenticateWithPassword(username, password)
            if (!isAuthenticated) {
                throw new IOException("Authentication failed.")
            }
            sess = conn.openSession()
            sess.execCommand(script)
            InputStream stdout = sess.getStdout()
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout))
            StringBuilder sb = new StringBuilder()
            while(true) {
                String line = br.readLine()
                if (line == null) {
                    String var12 = sb.toString()
                    return var12
                }
                sb.append(line)
                sb.append('\n')
            }
        } catch (Exception var15) {

        } finally {
            if (sess != null) {
                sess.close()
            }
            if (conn != null) {
                conn.close()
            }
        }
        return ""
    }
}
