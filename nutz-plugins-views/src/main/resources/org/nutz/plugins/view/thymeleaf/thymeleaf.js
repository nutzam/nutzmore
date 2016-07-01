var ioc = {
    thymeleafProperties : {
        type: "org.nutz.plugins.view.thymeleaf.ThymeleafProperties",
        fields: {
            prefix: "/WEB-INF/template/",
            suffix: ".html",
            mode: "HTML5",
            encoding: "UTF-8",
            contentType: "text/html",
            cache: true,
            cacheTTLMs: 3600000
        }
    }
};
