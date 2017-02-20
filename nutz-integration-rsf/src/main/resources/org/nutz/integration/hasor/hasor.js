var ioc = {
    hasorIocLoader: {
        type: "org.nutz.integration.hasor.HasorIocLoader",
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
        factory: "$hasorIocLoader#getAppContext"
    }
};