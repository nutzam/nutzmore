package org.nutz.plugins.zdoc.markdown;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.lang.Strings;

public class MarkdownTest {

    @Test
    public void test_html_special_char() {
        assertEquals("<p>A&nbsp;B\n</p>", _HTML("A&nbsp;B"));
        assertEquals("<p>A&copy;B\n</p>", _HTML("A&copy;B"));
    }

    private String _HTML(String md) {
        return Strings.trim(Markdown.toHtml(md));
    }

}
