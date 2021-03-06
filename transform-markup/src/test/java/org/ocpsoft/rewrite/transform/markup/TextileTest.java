/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.transform.markup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TextileTest {

    @Test
    public void testBoldText() {

        String textile = "This is *bold*!";
        String html = new Textile(false).transform(textile);

        assertEquals("<p>This is <strong>bold</strong>!</p>", html);

    }

    @Test
    public void testHeaders() {

        String textile = "h1. Header\n\nh2. Section\n\nSome text!";
        String html = new Textile(false).transform(textile);

        assertEquals("<h1>Header</h1><h2>Section</h2><p>Some text!</p>", normalize(html));

    }

    @Test
    public void testBlockquote() {

        String textile = "bq. Some quote";
        String html = new Textile(false).transform(textile);

        assertEquals("<blockquote><p>Some quote</p></blockquote>", normalize(html));

    }

    @Test
    public void testLists() {

        String textile = "* One\n* Two";
        String html = new Textile(false).transform(textile);

        assertEquals("<ul><li>One</li><li>Two</li></ul>", normalize(html).replaceAll(" ", ""));

    }

    @Test
    public void testCode() {

        String textile = "bc. private int n = 0;";
        String html = new Textile(false).transform(textile);

        assertEquals("<pre><code>private int n = 0;</code></pre>", normalize(html));

    }

    @Test
    public void testFullHtmlDocument() {

        String textile = "some text";
        String html = new Textile(true).transform(textile);

        assertTrue("DOCTYPE is missing", html.contains("<!DOCTYPE html"));
        assertTrue("html tag is missing", html.contains("<html"));
        assertTrue("body tag is missing", html.contains("<body>"));
        assertTrue("Expected text missing", html.contains("<p>some text</p>"));

    }

    private static String normalize(String s) {
        return s.replaceAll("\n", "").replaceAll("[\t ]+", " ").trim();
    }

}
