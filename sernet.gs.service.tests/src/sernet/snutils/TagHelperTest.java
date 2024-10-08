package sernet.snutils;

import org.junit.Assert;
import org.junit.Test;

public class TagHelperTest {

    @Test
    public void splitTagsAllTestsInOne() {
        Assert.assertArrayEquals(
                new String[] { "foo", "bar", "tag1", "tag2", "label1", "label2", "5", "6" },
                TagHelper.getTags("foo,bar tag1 tag2, label1 , label2 , , 5,6").toArray());
    }

    @Test
    public void splitTagsSeparatedWithComma() {
        Assert.assertArrayEquals(new String[] { "foo", "bar" },
                TagHelper.getTags("foo,bar").toArray());
    }

    @Test
    public void splitTagsSeparatedWithSpace() {
        Assert.assertArrayEquals(new String[] { "foo", "bar" },
                TagHelper.getTags("foo bar").toArray());
    }

    @Test
    public void splitTagsSeparatedWithCommaAndSpace() {
        Assert.assertArrayEquals(new String[] { "foo", "bar" },
                TagHelper.getTags("foo, bar").toArray());
    }

    public void splitTagsSeparatedWithMultipleCommasAndSpaces() {
        Assert.assertArrayEquals(new String[] { "foo", "bar" },
                TagHelper.getTags("foo , , bar").toArray());
    }

    @Test
    public void splitTagsWithLeadingSpace() {
        Assert.assertArrayEquals(new String[] { "foo", "bar" },
                TagHelper.getTags(" foo, bar").toArray());
    }

    @Test
    public void splitTagsWithLeadingSpaceAndComma() {
        Assert.assertArrayEquals(new String[] { "foo", "bar" },
                TagHelper.getTags(" ,foo, bar").toArray());
    }

    @Test
    public void splitTagsWithLeadingComma() {
        Assert.assertArrayEquals(new String[] { "foo", "bar" },
                TagHelper.getTags(", foo, bar").toArray());
    }

    @Test
    public void splitTagsWithTrailingSpace() {
        Assert.assertArrayEquals(new String[] { "foo", "bar" },
                TagHelper.getTags("foo, bar ").toArray());
    }

    @Test
    public void splitTagsWithTrailingComma() {
        Assert.assertArrayEquals(new String[] { "foo", "bar" },
                TagHelper.getTags("foo, bar,").toArray());
    }

}
