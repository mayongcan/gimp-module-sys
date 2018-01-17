package com.gimplatform.module.sys.restful;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.service.DistrictService;
import com.gimplatform.core.utils.RestfulRetUtils;

/**
 * 区域相关的Restful接口
 * @author zzd
 *
 */
@RestController
@RequestMapping(value = "/api/system/district")
public class DistrictRestful {

	protected static final Logger logger = LogManager.getLogger(DistrictRestful.class);
	
	@Autowired
	private DistrictService districtService;

	/**
	 * 获取父ID获取区域列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getListByParentId",method=RequestMethod.GET)
	public JSONObject getListByParentId(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			//查询并返回内容
			Long parentId = MapUtils.getLong(params, "parentId");
			if(parentId == null) RestfulRetUtils.getErrorParams();
			else json = RestfulRetUtils.getRetSuccess(districtService.getDistrictListByParentId(parentId));
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("31001","获取区域列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

}
