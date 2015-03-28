package by.istin.android.xcore.test.utils;

import android.test.ApplicationTestCase;


import com.google.common.internal.net.PercentEscaper;


import org.apache.commons.codec.internal.net.URLCodec;

import java.net.URLDecoder;
import java.net.URLEncoder;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.app.Application;

public class TestEncodeUtils extends ApplicationTestCase<Application> {

    public static final int COUNT = 2000;

    public TestEncodeUtils() {
        super(Application.class);
    }

    private static final String TEST = "По сообщению «Вымпелком», любые звонки в Крыму и Севастополе с 20 июня обойдутся всего в 9,95 рублей за минуту (ранее 34 руб./мин), SMS — в 3,95 рубля за сообщение (ранее 9,5 руб.), мобильный интернет — в 9,95 рубля за мегабайт. При этом с 20 июня в сети «Киевстар» для россиян прекращают действовать услуги «Моя страна», «Моя планета», «Планета интернета» и «Планета Ноль». Как отмечается в пресс-релизе, «новые расценки на роуминг стирают границы между городами».";

    @Override
    protected void setUp() throws Exception {
        createApplication();
        super.setUp();
    }

    public void testDefaultEncoding() throws Exception {
        for (int i = 0; i < COUNT; i++) {
            String encode = URLEncoder.encode(TEST, "utf-8").replaceAll("\\+", "%20");
            String decode = URLDecoder.decode(encode, "utf-8");
            assertEquals(decode, TEST);
        }
    }

    public void testDefaultFastEncoding() throws Exception {
        String sampleEncode = URLEncoder.encode(TEST, "utf-8").replaceAll("\\+", "%20");
        URLCodec urlCodec = new URLCodec("utf-8");
        for (int i = 0; i < COUNT; i++) {
            String encode = urlCodec.encode(TEST).replaceAll("\\+", "%20");
            assertEquals(sampleEncode, encode);
            String decode = urlCodec.decode(encode);
            assertEquals(decode, TEST);
        }
    }

    public void testDefaultFast2Encoding() throws Exception {
        String sampleEncode = URLEncoder.encode(TEST, "utf-8").replaceAll("\\+", "%20");
        PercentEscaper percentEscaper = new PercentEscaper("-_.*", false);
        URLCodec urlCodec = new URLCodec("utf-8");
        for (int i = 0; i < COUNT; i++) {
            //String encode = UrlEscapers.urlPathSegmentEscaper().escape(TEST);
            String encode = percentEscaper.escape(TEST);
            assertEquals(sampleEncode, encode);
            String decode = urlCodec.decode(encode);
            assertEquals(decode, TEST);
        }
    }


}
