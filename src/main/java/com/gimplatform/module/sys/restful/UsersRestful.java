package com.gimplatform.module.sys.restful;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.FuncInfo;
import com.gimplatform.core.entity.OrganizerInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.DataPermissionService;
import com.gimplatform.core.service.FuncInfoService;
import com.gimplatform.core.service.OrganizerInfoService;
import com.gimplatform.core.service.RoleInfoService;
import com.gimplatform.core.service.TenantsInfoService;
import com.gimplatform.core.service.UserInfoService;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.DateUtils;
import com.gimplatform.core.utils.HttpUtils;
import com.gimplatform.core.utils.OAuthUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;
import com.gimplatform.core.utils.StringUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 用户相关的Restful接口
 * 
 * @author zzd
 *
 */
@RestController
@RequestMapping(value = "/api/system/user")
public class UsersRestful {

	private static final Logger logger = LogManager.getLogger(UsersRestful.class);

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private RoleInfoService roleInfoService;

	@Autowired
	private FuncInfoService funcInfoService;

	@Autowired
	private TenantsInfoService tenantsInfoService;

	@Autowired
	private OrganizerInfoService organizerInfoService;

	@Autowired
	private DataPermissionService dataPermissionService;
	
    @Autowired 
    private DiscoveryClient discoveryClient; 

	/**
	 * 用于记录打开日志
	 * 
	 * @param request
	 */
	@ApiOperation(value = "用于记录日志，在权限菜单重配置", notes = "")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public JSONObject index(HttpServletRequest request){ return RestfulRetUtils.getRetSuccess();}

	/**
	 * 获取登录的用户信息，并写入session
	 * 
	 * @return
	 */
	@ApiOperation(value = "获取登录的用户信息，需要先进行Oauth2登录验证", notes = "")
	@RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
	public JSONObject getUserInfo() {
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		logger.info("当前登录用户:[" + userCode + "]");
		JSONObject json = new JSONObject();
		// 获取用户信息
		UserInfo userInfo = userInfoService.getByUserCode(userCode);
		// 写入session
		SessionUtils.setUserInfo(userInfo);
		// 获取当前用户的所有权限
		List<FuncInfo> userFuncList = funcInfoService.getUserFunc(userInfo);
		// 根据父权限分组写入redis
		List<FuncInfo> tmpUserFuncList = null;
		for (FuncInfo func : userFuncList) {
			if (func.getParentFuncId() == null)
				continue;
			tmpUserFuncList = new ArrayList<FuncInfo>();
			for (FuncInfo subFunc : userFuncList) {
				if (func.getParentFuncId().equals(subFunc.getParentFuncId()))
					tmpUserFuncList.add(subFunc);
			}
			SessionUtils.setUserFunc(tmpUserFuncList, func.getParentFuncId().toString());
		}
		json.put("UserInfo", userInfo);
		List<String> userRoles = roleInfoService.getRolesNameByUser(userInfo);
		json.put("UserRole", userRoles);
		json.put("TenantsInfo", tenantsInfoService.getByTenantsId(userInfo.getTenantsId()));
		OrganizerInfo organizerInfo = organizerInfoService.getByOrganizerId(userInfo.getOrganizerId());
		json.put("OrganizerInfo", organizerInfo);
		OrganizerInfo rootOrganizerInfo = organizerInfoService.getRootOrgByTenantsId(userInfo.getTenantsId());
		SessionUtils.setUserRootOrganizerId(rootOrganizerInfo.getOrganizerId());
		json.put("RootOrganizerId", rootOrganizerInfo.getOrganizerId());
		json.put("isOrganizerManager", organizerInfoService.isOrganizerManager(userInfo));
		
		//写入当前用户的上级组织类型为1的组织ID
		Long topOrganizerId = organizerInfoService.getOrganizerByIdAndType(organizerInfo.getOrganizerId(), 1);
		SessionUtils.setUserTopOrganizerId(topOrganizerId);
		json.put("TopOrganizerId", topOrganizerId);
		
		//获取数据权限
		Map<String,String> map = dataPermissionService.getTreeListByUser(userInfo);
		//写入redis
		SessionUtils.setUserDataPermission(MapUtils.getString(map, "idList"));
		json.put("DataPermissionIdList", MapUtils.getString(map, "idList"));
		json.put("DataPermissionNameList", MapUtils.getString(map, "nameList"));
		json.put("DataPermissionId", MapUtils.getString(map, "firstId"));
		json.put("DataPermissionName", MapUtils.getString(map, "firstName"));
		return json;
	}

	/**
	 * 获取用户权限菜单
	 * 
	 * @return
	 */
	@ApiOperation(value = "获取用户权限菜单，需要先进行Oauth2登录验证", notes = "")
	@RequestMapping(value = "/getUserFunc", method = RequestMethod.GET)
	public JSONObject getUserFunc() {
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if (userInfo == null)
				json = RestfulRetUtils.getErrorNoUser();
			else
				json = RestfulRetUtils.getRetSuccess(userInfoService.getUserFunc(userInfo));
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("20001", "获取用户菜单目录失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 根据folderId获取用户权限菜单
	 * 
	 * @param folderId
	 * @return
	 */
	@ApiOperation(value = "根据权限目录ID，获取用户权限菜单", notes = "根据权限目录ID，获取用户权限菜单")
	@ApiImplicitParam(name = "folderId", value = "权限目录ID", required = true, dataType = "String")
	@RequestMapping(value = "/getUserFuncByFd/{folderId}", method = RequestMethod.GET)
	public JSONObject getUserFuncByFd(@PathVariable String folderId) {
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if (userInfo == null)
				json = RestfulRetUtils.getErrorNoUser();
			else{
				Long id = StringUtils.toLong(folderId);
				json = RestfulRetUtils.getRetSuccess(userInfoService.getUserFuncByFd(userInfo, id));
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("20001", "获取用户菜单目录失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 获取用户权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getUserRights", method = RequestMethod.GET)
	public JSONObject getUserRights(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		// 权限父ID
		String funcPid = request.getParameter("funcPid");
		if (StringUtils.isBlank(funcPid)) {
			json = RestfulRetUtils.getErrorParams();
			logger.error("传递参数有误，funcPid无效");
			return json;
		}
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if (userInfo == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				// 获取当前用户的权限
				List<FuncInfo> userFuncList = SessionUtils.getUserFunc(funcPid);
				json = RestfulRetUtils.getRetSuccess(userFuncList);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("20002", "获取用户菜单目录失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 获取用户列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getUserList", method = RequestMethod.GET)
	public JSONObject getUserList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if (userInfo == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				// 创建查询条件
				Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
				UserInfo searchUser = (UserInfo) BeanUtils.mapToBean(params, UserInfo.class);
				String organizerId = request.getParameter("organizerId");
				boolean isDept = StringUtils.toBoolean(request.getParameter("isDept"), false);
				//判断是否需要获取部门所属的公司组织(即机构类型为1的)
				if(isDept){
					searchUser.setOrganizerId(SessionUtils.getUserTopOrganizerId());
				}else{
					if (StringUtils.isBlank(organizerId))
						searchUser.setOrganizerId(userInfo.getOrganizerId());
					else
						searchUser.setOrganizerId(StringUtils.toLong(organizerId));
				}
				String tenantsId = request.getParameter("tenantsId");
				if (StringUtils.isBlank(tenantsId))
					searchUser.setTenantsId(userInfo.getTenantsId());
				else
					searchUser.setTenantsId(StringUtils.toLong(tenantsId));
				//判断获取用户基础类型
				if(StringUtils.isBlank(searchUser.getUserType())) searchUser.setUserType("1");
				// 查询并返回内容
				json = userInfoService.getUserList(pageable, searchUser, params);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("20003", "获取用户列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 新增用户信息
	 * 
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public JSONObject addUser(HttpServletRequest request, @RequestBody Map<String, Object> params) {
		JSONObject json = new JSONObject();
		try {
			UserInfo loginUser = SessionUtils.getUserInfo();
			if (loginUser == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				UserInfo userInfo = (UserInfo) BeanUtils.mapToBean(params, UserInfo.class);
				String beginDate = MapUtils.getString(params, "beginDate");
				String endDate = MapUtils.getString(params, "endDate");
				json = userInfoService.addUser(userInfo, loginUser,
						(beginDate == null) ? new Date() : DateUtils.parseDate(beginDate),
						(endDate == null) ? new Date() : DateUtils.parseDate(endDate));
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("20004", "新增用户信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 编辑用户信息
	 * 
	 * @param request
	 * @param userInfo
	 * @return
	 */
	@RequestMapping(value = "/editUser", method = RequestMethod.POST)
	public JSONObject editUser(HttpServletRequest request, @RequestBody Map<String, Object> params) {
		JSONObject json = new JSONObject();
		try {
			UserInfo loginUser = SessionUtils.getUserInfo();
			if (loginUser == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				UserInfo userInfo = (UserInfo) BeanUtils.mapToBean(params, UserInfo.class);
				String beginDate = MapUtils.getString(params, "beginDate");
				String endDate = MapUtils.getString(params, "endDate");
				json = userInfoService.editUser(userInfo, loginUser,
						(beginDate == null) ? new Date() : DateUtils.parseDate(beginDate),
						(endDate == null) ? new Date() : DateUtils.parseDate(endDate));
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("20005", "编辑用户信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 删除用户信息
	 * 
	 * @param request
	 * @param idsList
	 * @return
	 */
	@RequestMapping(value = "/delUser", method = RequestMethod.POST)
	public JSONObject delUser(HttpServletRequest request, @RequestBody String idsList) {
		JSONObject json = new JSONObject();
		try {
			UserInfo loginUser = SessionUtils.getUserInfo();
			if (loginUser == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				json = userInfoService.delUser(idsList, loginUser);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("20006", "删除用户信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 获取租户所属用户
	 * 
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/getTenantsUser", method = RequestMethod.GET)
	public JSONObject getTenantsUser(HttpServletRequest request, @RequestParam Map<String, Object> params) {
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if (userInfo == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				// 创建查询条件
				Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
				UserInfo searchUser =  (UserInfo) BeanUtils.mapToBean(params, UserInfo.class);
				String isAdmin = request.getParameter("isAdmin");
				if (!"Y".equals(isAdmin) && !"N".equals(isAdmin)) isAdmin = null;
				searchUser.setIsAdmin(isAdmin);
				String tenantsId = request.getParameter("tenantsId");
				if (StringUtils.isBlank(tenantsId))
					searchUser.setTenantsId(userInfo.getTenantsId());
				else
					searchUser.setTenantsId(StringUtils.toLong(tenantsId));
				//判断获取用户基础类型
				if(StringUtils.isBlank(searchUser.getUserType())) searchUser.setUserType("1");
				// 查询并返回内容
				json = userInfoService.getUserList(pageable, searchUser, params);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("200011", "获取租户所属用户列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 设置租户管理员
	 * 
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/setTenantsAdmin", method = RequestMethod.POST)
	public JSONObject setTenantsAdmin(HttpServletRequest request, @RequestBody Map<String, Object> map) {
		JSONObject json = new JSONObject();
		try {
			UserInfo loginUser = SessionUtils.getUserInfo();
			if (loginUser == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				String userIds = MapUtils.getString(map, "userIds");
				if (StringUtils.isBlank(userIds)) {
					json = RestfulRetUtils.getErrorParams();
				} else {
					List<Long> userIdList = new ArrayList<Long>();
					Long tmpId = null;
					if (!"".equals(userIds)) {
						String[] ids = userIds.split(",");
						for (String id : ids) {
							tmpId = StringUtils.toLong(id, -1L);
							if (tmpId != null && !tmpId.equals(-1L))
								userIdList.add(tmpId);
						}
					}
					json = userInfoService.setTenantsAdmin(loginUser, userIdList);
				}
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("200013", "设置租户管理员失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 获取用户ID和名称列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getUserKeyVal", method = RequestMethod.GET)
	public JSONObject getUserKeyVal() {
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if (userInfo == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				json = userInfoService.getUserKeyVal(userInfo);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("31001", "获取用户列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 设置直属下级
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/setSubordinate",method=RequestMethod.POST)
	public JSONObject setSubordinate(HttpServletRequest request, @RequestBody Map<String, Object> map){
		JSONObject json = new JSONObject();
		try{
			UserInfo loginUser = SessionUtils.getUserInfo();
			if(loginUser == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Long userId = MapUtils.getLong(map, "userId");
				String userIdList = MapUtils.getString(map, "userIdList");
				if(userId == null || StringUtils.isBlank(userIdList)){
					json = RestfulRetUtils.getErrorParams();
				}else{
					List<Long> tmpUserList = new ArrayList<Long>();
					Long tmpId = null;
					if(!"".equals(userIdList)){
						String[] ids = userIdList.split(",");
						for(String id : ids){
							tmpId = StringUtils.toLong(id, -1L);
							if(tmpId != null && !tmpId.equals(-1L))
								tmpUserList.add(tmpId);
						}
					}
					json = userInfoService.addOrgainzerPost(loginUser, userId, tmpUserList);
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("250010","设置直属下级失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 获取直属下级
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/getSubordinate",method=RequestMethod.GET)
	public JSONObject getSubordinate(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo loginUser = SessionUtils.getUserInfo();
			if(loginUser == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Long userId = MapUtils.getLong(params, "userId");
				if(userId == null){
					json = RestfulRetUtils.getErrorParams();
				}else{
					json = userInfoService.getSubordinateList(userId);
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("250010","获取直属下级失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 用户修改密码
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
	public JSONObject updatePassword(HttpServletRequest request, @RequestBody Map<String, Object> map) {
		JSONObject json = new JSONObject();
		try {
			UserInfo loginUser = SessionUtils.getUserInfo();
			if (loginUser == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				String oldPassword = MapUtils.getString(map, "oldPassword");
				String newPassword = MapUtils.getString(map, "newPassword");
				if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
					json = RestfulRetUtils.getErrorParams();
				} else {
					json = userInfoService.updatePassword(loginUser.getUserId(), newPassword, oldPassword);
				}
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("200013", "设置租户管理员失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 获取待办任务数量
	 * @param request
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "/getTaskCount", method = RequestMethod.GET)
    public JSONObject getTaskCount(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				String access_token = request.getParameter("access_token");
				List<ServiceInstance> serviceList = discoveryClient.getInstances("gimp-api-gateway");
				String taskCount = "";
				if(serviceList != null && serviceList.size() > 0){
					String gatewayUrl = serviceList.get(0).getUri().toString();
					//请求工作流模块的待办任务数
					taskCount = HttpUtils.get(gatewayUrl + "/api/workflow/getTaskCount?access_token=" + access_token);
				}
				if(StringUtils.isBlank(taskCount)) 
					json = RestfulRetUtils.getRetSuccess("0");
				else
					json = JSONObject.parseObject(taskCount);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("51004","获取待办任务数量失败");
			logger.error(e.getMessage(), e);
		}
		return json;
    }
	

	@RequestMapping(value = "/lock", method = RequestMethod.GET)
	public JSONObject lock(HttpServletRequest request){ return RestfulRetUtils.getRetSuccess();}


	/**
	 * 获取上锁账号列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getLockAccount", method = RequestMethod.GET)
	public JSONObject getLockAccount(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if (userInfo == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				// 创建查询条件
				Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
				// 查询并返回内容
				json = userInfoService.getLockAccount(pageable, userInfo);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("20003", "获取上锁账号列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 解锁账号
	 * 
	 * @param request
	 * @param idsList
	 * @return
	 */
	@RequestMapping(value = "/unlockAccount", method = RequestMethod.POST)
	public JSONObject unlockAccount(HttpServletRequest request, @RequestBody String idsList) {
		JSONObject json = new JSONObject();
		try {
			UserInfo loginUser = SessionUtils.getUserInfo();
			if (loginUser == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				json = userInfoService.unlockAccount(idsList, loginUser);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("20006", "解锁账号信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
}
