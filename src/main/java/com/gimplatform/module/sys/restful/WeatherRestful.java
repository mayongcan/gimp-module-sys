package com.gimplatform.module.sys.restful;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.WeatherInfoService;
import com.gimplatform.core.utils.DateUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;

/**
 * 字典相关的Restful接口
 * @author zzd
 *
 */
@RestController
@RequestMapping(value = "/api/system/weather")
public class WeatherRestful {

    private static final Logger logger = LogManager.getLogger(WeatherRestful.class);
    
    @Autowired
    private WeatherInfoService weatherInfoService;

	/**
	 * 用于记录打开日志
	 * @param request
	 */
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public JSONObject index(HttpServletRequest request){ return RestfulRetUtils.getRetSuccess();}

	/**
	 * 获取天气列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/getWeatherList",method=RequestMethod.GET)
	public JSONObject getWeatherList(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				String city = MapUtils.getString(params, "city");
				String beginTime = MapUtils.getString(params, "beginDate");
				if(StringUtils.isBlank(beginTime)) beginTime = DateUtils.getDate();
				String endTime = DateUtils.formatDate(DateUtils.addDays(DateUtils.parseDate(beginTime), 3), "yyyy-MM-dd");
				json = RestfulRetUtils.getRetSuccess(weatherInfoService.getWeatherList(city, beginTime, endTime, 3));
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("22001","获取天气列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

}
