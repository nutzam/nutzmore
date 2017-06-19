package org.nutz.integration.grpc;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

/**
 * 通过一个接口,反向生成proto文件, 服务端代理类和客户端代理类
 *
 */
public class GrpcProtoMaker {
    
    protected static String NL = "\r\n";
    public Class<?> klass;
    public StringBuilder proto = new StringBuilder();
    public StringBuilder serverProxy = new StringBuilder();
    public StringBuilder clientProxy = new StringBuilder();
    
    public void make() {
        String className = klass.getSimpleName();
        // proto文件的message部分
        StringBuilder messages = new StringBuilder();
        
        // 添加proto文件头
        String javaPackage = klass.getPackage().getName()+".rpc";
        proto.append("syntax = \"proto3\";").append(NL);
        proto.append("option java_multiple_files = true;").append(NL);
        proto.append("option java_package = \""+javaPackage+"\";").append(NL);
        proto.append("option java_outer_classname = \""+className+"Proto\";").append(NL);
        proto.append("package " + klass.getSimpleName().toLowerCase() + ";").append(NL);
        proto.append("service " + klass.getSimpleName() + "{").append(NL);
        
        // 添加服务器代理类的头部信息
        serverProxy.append("package " + javaPackage + ";").append(NL).append(NL);
        serverProxy.append("import org.nutz.ioc.loader.annotation.*;").append(NL);
        serverProxy.append("import "+klass.getPackage().getName() + ".*;").append(NL);
        serverProxy.append("import io.grpc.stub.*;").append(NL);
        serverProxy.append("import io.grpc.*;").append(NL).append(NL);
        serverProxy.append("@IocBean").append(NL);
        serverProxy.append("public class " + className + "GrpcServerProxy extends "+ className + "Grpc." + className + "ImplBase {").append(NL);
        serverProxy.append("    @Inject public "+className+" proxy;").append(NL);
        
        clientProxy.append("package " + javaPackage + ";").append(NL).append(NL);
        clientProxy.append("import org.nutz.ioc.loader.annotation.*;").append(NL);
        clientProxy.append("import "+klass.getPackage().getName() + ".*;").append(NL);
        clientProxy.append("import io.grpc.*;").append(NL);
        //clientProxy.append("import io.grpc.stub.*;").append(NL).append(NL);
        clientProxy.append("@IocBean").append(NL);
        clientProxy.append("public class " + className + "GrpcClientProxy implements "+ className + " {").append(NL);
        clientProxy.append("    @Inject protected ManagedChannel channel;").append(NL);
        clientProxy.append(String.format("    protected %sGrpc.%sBlockingStub blockingStub;", className, className)).append(NL);
        clientProxy.append(String.format("    public void setChannel(ManagedChannel channel) {this.channel=channel;blockingStub=%sGrpc.newBlockingStub(channel);}", className)).append(NL);
        
        
        for (Method method : klass.getMethods()) {
            String methodName = Strings.upperFirst(method.getName());
            // 拼proto文件
            proto.append("    rpc ").append(methodName).append("(");
            Parameter[] parameters = method.getParameters();
            boolean voidReturn = method.getReturnType().toString().equals("void");
            String reqName = methodName + "Request";
            String replyName = methodName + "Reply";
            if (parameters.length > 0) {
                proto.append(reqName);
                messages.append("message ").append(reqName).append(" {").append(NL);
                messages.append("    ");
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    messages.append(java2proto(parameter.getType())).append(" ").append(parameter.getName());
                    messages.append(" = ").append(i+1).append(";").append(NL);
                }
                messages.append("}").append(NL);
            } else {
                proto.append("VoidMessage");
            }
            proto.append(") returns (");
            if (voidReturn) {
                proto.append("VoidMessage");
            } else {
                proto.append("").append(replyName);
                messages.append("message ").append(replyName).append(" {").append(NL);
                messages.append("    ").append(java2proto(method.getReturnType())).append(" ").append(" re = 1;").append(NL);
                messages.append("}").append(NL);
            }
            proto.append(") ");
            proto.append("{}").append(NL);
            
            // 拼服务端的代码
            serverProxy.append("    @Override public void " + method.getName() + "(");
            if (parameters.length > 0) {
                String messageName = methodName + "Request";
                serverProxy.append(messageName);
            } else {
                serverProxy.append("VoidMessage");
            }
            serverProxy.append(" req, StreamObserver<"+(voidReturn ? "VoidMessage" : methodName + "Reply")+"> responseObserver");
            serverProxy.append(") {").append(NL);
            
            serverProxy.append("        " + (voidReturn ? "" : method.getReturnType().getName() + " re = ") 
                              + "proxy." + method.getName() + "(");
            for (Parameter parameter : parameters) {
                serverProxy.append("req.get" + Strings.upperFirst(parameter.getName()) + "(),");
            }
            if (parameters.length > 0)
                serverProxy.setCharAt(serverProxy.length() - 1, ')');
            else
                serverProxy.append(")");
            serverProxy.append(";").append(NL);
            if (voidReturn) {
                serverProxy.append("        responseObserver.onNext(VoidMessage.getDefaultInstance());").append(NL);
            } else {
                serverProxy.append("        " + replyName + ".Builder reply = " + replyName + ".newBuilder();");
                serverProxy.append("        ").append(NL);
                serverProxy.append("        reply.setRe(re);").append(NL);
                serverProxy.append("        responseObserver.onNext(reply.build());").append(NL);
            }
            serverProxy.append("        responseObserver.onCompleted();").append(NL);
            serverProxy.append("    }").append(NL).append(NL);
            
            // 拼客户端的代码
            clientProxy.append("    @Override public "+method.getReturnType().getName()+" " + method.getName() + "(");
            for (Parameter parameter : parameters) {
                clientProxy.append(parameter.getType().getName()+" " + parameter.getName()+",");
            }
            if (parameters.length > 0)
                clientProxy.setLength(clientProxy.length() - 1);
            clientProxy.append("){").append(NL);
            if (parameters.length > 0) {
                clientProxy.append(String.format("        %s msg = %s.newBuilder()", reqName, reqName));
                for (Parameter parameter : parameters) {
                    clientProxy.append(String.format(".set%s(%s)", Strings.upperFirst(parameter.getName()), parameter.getName()));
                }
                clientProxy.append(".build();").append(NL);
            } else {
                clientProxy.append("        VoidMessage msg = VoidMessage.getDefaultInstance();").append(NL);
            }
            if (voidReturn) {
                clientProxy.append(String.format("        blockingStub.%s(msg);", method.getName())).append(NL);
            } else {
                clientProxy.append(String.format("        return blockingStub.%s(msg).getRe();", method.getName())).append(NL);
            }
            clientProxy.append("    }").append(NL).append(NL);
        }
        serverProxy.append("}").append(NL);
        clientProxy.append("}").append(NL);
        proto.append("}").append(NL);
        
        proto.append(messages).append(NL);
        proto.append("message VoidMessage {}").append(NL);
    }
    
    protected static String java2proto(Class<?> klass) {
        Mirror<?> mirror = Mirror.me(klass);
        if (mirror.isString()) {
            return "string";
        }
        if (mirror.isInt()) {
            return "int32";
        }
        if (mirror.isLong()) {
            return "int64";
        }
        if (mirror.isFloat()) {
            return "float";
        }
        if (mirror.isDouble()) {
            return "double";
        }
        if (mirror.isBoolean()) {
            return "bool";
        }
        throw new RuntimeException("not support yet : " + klass.getName());
    }
}
