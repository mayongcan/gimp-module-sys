package com.gimplatform.module.sys.restful;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
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
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.LogInfoService;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;

/**
 * 日志相关的Restful接口
 * @author zzd
 */
@RestController
@RequestMapping(value = "/api/system/log")
public class LogRestful {

    private static final Logger logger = LogManager.getLogger(LogRestful.class);

    @Autowired
    private LogInfoService logInfoService;

    /**
     * 用于记录打开日志
     * @param request
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public JSONObject index(HttpServletRequest request) {
        return RestfulRetUtils.getRetSuccess();
    }

    /**
     * 获取日志列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLogList", method = RequestMethod.GET)
    public JSONObject getLogList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
                String searchTitle = MapUtils.getString(params, "searchTitle");
                String searchBeginTime = MapUtils.getString(params, "searchBeginTime");
                String searchEndTime = MapUtils.getString(params, "searchEndTime");
                Long tenantsId = MapUtils.getLong(params, "tenantsId");
                if (tenantsId == null)
                    tenantsId = userInfo.getTenantsId();
                Long organizerId = MapUtils.getLong(params, "organizerId");
                if (organizerId == null)
                    organizerId = userInfo.getOrganizerId();
                json = logInfoService.getLogList(pageable, userInfo, tenantsId, organizerId, searchTitle, searchBeginTime, searchEndTime);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("27001", "获取日志列表失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }
}
