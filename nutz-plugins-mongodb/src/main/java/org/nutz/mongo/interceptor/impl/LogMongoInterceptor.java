package org.nutz.mongo.interceptor.impl;

import java.lang.reflect.Field;

import org.bson.BsonDocument;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.interceptor.MongoInterceptor;
import org.nutz.mongo.interceptor.MongoInterceptorChain;

import com.mongodb.operation.CommandReadOperation;
import com.mongodb.operation.CommandWriteOperation;

public class LogMongoInterceptor implements MongoInterceptor {

    protected static final Log log = Logs.get();

    protected static Field cr_command;
    protected static Field cr_databaseName;
    protected static Field cw_command;
    protected static Field cw_databaseName;
    static {
        try {
            cr_command = CommandReadOperation.class.getDeclaredField("command");
            cw_command = CommandWriteOperation.class.getDeclaredField("command");
            cr_databaseName = CommandReadOperation.class.getDeclaredField("databaseName");
            cw_databaseName = CommandWriteOperation.class.getDeclaredField("databaseName");
            
            cr_command.setAccessible(true);
            cw_command.setAccessible(true);
            cr_databaseName.setAccessible(true);
            cw_databaseName.setAccessible(true);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public void filter(MongoInterceptorChain<?> chain) {
        if (log.isDebugEnabled()) {
            try {
                BsonDocument command = null;
                String databaseName = "";
                String tag = "";
                if (chain.getReadOperation() != null && chain.getReadOperation() instanceof CommandReadOperation) {
                    CommandReadOperation cr = (CommandReadOperation)chain.getReadOperation();
                    command = (BsonDocument) cr_command.get(cr);
                    databaseName = (String) cr_databaseName.get(cr);
                    tag = "R";
                } else if (chain.getWriteOperation() != null && chain.getWriteOperation() instanceof CommandWriteOperation) {
                    CommandWriteOperation cr = (CommandWriteOperation)chain.getReadOperation();
                    command = (BsonDocument) cw_command.get(cr);
                    databaseName = (String) cw_databaseName.get(cr);
                    tag = "W";
                }
                if (command != null) {
                    log.debugf("%s : db=%s : cmd=%s", tag, databaseName, command.values().iterator().next());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        chain.doChain();
    }

}
