package com.gimplatform.module.sys.restful;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.TenantsInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.FuncInfoService;
import com.gimplatform.core.service.TenantsInfoService;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 租户相关的Restful接口
 * @author zzd
 *
 */
@RestController
@RequestMapping(value = "/api/system/tenants")
public class TenantsRestful {

    private static final Logger logger = LogManager.getLogger(TenantsRestful.class);

	@Autowired
	private TenantsInfoService tenantsInfoService;

	@Autowired
	private FuncInfoService funcInfoService;

	/**
	 * 用于记录打开日志
	 * @param request
	 */
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public JSONObject index(HttpServletRequest request){ return RestfulRetUtils.getRetSuccess();}
	
	/**
	 * 获取租户列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getAllTenantsList",method=RequestMethod.GET)
	public JSONObject getAllTenantsList(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = tenantsInfoService.getAllTenantsList();
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("21001","获取租户列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 获取租户列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getTenantsList",method=RequestMethod.GET)
	public JSONObject getTenantsList(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));  
				TenantsInfo tenantsInfo = new TenantsInfo();
				tenantsInfo.setTenantsName(MapUtils.getString(params, "searchName"));
				tenantsInfo.setStatus(MapUtils.getLong(params, "searchStatus"));
				Page<TenantsInfo> tenantsList = tenantsInfoService.getTenantsList(pageable, tenantsInfo);
				json = RestfulRetUtils.getRetSuccessWithPage(tenantsList.getContent(), tenantsList.getTotalElements());
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("21001","获取租户列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 新增租户信息
	 * @param request
	 * @param tenantsInfo
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public JSONObject add(HttpServletRequest request, @RequestBody TenantsInfo tenantsInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = tenantsInfoService.addTenants(tenantsInfo, userInfo);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("21002","新增租户信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 编辑租户信息
	 * @param request
	 * @param tenantsInfo
	 * @return
	 */
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	public JSONObject edit(HttpServletRequest request, @RequestBody TenantsInfo tenantsInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = tenantsInfoService.editTenants(tenantsInfo, userInfo);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("21003","编辑租户信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 删除租户信息
	 * @param request
	 * @param idsList
	 * @return
	 */
	@RequestMapping(value="/del",method=RequestMethod.POST)
	public JSONObject del(HttpServletRequest request,@RequestBody String idsList){
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = tenantsInfoService.delTenants(idsList, userInfo);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("21004","删除租户信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 获取租户权限树列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getTenFuncTree",method=RequestMethod.GET)
	public JSONObject getTenFuncTree(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Long tenantsId = MapUtils.getLong(params, "tenantsId");
				if(tenantsId == null) tenantsId = userInfo.getTenantsId();
				json = funcInfoService.getFuncTreeByTenantsId(tenantsId);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("21008","获取租户权限树列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 通过租户ID获取权限树
	 * @param request
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/getFuncIdByTenantsId",method=RequestMethod.GET)
	public JSONObject getFuncIdByTenantsId(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Long tenantsId = MapUtils.getLong(params, "tenantsId");
				json = funcInfoService.getFuncIdByTenantsId(tenantsId);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("21009","获取租户权限树列表失败");
			logger.error(e.getMessage(), e);
		}
		logger.info(json.toString());
		return json;
	}
	
	/**
	 * 保存租户权限
	 * @param request
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/saveFunc",method=RequestMethod.POST)
	public JSONObject saveFunc(HttpServletRequest request,@RequestBody Map<String, Object> params){
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				long startTime = System.currentTimeMillis();
				
				Long tenantsId = MapUtils.getLong(params, "tenantsId");
				String funcIds = MapUtils.getString(params, "funcIds");
				List<Long> funcIdList = new ArrayList<Long>();
				Long tmpId = null;
				if(!StringUtils.isBlank(funcIds)){
					String[] ids = funcIds.split(",");
					for(String id : ids){
						tmpId = StringUtils.toLong(id, -1L);
						if(tmpId != null && !tmpId.equals(-1L))
							funcIdList.add(tmpId);
					}
				}
				json = tenantsInfoService.saveTenantsFunc(userInfo, tenantsId, funcIdList);
				
				long endTime = System.currentTimeMillis(); //获取结束时间
				logger.info("租户保存权限-运行时间： "+(endTime - startTime) + "ms");
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("21010","保存租户权限失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
}
