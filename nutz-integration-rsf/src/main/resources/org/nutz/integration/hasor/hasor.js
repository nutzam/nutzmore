var ioc = {
    hasor: {
        type: "org.nutz.integration.hasor.HasorIocLoader",
        fields: {
            ioc: {refer: "$ioc"},
            nutzConfig: {refer: "$conf"},
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