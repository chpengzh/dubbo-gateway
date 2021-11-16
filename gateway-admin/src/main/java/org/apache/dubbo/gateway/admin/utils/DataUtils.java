package org.apache.dubbo.gateway.admin.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author chpengzh@foxmail.com
 */
public class DataUtils {

    public static <T> T clone(T another, Class<T> model) {
        try {
            T result = model.newInstance();
            BeanUtils.copyProperties(another, result);
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T getField(Field field, Object data) {
        try {
            //noinspection unchecked
            return (T) field.get(data);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setField(Field field, Object data, Object value) {
        try {
            field.set(data, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Stream<Field> fieldStream(Object data) {
        return StreamSupport.stream(((Iterable<Field[]>) () -> new Iterator<Field[]>() {

            Class<?> dataType = data.getClass();

            @Override
            public boolean hasNext() {
                return dataType != Object.class;
            }

            @Override
            public Field[] next() {
                try {
                    return dataType.getDeclaredFields();
                } finally {
                    dataType = dataType.getSuperclass();
                }
            }
        }).spliterator(), false).flatMap(Stream::of);
    }
}
