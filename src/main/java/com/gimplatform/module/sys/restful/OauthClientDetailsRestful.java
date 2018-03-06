package com.gimplatform.module.sys.restful;

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
import com.gimplatform.core.entity.OauthClientDetails;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.OauthClientDetailsService;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;

/**
 * 授权相关的Restful接口
 * @author zzd
 */
@RestController
@RequestMapping(value = "/api/system/oauth")
public class OauthClientDetailsRestful {

    private static final Logger logger = LogManager.getLogger(OauthClientDetailsRestful.class);

    @Autowired
    private OauthClientDetailsService oauthClientDetailsService;

    /**
     * 用于记录打开日志
     * @param request
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public JSONObject index(HttpServletRequest request) {
        return RestfulRetUtils.getRetSuccess();
    }

    /**
     * 获取授权列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/getOauthList", method = RequestMethod.GET)
    public JSONObject getOauthList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
                OauthClientDetails oauthClientDetails = new OauthClientDetails();
                oauthClientDetails.setClientId(MapUtils.getString(params, "searchName"));
                Page<OauthClientDetails> oauthList = oauthClientDetailsService.getOauthClientList(pageable, oauthClientDetails);
                json = RestfulRetUtils.getRetSuccessWithPage(oauthList.getContent(), oauthList.getTotalElements());
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("26001", "获取授权列表失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 新增授权信息
     * @param request
     * @param oauthClientDetails
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JSONObject add(HttpServletRequest request, @RequestBody OauthClientDetails oauthClientDetails) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = oauthClientDetailsService.addOauthClient(oauthClientDetails, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("26002", "新增授权信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 编辑授权信息
     * @param request
     * @param params
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
                OauthClientDetails oauthClientDetails = new OauthClientDetails();
                oauthClientDetails.setClientId(MapUtils.getString(params, "clientId"));
                oauthClientDetails.setResourceIds(MapUtils.getString(params, "resourceIds"));
                oauthClientDetails.setClientSecret(MapUtils.getString(params, "clientSecret"));
                oauthClientDetails.setScope(MapUtils.getString(params, "scope"));
                oauthClientDetails.setAuthorizedGrantTypes(MapUtils.getString(params, "authorizedGrantTypes"));
                oauthClientDetails.setAccessTokenValidity(MapUtils.getLong(params, "accessTokenValidity"));
                oauthClientDetails.setRefreshTokenValidity(MapUtils.getLong(params, "refreshTokenValidity"));
                json = oauthClientDetailsService.editOauthClient(oauthClientDetails, userInfo, MapUtils.getString(params, "oldClientId"));
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("26003", "编辑授权信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 删除授权信息
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
                json = oauthClientDetailsService.delOauthClient(idsList, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("26004", "删除授权信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }
}
