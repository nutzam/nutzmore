package org.nutz.plugins.zdoc.msword;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNum;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.NutMap;
import org.nutz.lang.util.Tag;
import org.nutz.plugins.zdoc.NutD;
import org.nutz.plugins.zdoc.NutDSet;
import org.nutz.plugins.zdoc.NutDSetRender;
import org.nutz.plugins.zdoc.NutDoc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnderline;

public abstract class AbstractMsWordDSetRender implements NutDSetRender {

    private XWPFDocument wdDoc;

    private XWPFNumbering numbering;

    private CTAbstractNum anb;

    private XWPFStyles styles;

    private int maxImageWidth;

    private int maxImageHeight;

    /**
     * 存放各个标题级别的下标
     */
    private List<Integer> level_seqs;

    public AbstractMsWordDSetRender() {
        level_seqs = new ArrayList<>(10);

        wdDoc = new XWPFDocument();
        styles = wdDoc.createStyles();

        numbering = wdDoc.createNumbering();

        InputStream ins = Streams.fileIn("org/nutz/plugins/zdoc/msword/numbering.xml");
        try {
            anb = CTAbstractNum.Factory.parse(ins);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
        finally {
            Streams.safeClose(ins);
        }

        this.maxImageWidth = 420;
        this.maxImageHeight = 860;

        this.addCustomHeadingStyle("H1", 1, 36, "4288BC");
        this.addCustomHeadingStyle("H2", 2, 28, "4288BC");
        this.addCustomHeadingStyle("H3", 3, 24, "4288BC");
        this.addCustomHeadingStyle("H4", 4, 20, "000000");
        this.addCustomHeadingStyle("H5", 5, 18, "000000");
        this.addCustomHeadingStyle("H6", 6, 16, "000000");

    }

    void addCustomHeadingStyle(String strStyleId,
                               int headingLevel,
                               int pointSize,
                               String hexColor) {
        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId(strStyleId);

        CTString styleName = CTString.Factory.newInstance();
        styleName.setVal(strStyleId);
        ctStyle.setName(styleName);

        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
        indentNumber.setVal(BigInteger.valueOf(headingLevel));

        // lower number > style is more prominent in the formats bar
        ctStyle.setUiPriority(indentNumber);

        CTOnOff onoffnull = CTOnOff.Factory.newInstance();
        ctStyle.setUnhideWhenUsed(onoffnull);

        // style shows up in the formats bar
        ctStyle.setQFormat(onoffnull);

        // style defines a heading of the given level
        CTPPr ppr = CTPPr.Factory.newInstance();
        ppr.setOutlineLvl(indentNumber);
        ctStyle.setPPr(ppr);

        XWPFStyle style = new XWPFStyle(ctStyle);

        CTHpsMeasure size = CTHpsMeasure.Factory.newInstance();
        size.setVal(new BigInteger(String.valueOf(pointSize)));
        CTHpsMeasure size2 = CTHpsMeasure.Factory.newInstance();
        // size2.setVal(new BigInteger("24"));
        size2.setVal(new BigInteger(String.valueOf(pointSize)));

        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setAscii("Loma");

        CTRPr rpr = CTRPr.Factory.newInstance();
        rpr.setRFonts(fonts);
        rpr.setSz(size);
        rpr.setSzCs(size2);

        CTColor color = CTColor.Factory.newInstance();
        color.setVal(hexToBytes(hexColor));
        rpr.setColor(color);
        style.getCTStyle().setRPr(rpr);
        // is a null op if already defined

        style.setType(STStyleType.PARAGRAPH);
        styles.addStyle(style);

    }

    public static byte[] hexToBytes(String hexString) {
        HexBinaryAdapter adapter = new HexBinaryAdapter();
        byte[] bytes = adapter.unmarshal(hexString);
        return bytes;
    }

    @Override
    public void render(NutDSet ds, String target) {
        // 检查原始对象
        this.checkPrimerObj(ds);

        // 检查目标
        this.checkTarget(target);

        // 准备文档样式

        // 渲染整个文档集
        this.__do_render(ds, 1);

        // 输出
        this.writeToTarget(wdDoc);
    }

    private void __do_render(NutDSet ds, int level) {
        // 处理子文档/文档集
        for (NutD d : ds.getChildren()) {
            // 输出文档(集)标题
            XWPFParagraph h = __create_heading(level);
            this.__join_run_text(h, d.getTitle(d.getName()));

            // 目录:递归
            if (d.isSet()) {
                this.__do_render((NutDSet) d, level + 1);
            }
            // 文件:渲染
            else if (d.isDoc()) {
                // 准备上下文
                NutDoc doc = (NutDoc) d;
                NutMap c = new NutMap().attach(doc.getMeta());
                c.put("main", doc.getRootTag().toInnerHtml(false));
                c.put("tags", this.__gen_meta_list(doc.getTags()));
                c.put("authors", this.__gen_meta_list(doc.getAuthors()));

                // 渲染文档
                __do_render_doc(doc, level);
            }
            // 不可能
            else {
                throw Lang.impossible();
            }
        }

    }

    private XWPFParagraph __create_heading(int level) {
        XWPFParagraph h = wdDoc.createParagraph();
        h.setStyle("H" + level);

        // 显示标题编号
        String nbs = this.__assign_heading_nb(level);
        XWPFRun run = h.createRun();
        run.setText(nbs);

        // 返回标题段落
        return h;
    }

    private String __assign_heading_nb(int level) {
        // 确保本基本前所有的都有值
        for (int i = level_seqs.size(); i < level; i++) {
            level_seqs.add(0);
        }
        // 确保本级别后所有的元素被删除
        if (level_seqs.size() > level) {
            level_seqs = level_seqs.subList(0, level);
        }

        // 得到下标
        int il = level - 1;

        // 得到编号
        int re = level_seqs.get(il);
        level_seqs.set(il, re + 1);

        // 得到字符串
        return Lang.concat(".", level_seqs).toString();
    }

    private void __do_render_doc(NutDoc doc, int level) {
        // 渲染文档内容
        Tag tag = doc.getRootTag();
        for (Tag sub : tag.getChildTags()) {
            this.__do_render_tag(doc, sub, level);
        }
    }

    private void __do_render_tag(NutDoc doc, Tag tag, int level) {
        // 标题级别
        int hL = tag.getHeadingLevel();

        // 标题
        if (hL > 0) {
            XWPFParagraph h = this.__create_heading(hL + level);
            this.__join_paragraph(doc, h, tag);
        }
        // 列表
        else if (tag.isList()) {
            __join_list(doc, tag, 0);
        }
        // 普通段落
        else {
            XWPFParagraph pa = wdDoc.createParagraph();
            this.__join_paragraph(doc, pa, tag);
        }
    }

    private void __join_list(NutDoc doc, Tag tag, int level) {

        // restart numbering
        XWPFNum num = restartNumbering();

        XWPFParagraph pLi = null;
        for (Tag li : tag.getChildTags()) {
            pLi = wdDoc.createParagraph();
            pLi.setVerticalAlignment(TextAlignment.CENTER);
            pLi.setNumID(num.getCTNum().getNumId());

            // 这里设置缩进级别
            pLi.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(level));

            // 加入子节点
            for (Tag sub : li.getChildTags()) {
                // 列表: 递归
                if (sub.isList()) {
                    this.__join_list(doc, sub, level + 1);
                }
                // 其他
                else {
                    this.__join_ele(doc, pLi, sub);
                }
            }
        }

    }

    private XWPFNum restartNumbering() {
        XWPFAbstractNum abs = new XWPFAbstractNum(anb, numbering);
        CTAbstractNum an2 = abs.getAbstractNum();
        an2.setAbstractNumId(BigInteger.ONE);
        numbering.addAbstractNum(abs);
        BigInteger numId = numbering.addNum(an2.getAbstractNumId());
        XWPFNum num = numbering.getNum(numId);
        CTNumLvl lvlOverride = num.getCTNum().addNewLvlOverride();
        lvlOverride.setIlvl(BigInteger.ZERO);
        CTDecimalNumber number = lvlOverride.addNewStartOverride();
        number.setVal(BigInteger.ONE);
        return num;
    }

    // private BigInteger __assignListNumID(XWPFNumbering nbing, int nb_seq) {
    //
    // XWPFAbstractNum abs = new XWPFAbstractNum(anb, nbing);
    // BigInteger re = BigInteger.valueOf(nb_seq);
    // // while ((null != nbing.getAbstractNum(re))) {
    // // re = BigInteger.valueOf(nb_seq++);
    // // }
    // abs.getAbstractNum().setAbstractNumId(re);
    // re = nbing.addAbstractNum(abs);
    // return nbing.addNum(re);
    //
    // }

    private void __join_paragraph(NutDoc doc, XWPFParagraph p, Tag tag) {
        for (Tag ele : tag.getChildTags()) {
            __join_ele(doc, p, ele);
        }
    }

    private void __join_ele(NutDoc doc, XWPFParagraph p, Tag ele) {
        // 文本节点
        if (ele.isTextNode()) {
            this.__join_run_text(p, ele.getNodeValue());
        }
        // 内部链接
        else if (ele.is("A")) {
            String href = ele.attr("href");
            if (!Strings.isBlank(href)) {
                // Create hyperlink in paragraph
                CTHyperlink cLink = p.getCTP().addNewHyperlink();
                cLink.setAnchor(href);
                // Create the linked text
                CTText ctText = CTText.Factory.newInstance();
                ctText.setStringValue(ele.getText());
                CTR ctr = CTR.Factory.newInstance();
                ctr.setTArray(new CTText[]{ctText});

                // Create the formatting
                CTFonts fonts = CTFonts.Factory.newInstance();
                fonts.setAscii("Calibri Light");
                CTRPr rpr = ctr.addNewRPr();
                CTColor colour = CTColor.Factory.newInstance();
                colour.setVal("0000FF");
                rpr.setColor(colour);
                CTRPr rpr1 = ctr.addNewRPr();
                rpr1.addNewU().setVal(STUnderline.SINGLE);

                // Insert the linked text into the link
                cLink.setRArray(new CTR[]{ctr});
            }
            // 当做普通文本
            else {
                this.__join_run_text(p, ele.getText());
            }
        }
        // B/I/U/DEL|CODE
        else if (ele.is("^(B|I|U|DEL|CODE)$")) {
            XWPFRun run = p.createRun();
            this.__join_inline_ele(run, ele);
        }
        // 图片
        else if (ele.is("IMG")) {
            __join_image(doc, p, ele);
        }
        // 换行
        else if (ele.is("BR")) {
            XWPFRun run = p.createRun();
            run.addCarriageReturn();
        }
        // 其他
        else {
            this.__join_run_text(p, ele.getText());
        }
    }

    private void __join_image(NutDoc doc, XWPFParagraph p, Tag ele) {
        String src = ele.attr("src");
        if (!Strings.isBlank(src)) {
            // 计算相对于集合根部的路径
            String aph = Disks.appendPath(doc.getParent().getPath(), src);
            aph = Disks.getCanonicalPath(aph);

            // 读取图片信息和输入流
            MsWordImageInfo mwii = this.readPictureInfo(aph, true);

            // 等比缩减图片
            if (mwii.width > maxImageWidth || mwii.height > maxImageHeight) {
                float w = (float) mwii.width;
                float h = (float) mwii.height;
                float s = w / h;
                float max_w = (float) maxImageWidth;
                float max_h = (float) maxImageHeight;
                float max_s = max_w / max_h;
                float w2, h2;
                // 原图是扁的，缩减宽度
                if (s >= max_s) {
                    w2 = max_w;
                    h2 = w2 / s;
                }
                // 原图是长的，缩减高度
                else {
                    h2 = max_h;
                    w2 = h2 * s;
                }
                // 重新得到缩放后图片大小
                mwii.width = Math.round(w2);
                mwii.height = Math.round(h2);
            }

            // 插入到文档中
            XWPFRun run = p.createRun();
            try {
                run.addPicture(mwii.ins,
                               mwii.pictureType,
                               mwii.fileName,
                               Units.toEMU(mwii.width),
                               Units.toEMU(mwii.height));
            }
            catch (Exception e) {
                throw Lang.wrapThrow(e);
            }
            finally {
                Streams.safeClose(mwii.ins);
            }

        }
    }

    private void __join_inline_ele(XWPFRun run, Tag ele) {
        // 文本节点
        if (ele.isTextNode()) {
            run.setText(ele.getNodeValue());
            return;
        }
        // B
        if (ele.is("B")) {
            run.setBold(true);
        }
        // I
        else if (ele.is("I")) {
            run.setItalic(true);
        }
        // U
        else if (ele.is("U")) {
            run.setUnderline(UnderlinePatterns.SINGLE);
        }
        // DEL
        else if (ele.is("DEL")) {
            run.setStrikeThrough(true);
        }

        // 递归
        for (Tag sub : ele.getChildTags())
            this.__join_inline_ele(run, sub);
    }

    private XWPFRun __join_run_text(XWPFParagraph p, String text) {
        XWPFRun run = p.createRun();
        run.setText(text);
        return run;
    }

    private String __gen_meta_list(List<String> list) {
        return Strings.join(", ", list);
    }

    protected abstract MsWordImageInfo readPictureInfo(String aph, boolean openStream);

    protected abstract void writeToTarget(XWPFDocument wdDoc);

    protected abstract void checkTarget(String target);

    protected abstract void checkPrimerObj(NutDSet ds);

}
