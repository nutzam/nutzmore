package org.nutz.integration.grpc;

import org.junit.Test;

public class GrpcProtoMakerTest {

    @Test
    public void testMake() {
        GrpcProtoMaker maker = new GrpcProtoMaker();
        maker.klass = SimpleGrpcService.class;
        maker.make();
        System.out.println("-====================");
        System.out.println(maker.proto);
        System.out.println("-====================");
        System.out.println(maker.serverProxy);
        System.out.println("-====================");
        System.out.println(maker.clientProxy);
        System.out.println("-====================");
    }

}
