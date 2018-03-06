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
import com.gimplatform.core.entity.ClientVersion;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.ClientVersionService;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;

/**
 * 客户端规则
 * @author zzd
 */
@RestController
@RequestMapping(value = "/api/system/client")
public class ClientRestful {

    protected static final Logger logger = LogManager.getLogger(ClientRestful.class);

    @Autowired
    private ClientVersionService clientVersionService;

    /**
     * 用于记录打开日志
     * @param request
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public JSONObject index(HttpServletRequest request) {
        return RestfulRetUtils.getRetSuccess();
    }

    /**
     * 获取客户端版本列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/getAppVersionList", method = RequestMethod.GET)
    public JSONObject getAppVersionList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
                ClientVersion ClientVersion = new ClientVersion();
                ClientVersion.setName(MapUtils.getString(params, "searchName"));
                Page<ClientVersion> list = clientVersionService.getList(pageable, ClientVersion);
                json = RestfulRetUtils.getRetSuccessWithPage(list.getContent(), list.getTotalElements());
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51001", "获取客户端版本列表失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 新增客户端版本信息
     * @param request
     * @param ClientVersion
     * @return
     */
    @RequestMapping(value = "/addVersion", method = RequestMethod.POST)
    public JSONObject addVersion(HttpServletRequest request, @RequestBody ClientVersion ClientVersion) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = clientVersionService.add(ClientVersion, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51002", "新增客户端版本信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 编辑客户端版本信息
     * @param request
     * @param ClientVersion
     * @return
     */
    @RequestMapping(value = "/editVersion", method = RequestMethod.POST)
    public JSONObject editVersion(HttpServletRequest request, @RequestBody ClientVersion ClientVersion) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = clientVersionService.edit(ClientVersion, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51003", "编辑客户端版本信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 删除客户端版本信息
     * @param request
     * @param idsList
     * @return
     */
    @RequestMapping(value = "/delVersion", method = RequestMethod.POST)
    public JSONObject delVersion(HttpServletRequest request, @RequestBody String idsList) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = clientVersionService.del(idsList, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51004", "删除客户端版本信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

}
