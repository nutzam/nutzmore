package org.nutz.integration.activiti;

import org.activiti.engine.impl.cfg.TransactionPropagation;
import org.activiti.engine.impl.interceptor.AbstractCommandInterceptor;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.nutz.trans.Molecule;
import org.nutz.trans.Trans;
import org.nutz.trans.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NutTransactionInterceptor extends AbstractCommandInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NutTransactionInterceptor.class);

    @Override
    public <T> T execute(final CommandConfig config, final Command<T> command) {
        LOGGER.debug("Running command with propagation {}", config.getTransactionPropagation());

        Transaction t = Trans.get();
        if (config.getTransactionPropagation() == TransactionPropagation.NOT_SUPPORTED) {
            try {
                Trans.set(null);
                return next.execute(config, command);
            }
            finally {
                if (t != null)
                    Trans.set(t);
            }
        }
        Molecule<T> m = new Molecule<T>() {
            @Override
            public void run() {
                setObj(next.execute(config, command));
            }
        };
        boolean requiresNew = config.getTransactionPropagation() == TransactionPropagation.REQUIRES_NEW;
        if (requiresNew) {
            if (t == null) {
                return Trans.exec(m);
            } else {
                try {
                    Trans.set(null);
                    return Trans.exec(m);
                } finally {
                    Trans.set(t);
                }
            }
        }
        return Trans.exec(m);
    }

}
