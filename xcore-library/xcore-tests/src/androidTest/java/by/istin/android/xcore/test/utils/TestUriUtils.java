package by.istin.android.xcore.test.utils;

import java.util.Collections;
import java.util.Set;

import android.app.Application;
import android.net.Uri;
import android.test.ApplicationTestCase;

import by.istin.android.xcore.utils.UriUtils;

public class TestUriUtils extends ApplicationTestCase<Application> {

    public TestUriUtils() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        createApplication();
        super.setUp();
    }

    public void testIsOpaque() {
        Uri uri = Uri.parse("mailto:nobody@google.com");
        assertNotNull(UriUtils.getQueryParameters(uri));
        assertEquals(UriUtils.getQueryParameters(uri), Collections.emptySet());
    }

    public void testEmptyQuery() {
        Uri uri = Uri.parse("http://google.com/search#activity");
        assertNotNull(UriUtils.getQueryParameters(uri));
        assertEquals(UriUtils.getQueryParameters(uri), Collections.emptySet());
    }

    public void testGetParameters() {
        Uri uri = Uri.parse("http://google.com/search?q=android&limit=200#activity");
        assertNotNull(UriUtils.getQueryParameters(uri));
        assertTrue(!UriUtils.getQueryParameters(uri).equals(
                Collections.emptySet()));
        Set<String> params = UriUtils.getQueryParameters(uri);
        assertTrue(params.size() == 2);
        assertTrue(params.contains("q"));
        assertTrue(params.contains("limit"));
        assertTrue(!params.contains("android"));
        assertTrue(!params.contains("200"));
        assertTrue(!params.contains("="));
        assertTrue(!params.contains("&"));
        assertTrue(!params.contains("?"));
        assertTrue(!params.contains("#"));
    }

    public void testNegotiateMimeTypeFromUri() {
//		TODO needs example uri
    }
}
