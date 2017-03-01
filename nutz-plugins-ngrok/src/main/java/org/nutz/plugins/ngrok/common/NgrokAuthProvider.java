package org.nutz.plugins.ngrok.common;

public interface NgrokAuthProvider {

    boolean check(NgrokMsg auth);
    
    String[] mapping(NgrokMsg auth, NgrokMsg req);
}
