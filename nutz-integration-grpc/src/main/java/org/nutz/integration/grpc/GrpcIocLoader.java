package org.nutz.integration.grpc;

import org.nutz.ioc.loader.json.JsonLoader;

public class GrpcIocLoader extends JsonLoader {

    public GrpcIocLoader() {
        super("org/nutz/integration/grpc/grpc.js");
    }
}
