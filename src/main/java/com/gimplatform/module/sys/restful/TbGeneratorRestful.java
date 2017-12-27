package com.gimplatform.module.sys.restful;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.TbGenerator;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.TbGeneratorService;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;

/**
 * 自增表相关的Restful接口
 * @author zzd
 *
 */
@RestController
@RequestMapping(value = "/api/system/tbgenerator")
public class TbGeneratorRestful {

    private static final Logger logger = LogManager.getLogger(TbGeneratorRestful.class);
    
    @Autowired
    private TbGeneratorService tbGeneratorService;

	/**
	 * 用于记录打开日志
	 * @param request
	 */
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public JSONObject index(HttpServletRequest request){ return RestfulRetUtils.getRetSuccess();}
	
	/**
	 * 获取列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getList",method=RequestMethod.GET)
	public JSONObject getList(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request)); 
				TbGenerator tbGenerator = new TbGenerator();
				tbGenerator.setGenName(MapUtils.getString(params, "searchName"));
				json = tbGeneratorService.getList(pageable, tbGenerator);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("21001","获取自增列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	

	/**
	 * 更新自增ID
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/fix",method=RequestMethod.POST)
	public JSONObject fix(HttpServletRequest request){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = tbGeneratorService.fixGenerator();
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("21001","更新自增ID失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
}
