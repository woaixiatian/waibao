package com.bilibili.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public interface Bean extends Serializable{
	@JsonIgnore
	default String getBeanName(){
		return this.getClass().getSimpleName();
	}
}