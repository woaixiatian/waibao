package com.bilibili.net.http.converter;

import com.bilibili.bean.user.ObjectBean;
import com.bilibili.data.manager.UserManager;
import com.bilibili.net.http.dto.response.Result;
import com.bilibili.util.JsonUtils;
import com.bilibili.util.SpringUtil;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

public class ResultConverter extends AbstractHttpMessageConverter<Result> {

	private final StringHttpMessageConverter stringHttpMessageConverter;

	/**
	 * A constructor accepting a {@code ConversionService} dto use dto convert the
	 * (String) message body dto/from the target class type. This constructor
	 * uses {@link StringHttpMessageConverter#DEFAULT_CHARSET} as the default
	 * charset.
	 * 
	 *            the conversion service
	 */
	public ResultConverter() {
		this(StringHttpMessageConverter.DEFAULT_CHARSET);
	}

	/**
	 * A constructor accepting a {@code ConversionService} as well as a default
	 * charset.
	 *            the conversion service
	 * @param defaultCharset
	 *            the default charset
	 */
	public ResultConverter(Charset defaultCharset) {
		super(defaultCharset, MediaType.TEXT_PLAIN);

		this.stringHttpMessageConverter = new StringHttpMessageConverter(defaultCharset);
	}
	/**
	 * Indicates whether the {@code Accept-Charset} should be written dto any
	 * outgoing request.
	 * <p>
	 * Default is {@code true}.
	 */
	public void setWriteAcceptCharset(boolean writeAcceptCharset) {
		this.stringHttpMessageConverter.setWriteAcceptCharset(writeAcceptCharset);
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return true;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return true;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Result readInternal(Class<? extends Result> clazz, HttpInputMessage inputMessage) throws IOException {

		return null;
	}
	//自动添加插入或者修改的数据 返回给前端
	@Override
	protected void writeInternal(Result obj, HttpOutputMessage outputMessage) throws IOException {
		UserManager userManager = SpringUtil.getBean(UserManager.class);
		Map<String, ArrayList<ObjectBean>> mapSyn = userManager.getMapSyn();
		if(mapSyn != null){
			obj.addData("SyncData", mapSyn);
		}
		String json = JsonUtils.bean2Json(obj);
		this.stringHttpMessageConverter.writeInternal(json, outputMessage);
	}

}
