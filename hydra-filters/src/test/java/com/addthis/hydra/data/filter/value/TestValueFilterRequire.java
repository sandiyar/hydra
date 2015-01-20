/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addthis.hydra.data.filter.value;

import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestValueFilterRequire {

    private String requireFilter(String val, HashSet<String> exactValues, HashSet<String> match,
                                 HashSet<String> find, String[] contains, boolean not) {
        return new ValueFilterRequire(
                exactValues,
                null,
                match,
                null,
                find,
                null,
                contains,
                null,
                false,
                false,
                0,
                0,
                not).filter(val);
    }

    @Test
    public void nullPassThrough() {
        assertEquals(null, requireFilter(null, null, null, null, null, false));
        assertEquals("", requireFilter("", null, null, null, null, false));
    }

    @Test
    public void exactMatch() {
        HashSet<String> exactValues = new HashSet<>();
        exactValues.add("bar");
        exactValues.add("bax");
        assertEquals(null, requireFilter("foo", exactValues, null, null, null, false));
        assertEquals(null, requireFilter("foobarfoo", exactValues, null, null, null, false));
        assertEquals("bar", requireFilter("bar", exactValues, null, null, null, false));
    }

    @Test
    public void negateMatch() {
        HashSet<String> exactValues = new HashSet<>();
        exactValues.add("bar");
        exactValues.add("bax");
        assertEquals("foo", requireFilter("foo", exactValues, null, null, null, true));
        assertEquals("foobarfoo", requireFilter("foobarfoo", exactValues, null, null, null, true));
        assertEquals(null, requireFilter("bar", exactValues, null, null, null, true));
    }

    @Test
    public void contains() {
        String[] contains = new String[]{"bar", "bax"};
        assertEquals(null, requireFilter("foo", null, null, null, contains, false));
        assertEquals("bar", requireFilter("bar", null, null, null, contains, false));
    }

    @Test
    public void matches() {
        HashSet<String> matches = new HashSet<>();
        matches.add("\\d\\d");
        matches.add(".*addthis.com.*");
        assertEquals(null, requireFilter("foo", null, matches, null, null, false));
        assertEquals("s7.addthis.com/live", requireFilter("s7.addthis.com/live", null, matches, null, null, false));
        assertEquals("42", requireFilter("42", null, matches, null, null, false));
    }

    @Test
    public void find() {
        HashSet<String> find = new HashSet<>();
        find.add("^[a-z0-9]*$");
        assertEquals(null, requireFilter("-123", null, null, find, null, false));
        assertEquals("abcd", requireFilter("abcd", null, null, find, null, false));
    }

    @Test
    public void multiple() {
        HashSet<String> exactValues = new HashSet<>();
        exactValues.add("bar");
        exactValues.add("bax");
        String[] contains = new String[]{"fuz", "baz"};
        HashSet<String> matches = new HashSet<>();
        matches.add("\\d\\d");
        matches.add(".*addthis.com.*");
        assertEquals("s7.addthis.com/live", requireFilter("s7.addthis.com/live", exactValues, matches, null, null, false));
        assertEquals("bar", requireFilter("bar", exactValues, null, null, contains, false));
        assertEquals("fuz", requireFilter("fuz", null, matches, null, contains, false));
        assertEquals(null, requireFilter("moo", exactValues, matches, null, contains, false));
    }

}
