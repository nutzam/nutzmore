package org.nutz.mongo;

import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBEncoder;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.ReadPreference;

public class ZMongoDB2 extends DB {
    
    private static final Log log = Logs.get();

    public ZMongoDB2(Mongo mongo, String name) {
        super(mongo, name);
    }

    @Override
    public CommandResult command(DBObject command,
                                 ReadPreference readPreference,
                                 DBEncoder encoder) {
        if (log.isDebugEnabled())
            log.debug("cmd= " + command.toString());
        return super.command(command, readPreference, encoder);
    }
}
