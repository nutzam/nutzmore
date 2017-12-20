package org.nutz.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.mongo.interceptor.MongoInterceptor;
import org.nutz.mongo.interceptor.ZOperationExecutor;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.operation.OperationExecutor;

/**
 * 维持了一个与 MongoDB 的连接方式，包括用户名密码等
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZMongo {

    public static ServerAddress NEW_SA(String host, int port) {
        try {
            return new ServerAddress(host,
                                     port <= 0 ? ServerAddress.defaultPort()
                                              : port);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    public static ZMongo me() {
        return me("localhost", ServerAddress.defaultPort());
    }

    public static ZMongo me(String host) {
        return me(host, ServerAddress.defaultPort());
    }

    public static ZMongo me(String host, int port) {
        return me(NEW_SA(host, port), null);
    }

    public static ZMongo me(ServerAddress sa, MongoCredential cred) {
        return me(sa, cred, null);
    }

    public static ZMongo me(ServerAddress sa,
                            MongoCredential cred,
                            MongoClientOptions mopt) {
        return new ZMongo(sa == null ? null : Lang.list(sa),
                          cred == null ? new ArrayList<MongoCredential>() : Lang.list(cred),
                          mopt);
    }

    public static ZMongo me(List<ServerAddress> sas,
                            List<MongoCredential> creds,
                            MongoClientOptions mopt) {
        return new ZMongo(sas, creds, mopt);
    }

    public static ZMongo me(MongoClient mc) {
        return new ZMongo(mc);
    }

    public static ZMongo me(String userName,
                            String password,
                            String host,
                            int port) {
        return me(userName, password, host, port, null);
    }

    public static ZMongo me(String userName,
                            String password,
                            String host,
                            int port,
                            String database) {
        return me(NEW_SA(host, port),
                  MongoCredential.createScramSha1Credential(userName,
                                                          database,
                                                          password.toCharArray()));
    }
    
    public static ZMongo uri(String uri) {
        return new ZMongo(new MongoClient(new MongoClientURI(uri)));
    }

    /**
     * 保持一个连接实例
     */
    private MongoClient moclient;

    private ZMongo(MongoClient mc) {
        this.moclient = mc;
    }

    public void close() {
        moclient.close();
    }

    /**
     * 创建一个 MongoDB 的连接
     * 
     * @param sas
     *            候选服务器连接地址
     * @param creds
     *            认证信息
     * @param mopt
     *            连接配置信息
     */
    private ZMongo(List<ServerAddress> sas,
                   List<MongoCredential> creds,
                   MongoClientOptions mopt) {
        // 确保默认配置
        if (null == mopt)
            mopt = new MongoClientOptions.Builder().build();
        this.moclient = new MongoClient(sas, creds, mopt);
    }

    /**
     * 获取数据库访问对象
     * 
     * @param dbname
     *            数据库名称
     * @return 数据库封装对象
     */
    public ZMoDB db(String dbname) {
        return db(dbname, null);
    }
    
    public ZMoDB db(String dbname, List<MongoInterceptor> interceptors) {
        DB db = new ZMongoDB2(moclient, dbname);
        if (interceptors != null && interceptors.size() > 0) {
            OperationExecutor proxy = (OperationExecutor) Mirror.me(DB.class).getValue(db, "executor");
            ZOperationExecutor executor = new ZOperationExecutor(proxy, interceptors);
            Mirror.me(DB.class).setValue(db, "executor", executor);
        }
        return new ZMoDB(db);
    }

    /**
     * @return 当前服务器的数据库名称列表
     */
    public List<String> dbnames() {
        return moclient.listDatabaseNames().into(new ArrayList<String>());
    }
    
    
    /**
     * 获取collection对象 - 指定Collection
     * @param dbName
     * @param collName
     * @return
     */
    public MongoCollection<Document> getMongoCollection(String dbName,String collName){
         if (Strings.isBlank(dbName)) {
             return null;
         }
         if (Strings.isBlank(collName)) {
             return null;
         }
        return moclient.getDatabase(dbName).getCollection(collName);
    }
}
