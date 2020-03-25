package org.qadoc.atool.groovy.remote

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.connection.channel.direct.Session
import net.schmizz.sshj.transport.verification.PromiscuousVerifier

import java.nio.charset.Charset
import java.security.Security

class SshjUtil implements SSHHelper{

    @Override
    String exec(String ip, String username, String password, String script) {
        SSHClient ssh = new SSHClient();
        Security.setProperty("crypto.policy","unlimited");

        InputStream ingg = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        Session.Command cmdo = null;
        Session session = null;

        ssh.setTimeout(300);
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        StringBuffer sb = null;
        try {
            ssh.connect(ip);
            ssh.authPassword(username, password);
            session = ssh.startSession();
            session.allocateDefaultPTY(); // 必须加上这句，否则多个session的时候，session.close() 方法关闭异常，报 Timeout expired
            //执行命令
            cmdo = session.exec(script);
            ingg = cmdo.getInputStream();
            isr = new InputStreamReader(ingg, Charset.forName("UTF-8"));
            reader = new BufferedReader(isr);
            String line;
            sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                if (reader != null){
                    reader.close();
                }
                if (isr != null){
                    isr.close();
                }
                if (ingg != null){
                    ingg.close();
                }
                if (cmdo != null){
                    cmdo.close();
                }
                if (session != null){
                    session.close();
                }
                if(ssh != null){
                    ssh.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
