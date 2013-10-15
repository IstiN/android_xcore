package by.istin.android.xcore.test.db;

import android.content.ContentValues;
import by.istin.android.xcore.test.bo.SubEntity;
import by.istin.android.xcore.test.bo.TestEntity;
import by.istin.android.xcore.utils.BytesUtils;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 */
public class MockStorage {

    //21s
    //18s
    public static final int SIZE = 200;

    //30s
    //29s
    //public static final int SIZE = 10000;

    //104s
    //86s
    //public static final int SIZE = 43000;

    public static ContentValues[] generateArray() {
        ContentValues[] contentValueses = new ContentValues[SIZE];
        for (int i = 0; i < SIZE; i++) {
            ContentValues contentValues = generateSingleEntity(i);
            contentValueses[i] = contentValues;
        }
        return contentValueses;
    }

    public static ContentValues generateSingleEntity(int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TestEntity.BOOLEAN_VALUE, i % 2);
        contentValues.put(TestEntity.BYTE_VALUE, Integer.valueOf(i).byteValue());
        contentValues.put(TestEntity.DOUBLE_VALUE, Double.valueOf(i));
        contentValues.put(TestEntity.INT_VALUE, i);
        contentValues.put(TestEntity.ID, Long.valueOf(i));
        contentValues.put(TestEntity.STRING_VALUE, String.valueOf(i));

        ContentValues subEntity = new ContentValues();
        subEntity.put(SubEntity.ID, Long.valueOf(i));
        subEntity.put(SubEntity.STRING_VALUE, String.valueOf(i));

        contentValues.put(TestEntity.SUB_ENTITY_VALUE, BytesUtils.toByteArray(subEntity));
        contentValues.put("dbEntity", SubEntity.class.getCanonicalName());
        return contentValues;
    }

}
