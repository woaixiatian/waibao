package com.bilibili.net.http.converter;
import com.bilibili.bean.user.ObjectBean;
import com.bilibili.data.manager.UserManager;
import com.bilibili.net.http.dto.response.Result;
import com.bilibili.util.SpringUtil;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * describe :
 * author : xusong
 * createTime : 2018/6/22
 */
public class MyResultConverter extends AbstractHttpMessageConverter<Result> {
    @Override
    protected boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    protected Result readInternal(Class<? extends Result> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }
    @Override
    protected void writeInternal(Result result, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        UserManager userManager = SpringUtil.getBean(UserManager.class);
        Map<String, ArrayList<ObjectBean>> mapSyn = userManager.getMapSyn();
        if(mapSyn != null){
            result.addData("SyncData", mapSyn);
        }
    }

}
