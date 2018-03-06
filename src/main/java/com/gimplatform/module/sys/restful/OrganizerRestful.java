package com.gimplatform.module.sys.restful;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.OrganizerInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.OrganizerInfoService;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 组织相关的Restful接口
 * @author zzd
 */
@RestController
@RequestMapping(value = "/api/system/organizer")
public class OrganizerRestful {

    private static final Logger logger = LogManager.getLogger(OrganizerRestful.class);

    @Autowired
    private OrganizerInfoService organizerInfoService;

    /**
     * 用于记录打开日志
     * @param request
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public JSONObject index(HttpServletRequest request) {
        return RestfulRetUtils.getRetSuccess();
    }

    /**
     * 获取组织列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/getOrganizerTree", method = RequestMethod.GET)
    public JSONObject getOrganizerTree(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Long tenantsId = StringUtils.toLong(request.getParameter("tenantsId"));
                JSONArray array = new JSONArray();
                if (tenantsId == null || tenantsId.equals(-1L)) {
                    // array = organizerInfoService.getOrganizerTreeById(null, userInfo, organizerId, filterDept, filterPost);
                    array = organizerInfoService.getOrganizerTreeByTenantsId(userInfo, params);
                } else {
                    array = organizerInfoService.getOrganizerTreeByTenantsId(userInfo, params);
                }
                json = RestfulRetUtils.getRetSuccess(array);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("23001", "获取组织菜单树失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 新增组织信息
     * @param request
     * @param organizerInfo
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JSONObject add(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                OrganizerInfo organizerInfo = (OrganizerInfo) BeanUtils.mapToBean(params, OrganizerInfo.class);
                json = organizerInfoService.addOrganizer(organizerInfo, userInfo, params);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("23002", "新增组织信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 编辑组织信息
     * @param request
     * @param organizerInfo
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public JSONObject edit(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                OrganizerInfo organizerInfo = (OrganizerInfo) BeanUtils.mapToBean(params, OrganizerInfo.class);
                json = organizerInfoService.editOrganizer(organizerInfo, userInfo, params);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("23003", "编辑组织信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 删除组织信息
     * @param request
     * @param idsList
     * @return
     */
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public JSONObject del(HttpServletRequest request, @RequestBody String idsList) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = organizerInfoService.delOrganizer(idsList, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("23004", "删除组织信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 获取组织父ID
     * @param request
     * @return
     */
    @RequestMapping(value = "/getParentId", method = RequestMethod.GET)
    public JSONObject getParentId(HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Long organizerId = StringUtils.toLong(request.getParameter("organizerId"));
                json = RestfulRetUtils.getRetSuccess(organizerInfoService.getOrganizerParentId(organizerId) + "");
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("23001", "获取组织菜单树失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 获取默认角色和数据权限
     * @param request
     * @return
     */
    @RequestMapping(value = "/getRoleAndData", method = RequestMethod.GET)
    public JSONObject getRoleAndData(HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Long organizerId = StringUtils.toLong(request.getParameter("organizerId"));
                json = organizerInfoService.getRoleAndData(organizerId);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("23001", "获取默认角色和数据权限失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 获取额外信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/getExtraInfo", method = RequestMethod.GET)
    public JSONObject getExtraInfo(HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Long organizerId = StringUtils.toLong(request.getParameter("organizerId"));
                Long parentOrgId = StringUtils.toLong(request.getParameter("parentOrgId"));
                json = organizerInfoService.getExtraInfo(organizerId, parentOrgId);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("23001", "获取获取额外信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 编辑组织信息
     * @param request
     * @param organizerInfo
     * @return
     */
    @RequestMapping(value = "/submitOrganizerCache", method = RequestMethod.POST)
    public JSONObject submitOrganizerCache(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                OrganizerInfo organizerInfo = (OrganizerInfo) BeanUtils.mapToBean(params, OrganizerInfo.class);
                json = organizerInfoService.submitOrganizerCache(organizerInfo, userInfo, params);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("23003", "编辑组织信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }
}
