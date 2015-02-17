package org.nutz.integration.jsch;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * 使用说明, 声明个ioc的bean, 设置属性, 并设置create event调用init方法. <p/> TODO 断线重连之类的
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class JschPortMapping {
    
    protected static final Log log = Logs.get().setTag("jsch");

    protected String host;
    protected int port = 22;
    protected int lport = 3306;
    protected int rport = 3306;
    protected String rhost = "localhost";
    protected String user = "root";
    protected String password;
    JSch jsch;
    Session session;
    
    public JschPortMapping() {
        jsch = new JSch();
    }
    
    public void init() throws JSchException {
        session = jsch.getSession(user, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        System.out.println("Establishing Connection...");
        session.connect();
        int assinged_port= session.setPortForwardingL(lport, rhost, rport);
        log.info("local assinged_port="+assinged_port);
    }

    public void depose() {
        if (session != null)
            session.disconnect();
    }
    
    public static void main(String[] args) throws JSchException {
        JschPortMapping m = new JschPortMapping();
        m.host = ""; //TODO change yourself
        m.user = ""; //TODO change yourself
        m.password = ""; //TODO change yourself
        
        m.init();
        Lang.quiteSleep(5*60*1000);
    }
}
