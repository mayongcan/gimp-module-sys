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
import com.gimplatform.core.entity.OrganizerInfo;
import com.gimplatform.core.entity.RoleInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.OrganizerInfoService;
import com.gimplatform.core.service.RoleInfoService;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 角色相关的Restful接口
 * @author zzd
 *
 */
@RestController
@RequestMapping(value = "/api/system/role")
public class RoleRestful {

    private static final Logger logger = LogManager.getLogger(RoleRestful.class);
    
    @Autowired
    private RoleInfoService roleInfoService;

	@Autowired
	private OrganizerInfoService organizerInfoService;

	/**
	 * 用于记录打开日志
	 * @param request
	 */
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public JSONObject index(HttpServletRequest request){ return RestfulRetUtils.getRetSuccess();}
	
	/**
	 * 获取角色列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getRoleList",method=RequestMethod.GET)
	public JSONObject getRoleList(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));  
				RoleInfo roleInfo = new RoleInfo();
				roleInfo.setTenantsId(MapUtils.getLong(params, "tenantsId"));
				roleInfo.setOrganizerId(MapUtils.getLong(params, "organizerId"));
				if(roleInfo.getTenantsId() == null) roleInfo.setTenantsId(userInfo.getTenantsId());
				if(roleInfo.getOrganizerId() == null) roleInfo.setOrganizerId(userInfo.getOrganizerId());
				Page<RoleInfo> roleInfoList = roleInfoService.getRoleList(pageable, roleInfo);
				json = RestfulRetUtils.getRetSuccessWithPage(roleInfoList.getContent(), roleInfoList.getTotalElements());
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("25001","获取角色列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 获取角色用户列表
	 * @param request
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/getRoleUserList",method=RequestMethod.GET)
	public JSONObject getRoleUserList(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
				params.put("userType", "1");
				json = roleInfoService.getRoleUserList(pageable, params);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("25001","获取角色列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 根据角色ID获取角色所属权限
	 * @param request
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/getFuncTreeByRoleId",method=RequestMethod.GET)
	public JSONObject getFuncTreeByRoleId(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = roleInfoService.getFuncTreeByRoleId(userInfo, MapUtils.getLong(params, "roleId"));
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("25002","获取角色所属权限列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 新增角色信息
	 * @param request
	 * @param roleInfo
	 * @return
	 */
	@RequestMapping(value="/addRole",method=RequestMethod.POST)
	public JSONObject addRole(HttpServletRequest request, @RequestBody RoleInfo roleInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = roleInfoService.addRole(roleInfo, userInfo);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("25003","新增角色信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 编辑角色信息
	 * @param request
	 * @param roleInfo
	 * @return
	 */
	@RequestMapping(value="/editRole",method=RequestMethod.POST)
	public JSONObject editRole(HttpServletRequest request, @RequestBody RoleInfo roleInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = roleInfoService.editRole(roleInfo, userInfo);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("25004","编辑角色信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 删除角色信息
	 * @param request
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/delRole",method=RequestMethod.POST)
	public JSONObject delRole(HttpServletRequest request,@RequestBody Map<String, Object> params){
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Long roleId = MapUtils.getLong(params, "idsList");
				if(roleId == null) json = RestfulRetUtils.getErrorParams();
				else json = roleInfoService.delRole(roleId, userInfo);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("25005","删除角色信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 保存角色权限
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
				
				Long roleId = MapUtils.getLong(params, "roleId");
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
				json = roleInfoService.saveRoleFunc(userInfo, roleId, funcIdList);
				
				long endTime = System.currentTimeMillis(); //获取结束时间
				logger.info("角色保存权限-运行时间： "+(endTime - startTime) + "ms");
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("25009","保存角色权限失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 保存角色用户
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/addUserRole",method=RequestMethod.POST)
	public JSONObject addUserRole(HttpServletRequest request, @RequestBody Map<String, Object> map){
		JSONObject json = new JSONObject();
		try{
			UserInfo loginUser = SessionUtils.getUserInfo();
			if(loginUser == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Long roleId = MapUtils.getLong(map, "roleId");
				String userIds = MapUtils.getString(map, "userIds");
				if(roleId == null || StringUtils.isBlank(userIds)){
					json = RestfulRetUtils.getErrorParams();
				}else{
					List<Long> userIdList = new ArrayList<Long>();
					Long tmpId = null;
					if(!"".equals(userIds)){
						String[] ids = userIds.split(",");
						for(String id : ids){
							tmpId = StringUtils.toLong(id, -1L);
							if(tmpId != null && !tmpId.equals(-1L))
								userIdList.add(tmpId);
						}
					}
					json = roleInfoService.addUserRole(loginUser, roleId, userIdList);
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("250010","新增角色用户失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 删除角色用户
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/delUserRole",method=RequestMethod.POST)
	public JSONObject delUserRole(HttpServletRequest request, @RequestBody Map<String, Object> map){
		JSONObject json = new JSONObject();
		try{
			UserInfo loginUser = SessionUtils.getUserInfo();
			if(loginUser == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Long roleId = MapUtils.getLong(map, "roleId");
				String userIds = MapUtils.getString(map, "userIds");
				if(roleId == null || StringUtils.isBlank(userIds)){
					json = RestfulRetUtils.getErrorParams();
				}else{
					List<Long> userIdList = new ArrayList<Long>();
					Long tmpId = null;
					if(!"".equals(userIds)){
						String[] ids = userIds.split(",");
						for(String id : ids){
							tmpId = StringUtils.toLong(id, -1L);
							if(tmpId != null && !tmpId.equals(-1L))
								userIdList.add(tmpId);
						}
					}
					json = roleInfoService.delUserRole(loginUser, roleId, userIdList);
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("250011","删除角色用户失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 获取用户ID和名称列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getRoleKeyVal", method = RequestMethod.GET)
	public JSONObject getRoleKeyVal(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if (userInfo == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				Long organizerId = MapUtils.getLong(params, "organizerId", null);
				if(organizerId == null){
					OrganizerInfo organizerInfo = organizerInfoService.getByOrganizerId(userInfo.getOrganizerId());
					String idPath = organizerInfo.getIdPath();
					if(StringUtils.isBlank(idPath)) return RestfulRetUtils.getErrorParams();
					else{
						String[] parentId = idPath.split(",");
						organizerId = StringUtils.toLong(parentId[0]);
					}
				}
				json = roleInfoService.getRolesKeyValByOrganizerId(organizerId);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("31001", "获取列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
}
