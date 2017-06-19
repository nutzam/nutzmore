package org.nutz.plugins.zdoc.pdf;

// import java.awt.Color;
// import java.io.File;
// import java.io.IOException;
//
// import org.nutz.lang.Lang;
// import org.nutz.lang.Strings;
// import org.nutz.lang.util.Tag;
// import org.nutz.log.Logs;
//
// import com.itextpdf.text.Anchor;
// import com.itextpdf.text.Chunk;
// import com.itextpdf.text.List;
// import com.itextpdf.text.ListItem;
// import com.itextpdf.text.Paragraph;
// import com.itextpdf.text.Section;
// import com.itextpdf.text.pdf.PdfPTable;

public class PdfHelper {

    // private PdfFonts fonts;
    //
    // public PdfHelper() {
    // try {
    // fonts = new PdfFonts();
    // }
    // catch (Exception e) {
    // throw Lang.wrapThrow(e, ZDocException.class);
    // }
    // }
    //
    // public Section section(int num, String title, int level) {
    // Paragraph p = new Paragraph(title, fonts.getHeadingFont(level));
    // p.setSpacingBefore(10);
    // p.setSpacingAfter(10);
    // return new Chapter(p, num);
    // }
    //
    // public Section addSection(Section section, String title, int level,
    // Anchor anchor) {
    // Paragraph paragraph = null;
    // if (anchor != null) {
    // paragraph = new Paragraph("", fonts.getHeadingFont(level));
    // paragraph.add(anchor);
    // } else {
    // paragraph = new Paragraph(title, fonts.getHeadingFont(level));
    // }
    // return section.addSection(paragraph);
    // }
    //
    // public ListItem LI() {
    // return new ListItem();
    // }
    //
    // public List UL() {
    // List ul = new List(false, 20);
    // ul.setIndentationLeft(20);
    // return ul;
    // }
    //
    // public List OL() {
    // List ul = new List(true, 20);
    // ul.setIndentationLeft(20);
    // return ul;
    // }
    //
    // public Paragraph p() {
    // return new Paragraph();
    // }
    //
    // public Paragraph blank() {
    // Paragraph p = p();
    // p.add(new Chunk(" ", fonts.getNormalFont()));
    // return p;
    // }
    //
    // public Paragraph normal() {
    // Paragraph p = new Paragraph();
    // p.setIndentationLeft(16);
    // p.setSpacingBefore(8);
    // p.setSpacingAfter(8);
    // return p;
    // }
    //
    // public PdfPTable codeTable() {
    // try {
    // PdfPTable table = new PdfPTable(1);
    // table.set.setPadding(10);
    // table.set .setBorderColor(new Color(200, 200, 200));
    // table.setBorderWidth(0);
    // table.setBorderWidthLeft(3);
    // return table;
    // }
    // catch (Exception e) {
    // throw Lang.wrapThrow(e);
    // }
    // }
    //
    // public Cell codeCell(String title) {
    // Cell cell = new Cell();
    // cell.setBorder(0);
    // cell.setBackgroundColor(new Color(240, 240, 240));
    // Paragraph t = this.p();
    // t.add(chunk("[" + (Strings.isBlank(title) ? "CODE" : title) + "]",
    // fonts.getCodeTypeFont()));
    // cell.add(t);
    // cell.add(blank());
    // return cell;
    // }
    //
    // public Table table(int columnCount) {
    // try {
    // Table table = new Table(columnCount);
    // table.setBorder(0);
    // table.setAlignment(Table.ALIGN_LEFT);
    // table.setPadding(4);
    // table.setBorderColor(new Color(200, 200, 200));
    // return table;
    // }
    // catch (Exception e) {
    // throw Lang.wrapThrow(e);
    // }
    // }
    //
    // public Cell cell() {
    // Cell cell = new Cell();
    // cell.setBorderWidth(1);
    // cell.setBorderColor(new Color(200, 200, 200));
    // cell.setHorizontalAlignment(Cell.ALIGN_LEFT);
    // cell.setVerticalAlignment(Cell.ALIGN_TOP);
    // return cell;
    // }
    //
    // public Chunk chunk(String text, Font font) {
    // return new Chunk(text, font);
    // }
    //
    // public Paragraph codeLine(String text) {
    // Paragraph p = p();
    // p.add(new Chunk(text, fonts.getCodeFont()));
    // return p;
    // }
    //
    // public Anchor anchor(Tag ele) {
    // String text = ele.getText();
    // String href = ele.getHref().getPath();
    // Anchor anchor = new Anchor(text, fonts.getAnchorFount());
    // if (href.startsWith("http://") || href.startsWith("https://"))
    // anchor.setReference(href);
    // else {
    // if (new File(ele.getDoc().getSource()).exists()) {
    // File x = new
    // File(ele.getDoc().getSource()).getAbsoluteFile().getParentFile();
    // if (href.contains("#"))
    // href = href.substring(0,href.indexOf("#"));
    // File toFile = new File(x.getAbsolutePath()+"/"+href);
    // if (toFile.exists()) {
    // anchor.setReference("#"+toX(toFile));
    // } else
    // Logs.getLog(getClass()).infof("Refer no found : %s from
    // %s",ele.getHref(),ele.getDoc().getSource());
    // }
    // }
    // return anchor;
    // }
    //
    // public String toX(File file) {
    // try {
    // int code = file.getCanonicalPath().toString().hashCode();
    // if (code < 0)
    // return "NUTZa"+ (code * -1);
    // return "NUTZ"+code;
    // }
    // catch (IOException e) {
    // e.printStackTrace();
    // return ""+file.getAbsolutePath().hashCode();
    // }
    // }
    //
    // public Anchor anchor(ZDoc doc, int level) {
    // Anchor anchor = new Anchor(doc.getTitle(),fonts.getHeadingFont(level));
    // anchor.setName(toX(new File(doc.getSource())));
    // return anchor;
    // }
    //
    // public Font font() {
    // return new Font(fonts.getNormalFont());
    // }

}
