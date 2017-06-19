package org.nutz.integration.grpc;

public interface SimpleGrpcService {

    void ping(); // 无参数无返回值
    
    String pong(); // 无参数,有返回值
    
    void hello(String name); // 有参数,无返回值
    
    String incr(String key); // 有参数,有返回值
    
    int simple(String name, int age, float x, double y, boolean flag); // 各种基本数据类型
}
