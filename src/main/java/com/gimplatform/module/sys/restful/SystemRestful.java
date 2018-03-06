package com.gimplatform.module.sys.restful;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.utils.OAuthUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;

/**
 * 系统相关的Restful接口
 * @author zzd
 */
@RestController
@RequestMapping(value = "/api/system")
public class SystemRestful {

    private static final Logger logger = LogManager.getLogger(SystemRestful.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 本地服务实例的信息
     * @return
     */
    @GetMapping("/serviceInstance")
    public ServiceInstance showInfo() {
        ServiceInstance localServiceInstance = this.discoveryClient.getLocalServiceInstance();
        return localServiceInstance;
    }

    /**
     * 处理登出
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public JSONObject logout() {
        String userCode = OAuthUtils.getCurrentLoginUserCode();
        logger.info("退出当前已登录的用户:[" + userCode + "]");
        SessionUtils.removeAll();
        return RestfulRetUtils.getRetSuccess();
    }

    /**
     * 页面重定向到API文档首页
     * @return
     */
    @RequestMapping(value = "/apiDoc", method = RequestMethod.GET)
    public RedirectView apiDoc(HttpServletRequest request) {
        return new RedirectView("/swagger-ui.html?access_token=" + request.getParameter("access_token"));
    }
}
