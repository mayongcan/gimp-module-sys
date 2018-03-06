package com.gimplatform.module.sys.restful;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.DataPermission;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.DataPermissionService;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 数据权限相关的Restful接口
 * @author zzd
 */
@RestController
@RequestMapping(value = "/api/system/data")
public class DataPermissionRestful {

    private static final Logger logger = LogManager.getLogger(DataPermissionRestful.class);

    @Autowired
    private DataPermissionService dataPermissionService;

    /**
     * 用于记录打开日志
     * @param request
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public JSONObject index(HttpServletRequest request) {
        return RestfulRetUtils.getRetSuccess();
    }

    /**
     * 获取数据权限树
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/getDataPermissionTreeList", method = RequestMethod.GET)
    public JSONObject getDataPermissionTreeList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Long tenantsId = MapUtils.getLong(params, "tenantsId", null);
                Long organizerId = MapUtils.getLong(params, "organizerId", null);
                if (tenantsId == null)
                    tenantsId = userInfo.getTenantsId();
                // if(organizerId == null) organizerId = userInfo.getOrganizerId();
                json = dataPermissionService.getTreeList(tenantsId, organizerId);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51001", "获取树列表失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 获取数据权限顶层树
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/getDataPermissionRootTreeList", method = RequestMethod.GET)
    public JSONObject getDataPermissionRootTreeList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Long tenantsId = MapUtils.getLong(params, "tenantsId", null);
                Long organizerId = MapUtils.getLong(params, "organizerId", null);
                if (tenantsId == null)
                    tenantsId = userInfo.getTenantsId();
                // if(organizerId == null) organizerId = userInfo.getOrganizerId();
                json = dataPermissionService.getRootTreeList(tenantsId, organizerId);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51001", "获取树列表失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 获取具有对应数据权限的用列表
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/getUserListByDataPermission", method = RequestMethod.GET)
    public JSONObject getUserListByDataPermission(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
                params.put("userType", "1");
                json = dataPermissionService.getUserListByDataPermission(pageable, params);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("25001", "获取用户数据权限列表失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 新增数据权限信息
     * @param request
     * @param dataPermission
     * @return
     */
    @RequestMapping(value = "/addDataPermission", method = RequestMethod.POST)
    public JSONObject addDataPermission(HttpServletRequest request, @RequestBody DataPermission dataPermission) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = dataPermissionService.addDataPermission(dataPermission, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("25003", "新增数据权限信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 编辑数据权限信息
     * @param request
     * @param dataPermission
     * @return
     */
    @RequestMapping(value = "/editDataPermission", method = RequestMethod.POST)
    public JSONObject editDataPermission(HttpServletRequest request, @RequestBody DataPermission dataPermission) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = dataPermissionService.editDataPermission(dataPermission, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("25004", "编辑数据权限信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 删除数据权限信息
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/delDataPermission", method = RequestMethod.POST)
    public JSONObject delDataPermission(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Long permissionId = MapUtils.getLong(params, "idsList");
                if (permissionId == null)
                    json = RestfulRetUtils.getErrorParams();
                else
                    json = dataPermissionService.delDataPermission(permissionId, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("25005", "删除数据权限信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 保存数据权限用户
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/addUserDataPermission", method = RequestMethod.POST)
    public JSONObject addUserDataPermission(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        JSONObject json = new JSONObject();
        try {
            UserInfo loginUser = SessionUtils.getUserInfo();
            if (loginUser == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Long permissionId = MapUtils.getLong(map, "permissionId");
                String userIds = MapUtils.getString(map, "userIds");
                if (permissionId == null || StringUtils.isBlank(userIds)) {
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
                    json = dataPermissionService.addUserDataPermission(loginUser, permissionId, userIdList);
                }
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("250010", "新增数据权限用户失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 删除数据权限用户
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/delUserDataPermission", method = RequestMethod.POST)
    public JSONObject delUserDataPermission(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        JSONObject json = new JSONObject();
        try {
            UserInfo loginUser = SessionUtils.getUserInfo();
            if (loginUser == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Long permissionId = MapUtils.getLong(map, "permissionId");
                String userIds = MapUtils.getString(map, "userIds");
                if (permissionId == null || StringUtils.isBlank(userIds)) {
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
                    json = dataPermissionService.delUserDataPermission(loginUser, permissionId, userIdList);
                }
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("250011", "删除数据权限用户失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }
}
