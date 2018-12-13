package com.bilibili.net.http.converter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * describe :
 * author : xusong
 * createTime : 2018/6/22
 */
public class MyStringConverter extends AbstractHttpMessageConverter<String> {
    @Override
    protected boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    protected String readInternal(Class<? extends String> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(String s, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

    }
}
