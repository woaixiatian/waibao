package com.bilibili.define;

public enum ReturnCode {

	/** 操作成功 */
	SUCCESS(1),
	/** 用户不存在 */
	USER_NOT_EXIST(2001),
	/** 用户插入失败 */
	USER_INSERT_FAILED(2002),
	/** 更新用户信息失败 */
	USER_UPDATE_FAILED(2003),
	/** 用户未登录 */
	USER_NOT_LOGIN(2004),
	/** 登录失败 */
	LOGIN_ERROR(2005),

	/** 更新玩家位置失败 */
	UPDATE_ADDRESS_ERROR(3001),
	/** 未知错误 */
	UNKNOWN_ERROR(-1),
	/** 参数缺少 */
	PARAM_MISSING(-10086),
	;
	
	int nCode;

	ReturnCode(int nCode) {
		this.nCode = nCode;
	}

	public int getCode() {
		return nCode;
	}
}
