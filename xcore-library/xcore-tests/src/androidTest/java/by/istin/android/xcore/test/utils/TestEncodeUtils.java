package by.istin.android.xcore.test.utils;

import android.test.ApplicationTestCase;

import com.ziesemer.utils.codec.IByteToCharEncoder;
import com.ziesemer.utils.codec.ICharToByteDecoder;

import org.apache.commons.codec.net.URLCodec;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import by.istin.android.xcore.CoreApplication;

public class TestEncodeUtils extends ApplicationTestCase<CoreApplication>{

    public static final int COUNT = 200;

    public TestEncodeUtils() {
		super(CoreApplication.class);
	}

    private static final String TEST = "По сообщению «Вымпелком», любые звонки в Крыму и Севастополе с 20 июня обойдутся всего в 9,95 рублей за минуту (ранее 34 руб./мин), SMS — в 3,95 рубля за сообщение (ранее 9,5 руб.), мобильный интернет — в 9,95 рубля за мегабайт. При этом с 20 июня в сети «Киевстар» для россиян прекращают действовать услуги «Моя страна», «Моя планета», «Планета интернета» и «Планета Ноль». Как отмечается в пресс-релизе, «новые расценки на роуминг стирают границы между городами».";

	@Override
	protected void setUp() throws Exception {
		createApplication();
		super.setUp();
	}
	
	public void testDefaultEncoding() throws Exception {
        for (int i = 0; i < COUNT; i++) {
            String encode = URLEncoder.encode(TEST, "utf-8");
            String decode = URLDecoder.decode(encode, "utf-8");
            assertEquals(decode, TEST);
        }
    }

	public void testDefaultFastEncoding() throws Exception {
        String sampleEncode = URLEncoder.encode(TEST, "utf-8");
        for (int i = 0; i < COUNT; i++) {
            String encode = new URLCodec("utf-8").encode(TEST);
            assertEquals(sampleEncode, encode);
            String decode = new URLCodec("utf-8").decode(encode);
            assertEquals(decode, TEST);
        }
    }

	public void testDefaultFastEncoding2() throws Exception {
        String sampleEncode = URLEncoder.encode(TEST, "UTF-8");
        IByteToCharEncoder encoder = new com.ziesemer.utils.codec.impl.URLEncoder();
        ICharToByteDecoder decoder = new com.ziesemer.utils.codec.impl.URLDecoder();
        for (int i = 0; i < COUNT; i++) {
            CharBuffer encode = encoder.code(ByteBuffer.wrap(TEST.getBytes("UTF-8")));
            String actual = encode.toString();
            assertEquals(sampleEncode, actual);
            CharBuffer wrap = CharBuffer.wrap(actual);
            ByteBuffer decode = decoder.code(wrap);
            String v = new String(decode.array(), "UTF-8");
            //assertEquals(v, TEST);
        }
    }


}
