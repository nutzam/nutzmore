package org.nutz.plugins.zdoc.markdown;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.lang.Strings;

public class MarkdownTest {
    
    @Test
    public void test_mix_html() {
        assertEquals("<p>A</p><p>B<b>X</b>C</p>", _HTML("A\n\nB<b>X</b>C"));
    }
    
    @Test
    public void test_indent_2space() {
        assertEquals("<ul><li>A<ul><li>B</li></ul></li></ul>", _HTML(" - A\n  - B"));
    }

    @Test
    public void test_bold_in_anchor() {
        assertEquals("<p><a href=\"aa.com\"><b>A</b></a></p>", _HTML("[**A**](aa.com)"));
    }

    @Test
    public void test_html_special_char() {
        assertEquals("<p>A&nbsp;B</p>", _HTML("A&nbsp;B"));
        assertEquals("<p>A&copy;B</p>", _HTML("A&copy;B"));
    }

    private String _HTML(String md) {
        return _HTML(md, true);
    }

    private String _HTML(String md, boolean rmWhitespace) {
        String re = Strings.trim(Markdown.toHtml(md));
        if (rmWhitespace)
            return re.replaceAll("((?<=[\\s\n>])\\s)|([\r\n])", "");
        return re;
    }

}
