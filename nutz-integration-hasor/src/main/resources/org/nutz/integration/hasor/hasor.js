var ioc = {
    hasor: {
        type: "org.nutz.integration.hasor.HasorFactoryIocLoader",
        args : [{refer:"conf"}],
        fields: {
            ioc: {refer: "$ioc"}
        },
        events: {
            create: "init",
            depose: "shutdown"
        }
    },
    appContext: {
        type: "net.hasor.core.AppContext",
        factory: "$hasor#getAppContext"
    }
};