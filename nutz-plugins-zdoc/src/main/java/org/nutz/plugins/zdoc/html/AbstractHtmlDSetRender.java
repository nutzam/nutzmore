package org.nutz.plugins.zdoc.html;

import java.util.List;

import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.tmpl.Tmpl;
import org.nutz.lang.util.NutMap;
import org.nutz.lang.util.Tag;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.zdoc.NutD;
import org.nutz.plugins.zdoc.NutDSet;
import org.nutz.plugins.zdoc.NutDSetRender;
import org.nutz.plugins.zdoc.NutDoc;

/**
 * 渲染给定的文档集到一个目标目录（非线程安全）
 * 
 * <h3>本函数假设源目录的结构如下:</h3>
 * 
 * <pre>
 * SRC-HOME
 *      _tmpl/         # 「选」输出的模板目录
 *          doc.html   # 文档输出模板，其中占位符与所有 Meta 同名。文档内容占位符为 <code>${main}</code>
 *          index.html # 索引页模板，其中主要内容占位符为 <code>${indexes}</code>
 *      css/           # 资源目录是否 copy 声明在 zdoc.conf 里
 *      js/            # 属性名为 "copy-paths"
 *      emptydir/      # 其他目录如果没有文档，则会被无视
 *      some/          # 文档可以是多级目录，总之会被解析器解析好的
 *          xxx.md
 *          yyy.md
 *      aaa.md
 *      bbb.md
 *      zdoc.conf      # 「选」整体配置文件
 * </pre>
 * 
 * <b>!注意</b> 如果没有声明模板，函数会采用下面两个默认模板:
 * 
 * <ul>
 * <li><code>org/nutz/plugins/zdoc/html/dft_tmpl_doc.html</code>
 * <li><code>org/nutz/plugins/zdoc/html/dft_tmpl_index.html</code>
 * </ul>
 * 
 * <h3>一个 <code>zdoc.conf</code> 的例子</h3>
 * 
 * <pre>
 * #------------------------------------------
 * # 集合的标题
 * title=这是本集合的整体标题
 * #------------------------------------------
 * # 集合的作者，多个作者半角逗号隔开
 * authors=xiaobai,xiaohei
 * #------------------------------------------
 * # 关键是下面的路径，如果没声明，则全查
 * # 每行格式为  路径 [: 标题]
 * paths:
 * a/b/c   : 一个集合
 * bbb.md  : 一个文档
 * # 结束
 * #------------------------------------------
 * # 要 copy 的资源路径，单行的话，路径用半角逗号分隔
 * # 多行的话，一行一个路径好了。
 * # 如果没有声明这个属性，默认为 css,js,media,image
 * copy-paths=css,js,media,image
 * #------------------------------------------
 * # 针对每个文档集所在目录，需要 copy 哪些资源文件
 * # 用一个正则表达式来声明，默认为 ^.+[.](png|jpe?g|gif)$
 * copy-set-rs=^.+[.](png|jpe?g|gif)$
 * #------------------------------------------
 * # 指明模板目录名，默认为 _tmpl
 * tmpl-dir=_tmpl
 * #------------------------------------------
 * # 指明输出的索引页名称，默认为 tree.html
 * # 如果值为 "none" 则表示不输出索引页
 * index-name=tree.html
 * </pre>
 * 
 */
public abstract class AbstractHtmlDSetRender implements NutDSetRender {

    private static final Log log = Logs.get();

    private NutMap meta;

    private Tmpl docTmpl;

    private Tmpl indexTmpl;

    private String copySetRs;

    public void render(NutDSet ds, String target) {
        // 检查目标
        log.info("HtmlDSetRender: " + target);
        this.checkTarget(target);

        // 得到元数据
        this.meta = ds.getMeta();
        log.info(Json.toJson(meta));

        // 检查原始对象是否合法
        this.checkPrimerObj(ds);
        log.info("PrimerObj Checked");

        // 读取模板
        String tmplDirName = meta.getString("tmpl-dir", "_tmpl");
        this.docTmpl = this.loadTmpl(tmplDirName, "doc.html", "dft_tmpl_doc.html");
        this.indexTmpl = this.loadTmpl(tmplDirName, "index.html", "dft_tmpl_index.html");
        log.info("Template Loaded");

        // 得到文档集要 Copy 的资源列表
        this.copySetRs = meta.getString("copy-set-rs", "^.+[.](png|jpe?g|gif)$");

        // 得到要 copy 的资源列表，并将其 Copy 的目标目录中
        String copyRss = meta.getString("copy-paths");
        if (!Strings.isBlank(copyRss)) {
            String[] rsPaths = Strings.splitIgnoreBlank(copyRss, "[\n,:]");
            log.infof("Will copy %d resources:", rsPaths.length);
            for (int i = 0; i < rsPaths.length; i++) {
                String rsph = rsPaths[i];
                log.infof(" + %d + : %s", i, rsph);
                this.copyResource(rsph);
            }
        } else {
            log.info("No resource need to be copied.");
        }

        // 渲染整个文档集
        this.__do_render(ds);

        // 生成索引页
        String indexName = meta.getString("index-name", "tree.html");
        if (!"none".equals(indexName)) {
            Tag index = this.__gen_indexes(ds);
            NutMap c = new NutMap().attach(meta);
            c.put("indexes", index.toOuterHtml(true));
            String html = indexTmpl.render(c, false);
            this.writeToTarget(indexName, html);
            log.infof("Index '%s' generated.", indexName);
        } else {
            log.info("No index need to be generated.");
        }
    }

    private Tag __gen_indexes(NutDSet ds) {
        Tag ul = Tag.tag("ul", ".ds-index");
        for (NutD d : ds.getChildren()) {
            Tag li = ul.add("li");

            // 目录的话，增加一个 <b> 并递归
            if (d.isSet()) {
                li.add("b").setText(d.getTitle(d.getName()));
                Tag ul2 = this.__gen_indexes((NutDSet) d);
                li.add(ul2);
            }
            // 文档的话，增加一个 <a>
            else if (d.isDoc()) {
                String rph = d.getPath();
                rph = Files.renameSuffix(rph, ".html");
                li.add("a").attr("href", rph).setText(d.getTitle(d.getName()));
            }
            // 不可能
            else {
                throw Lang.impossible();
            }
        }
        return ul;
    }

    private void __do_render(NutDSet ds) {
        // 首先 copy 资源
        this.copyToTarget(ds.getPath(), this.copySetRs);

        // 处理子文档/文档集
        for (NutD d : ds.getChildren()) {
            // 目录:递归
            if (d.isSet()) {
                this.__do_render((NutDSet) d);
            }
            // 文件:渲染
            else if (d.isDoc()) {
                // 准备上下文
                NutDoc doc = (NutDoc) d;
                String ph = doc.getPath();
                String taph = Files.renameSuffix(ph, ".html");
                log.info(" -> " + taph);
                NutMap c = new NutMap().attach(doc.getMeta());
                c.put("main", doc.getRootTag().toInnerHtml(false));
                c.put("tags", this.__gen_meta_list(doc.getTags(), ".doc-tags"));
                c.put("authors", this.__gen_meta_list(doc.getAuthors(), ".doc-authors"));
                c.put("path", taph);
                // TODO 这里要改成读文档的 title
                c.put("title", Files.getMajorName(doc.getName()));
                // 生成 HTML
                String html = docTmpl.render(c, false);

                // 输出至目标
                this.writeToTarget(taph, html);
            }
            // 不可能
            else {
                throw Lang.impossible();
            }
        }
    }

    private String __gen_meta_list(List<String> list, String selector) {
        Tag div = Tag.tag("div", selector);
        Tag ul = div.add("ul");
        for (String str : list)
            ul.add("li").setText(str);
        return div.toOuterHtml(true);
    }

    protected abstract Tmpl loadTmpl(String tmplDirName, String tmplName, String dftTmplName);

    protected abstract void copyToTarget(String ph, String regex);

    protected abstract void writeToTarget(String ph, String html);

    protected abstract void copyResource(String rsph);

    protected abstract void checkTarget(String target);

    protected abstract Tmpl loadTmpl(String tmplPath);

    protected abstract void checkPrimerObj(NutDSet ds);

}
