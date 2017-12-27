package com.gimplatform.module.sys.restful;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.utils.RestfulRetUtils;

/**
 * 用于测试，不用经过oauth验证
 * @author zzd
 *
 */
@RestController
@RequestMapping(value = "/api/system/ignore")
public class TestRestful {

	@RequestMapping(value = "/testSchedule", method = RequestMethod.POST)
	public JSONObject testSchedule(HttpServletRequest request) {
		int val = 1000000000;
		while(val > 0){
			val--;
		}
		return RestfulRetUtils.getRetSuccess();
	}
}
