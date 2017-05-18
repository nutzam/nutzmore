package org.nutz.plugins.zdoc.markdown;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Strings;
import org.nutz.lang.util.Tag;
import org.nutz.plugins.zdoc.NutDoc;
import org.nutz.plugins.zdoc.NutDocParser;

public class MarkdownDocParser implements NutDocParser {

    private NutDoc doc;

    private int index;

    private List<MdBlock> blocks;

    // 增加一个帮助函数
    private MdBlock tryPush(MdBlock B) {
        if (null != B.type) {
            blocks.add(B);
            return new MdBlock();
        }
        return B;
    }

    // 定义内容输出函数
    private void __B_to_html(Tag tag, MdBlock B) {
        boolean isFirstLine = true;
        for (String line : B.content) {
            if (isFirstLine) {
                isFirstLine = false;
            } else {
                tag.add("br");
            }
            __line_to_html(tag, line);
        }
    }

    private void __line_to_html(Tag tag, String str) {
        String reg = "(\\*(.+)\\*)"
                     + "|(\\*\\*(.+)\\*\\*)"
                     + "|(__(.+)__)"
                     + "|(~~(.+)~~)"
                     + "|(`(.+)`)"
                     + "|(!\\[(.*)\\]\\((.+)\\))"
                     + "|(\\[(.*)\\]\\((.+)\\))"
                     + "|(https?:\\/\\/[^ ]+)";
        Pattern REG = Pattern.compile(reg);
        Matcher m = REG.matcher(str);
        int pos = 0;
        while (m.find()) {
            // console.log(m)
            // 之前的内容直接加为文本节点
            if (pos < m.start()) {
                tag.add(Tag.text(str.substring(pos, m.start())));
            }
            // EM: *xxx*
            if (null != m.group(1)) {
                tag.add("em").setText(m.group(2));
            }
            // B: **xxx**
            else if (null != m.group(3)) {
                tag.add(Tag.tag("b").setText(m.group(4)));
            }
            // B: __xxx__
            else if (null != m.group(5)) {
                tag.add("b").setText(m.group(6));
            }
            // DEL: ~~xxx~~
            else if (null != m.group(7)) {
                tag.add("del").setText(m.group(8));
            }
            // CODE: `xxx`
            else if (null != m.group(9)) {
                tag.add("code").setText(m.group(10));
            }
            // IMG: ![](xxxx)
            else if (null != m.group(11)) {
                tag.add("img").attr("title", m.group(12)).attr("src", m.group(13));
            }
            // A: [](xxxx)
            else if (null != m.group(14)) {
                tag.add("a")
                   .attr("href", m.group(16))
                   .setText(Strings.sBlank(m.group(15), m.group(16)));
            }
            // A: http://xxxx
            else if (null != m.group(17)) {
                tag.add("a").attr("href", m.group(17)).setText(Strings.sBlank(m.group(17)));
            }

            // 唯一下标
            pos = m.end();
        }
        if (pos < str.length()) {
            tag.add(Tag.text(str.substring(pos)));
        }
    }

    private void __B_to_blockquote(Tag tag, MdBlock B) {
        Tag tB = tag.add("blockquote");
        this.__B_to_html(tB, B);
        // 循环查找后续的嵌套块
        for (this.index++; this.index < this.blocks.size(); this.index++) {
            MdBlock B2 = this.blocks.get(this.index);
            if ("quote" == B2.type && B2.level > B.level) {
                __B_to_blockquote(tB, B2);
            } else {
                break;
            }
        }
        this.index--;
    }

    private void __B_to_list(Tag tag, MdBlock B) {
        Tag tList = tag.add(B.type.toLowerCase());
        Tag tLi = tList.add("li");
        this.__B_to_html(tLi, B);
        // 循环查找后续的列表项，或者是嵌套
        for (this.index++; this.index < this.blocks.size(); this.index++) {
            MdBlock B2 = this.blocks.get(this.index);
            // 继续增加
            if (B.type == B2.type && B2.level == B.level) {
                tLi = tList.add("li");
                this.__B_to_html(tLi, B2);
            }
            // 嵌套
            else if (B2.level > B.level && B2.isType("^(OL|UL)$")) {
                this.__B_to_list(tLi, B2);
            }
            // 不属于本列表，退出吧
            else {
                break;
            }
        }
        this.index--;
    };

    public void parse(NutDoc d) {
        // 初始化文档对象
        this.doc = d;
        doc.setRootIfNull("main");

        /**
         * 首先将文本拆分成段落：
         * 
         * <pre>
         * {
         *     type : "H|P|code|OL|UL|hr|Th|Tr|quote|empty", 
         *     indent : 1, 
         *     content:["line1","line2"], 
         *     codeType : null, 
         *     cellAligns : ["left", "center", "right"]
         * }
         * </pre>
         */
        this.index = 0;
        this.blocks = new ArrayList<>(50);
        String str = d.getPrimerContent();
        String[] lines = str.split("\r?\n");

        // 准备第一段
        MdBlock B = new MdBlock();
        boolean lastLineIsBlankLink = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trim = Strings.trim(line);
            int indent = Strings.countStrHeadIndent(line, 4);

            // 来吧，判断类型
            // 空段落
            if (Strings.isEmpty(trim)) {
                // 之前如果是 code，那么增加进去
                if (B.isType("^(code|UL|OL)$")) {
                    B.content.add("");
                }
                // 否则如果有段落，就结束它
                else {
                    B = this.tryPush(B);
                }
            }
            // 标题: H
            else if (line.matches("^#+ .+$")) {
                B = this.tryPush(B);
                B.type = "H";
                B.level = Strings.countStrHeadChar(line, '#');
                B.content.add(Strings.trim(line.substring(B.level)));
            }
            // 代码: code
            else if (line.matches("^```.*$")) {
                B = this.tryPush(B);
                B.type = "code";
                B.codeType = Strings.sBlank(Strings.trim(trim.substring(3)), null);
                for (i++; i < lines.length; i++) {
                    line = lines[i];
                    if (line.matches("^```.*$")) {
                        break;
                    }
                    B.content.add(line);
                }
                B = this.tryPush(B);
            }
            // 水平线: hr
            else if (line.matches("^ *[=-]{3,} *$")) {
                B = this.tryPush(B);
                B.type = "hr";
                B = this.tryPush(B);
            }
            // 表格分隔符: T
            else if ("P" == B.type
                     && B.content.size() == 1
                     && B.content.get(0).indexOf("|") > 0
                     && line.matches("^[ |:-]{6,}$")) {
                // 修改之前段落的属性
                B.type = "Th";
                B.setContent(Strings.splitIgnoreBlank(B.content.get(0), "[|]"));

                // 解析自己，分析单元格的 align
                B.cellAligns = Strings.splitIgnoreBlank(trim, "[|]");
                for (int x = 0; x < B.cellAligns.length; x++) {
                    String align = B.cellAligns[x].replaceAll("[ ]+", "");
                    Matcher m = Pattern.compile("^(:)?([-]+)(:)?$").matcher(align);
                    if (m.find()) {
                        boolean qL = !Strings.isBlank(m.group(1));
                        boolean qR = !Strings.isBlank(m.group(3));
                        if (qL && qR) {
                            B.cellAligns[x] = "center";
                        } else {
                            B.cellAligns[x] = qR ? "right" : "left";
                        }
                    }
                }

                // 推入
                B = this.tryPush(B);

                // 标识后续类型为 Tr
                B.type = "Tr";
            }
            // 有序列表: OL
            else if ((!B.hasType() || B.isType("^(OL|UL)$")) && trim.matches("^[0-9a-z][.].+$")) {
                B = this.tryPush(B);
                B.type = "OL";
                B.level = indent;
                B.content.add(Strings.trim(trim.substring(trim.indexOf('.') + 1)));
            }
            // 无序列表: UL
            else if ((!B.hasType() || B.isType("^(OL|UL)$")) && trim.matches("^[*+-][ ].+$")) {
                B = this.tryPush(B);
                B.type = "UL";
                B.level = indent;
                B.content.add(Strings.trim(trim.substring(1)));
            }
            // 缩进表示的代码
            else if (indent > 0) {
                // 只有空段落，才表示开始 code
                if (!B.hasType()) {
                    B.type = "code";
                    B.content.add(Strings.shiftIndent(line, 1, 4));
                }
                // 否则就要加入进去
                else {
                    B.content.add(trim);
                }
            }
            // 引用: quote
            else if (trim.startsWith(">")) {
                B = this.tryPush(B);
                B.type = "quote";
                B.level = Strings.countStrHeadChar(trim, '>');
                B.content.add(Strings.trim(trim.substring(B.level)));
            }
            // 普通段落融合到之前的块
            else if (B.isType("^(OL|UL|quote|P)$") && (!lastLineIsBlankLink || indent > B.level)) {
                B.content.add(trim);
            }
            // 将自己作为表格行
            else if ("Tr" == B.type) {
                B.setContent(Strings.splitIgnoreBlank(trim, "[|]"));
                B = this.tryPush(B);
                B.type = "Tr";
            }
            // 默认是普通段落 : P
            else {
                B = this.tryPush(B);
                B.type = "P";
                B.content.add(trim);
            }
            // 记录上一行
            lastLineIsBlankLink = Strings.isEmpty(trim);
        }

        // 处理最后一段
        B = this.tryPush(B);

        // 逐个输出段落
        Tag top = doc.getRoot();
        for (; this.index < this.blocks.size(); this.index++) {
            B = this.blocks.get(this.index);

            // 标题: H
            if ("H" == B.type) {
                Tag tH = top.add("h" + B.level);
                this.__line_to_html(tH, B.content.get(0));
            }
            // 代码: code
            else if ("code" == B.type) {
                Tag tCode = top.add("pre");
                tCode.setText(Strings.join("\n", B.content));
            }
            // 列表: OL | UL
            else if ("OL" == B.type || "UL" == B.type) {
                this.__B_to_list(top, B);
            }
            // 水平线: hr
            else if ("hr" == B.type) {
                top.add("hr");
            }
            // 表格
            else if ("Th" == B.type) {
                Tag tT = top.add("table");

                // 记录表头
                MdBlock THead = B;
                String[] aligns = THead.cellAligns;
                if (null == aligns)
                    aligns = new String[0];

                // 输出表头
                Tag tHead = tT.add("thead");
                Tag tRow = tHead.add("tr");
                int x = 0;
                for (String line : B.content) {
                    String align = "left";
                    if (x < aligns.length)
                        align = aligns[x];
                    Tag tCell = tRow.add("th");
                    if (!"left".equals(align)) {
                        tCell.attr("align", align);
                    }
                    this.__line_to_html(tCell, line);
                    x++;
                }

                // 输出表体
                Tag tBody = tT.add("tbody");
                for (this.index++; this.index < this.blocks.size(); this.index++) {
                    B = this.blocks.get(this.index);
                    if (B.isType("Tr")) {
                        tRow = tBody.add("tr");
                        x = 0;
                        for (String line : B.content) {
                            String align = "left";
                            if (x < aligns.length)
                                align = aligns[x];
                            Tag tCell = tRow.add("td");
                            if (!"left".equals(align)) {
                                tCell.attr("align", align);
                            }
                            this.__line_to_html(tCell, line);
                            x++;
                        }
                    }
                    // 否则退出表格模式
                    else {
                        break;
                    }
                }

                // 退回一个块
                this.index--;
            }
            // 引用: quote
            else if ("quote" == B.type) {
                this.__B_to_blockquote(top, B);
            }
            // 默认是普通段落 : P
            else {
                Tag tP = top.add("p");
                this.__B_to_html(tP, B);
            }
        }
    }
}
