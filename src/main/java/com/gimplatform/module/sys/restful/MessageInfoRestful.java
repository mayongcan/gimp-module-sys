/*
 * Copyright(c) 2018 gimplatform(通用信息管理平台) All rights reserved.
 */
package com.gimplatform.module.sys.restful;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;

import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.gimplatform.core.entity.MessageInfo;
import com.gimplatform.core.entity.MessageUser;
import com.gimplatform.core.service.MessageInfoService;

/**
 * Restful接口
 * @version 1.0
 * @author
 */
@RestController
@RequestMapping("/api/system/message")
public class MessageInfoRestful {

    protected static final Logger logger = LogManager.getLogger(MessageInfoRestful.class);

    @Autowired
    private MessageInfoService messageInfoService;

    /**
     * 用于记录打开日志
     * @param request
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public JSONObject index(HttpServletRequest request) {
        return RestfulRetUtils.getRetSuccess();
    }

    /**
     * 获取列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public JSONObject getList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
                MessageInfo messageInfo = (MessageInfo) BeanUtils.mapToBean(params, MessageInfo.class);
                json = messageInfoService.getList(pageable, messageInfo, params);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51001", "获取列表失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 新增信息
     * @param request
     * @param params
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
                MessageInfo messageInfo = (MessageInfo) BeanUtils.mapToBean(params, MessageInfo.class);
                String userIdList = MapUtils.getString(params, "userIdList");
                json = messageInfoService.add(messageInfo, userInfo, userIdList, "1".equals(messageInfo.getMsgType()), false);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51002", "新增信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 编辑信息
     * @param request
     * @param messageInfo
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
                MessageInfo messageInfo =  (MessageInfo) BeanUtils.mapToBean(params, MessageInfo.class);
                String userIdList = MapUtils.getString(params, "userIdList");
                json = messageInfoService.edit(messageInfo, userInfo, userIdList, "1".equals(messageInfo.getMsgType()));
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51003", "编辑信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 删除信息
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
                json = messageInfoService.del(idsList, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51004", "删除信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 发送消息
     * @param request
     * @param idsList
     * @return
     */
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public JSONObject send(HttpServletRequest request, @RequestBody String idsList) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = messageInfoService.send(idsList, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51004", "发送消息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 发送消息
     * @param request
     * @param idsList
     * @return
     */
    @RequestMapping(value = "/revoke", method = RequestMethod.POST)
    public JSONObject revoke(HttpServletRequest request, @RequestBody String idsList) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = messageInfoService.revoke(idsList, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51004", "发送消息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }
    
    /**
     * 获取消息详情列表（指某条消息的所有发送情况）
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/getMessageDetailList", method = RequestMethod.GET)
    public JSONObject getMessageDetailList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
                json = messageInfoService.getMessageDetailList(pageable, params);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51001", "获取我的消息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 获取我的消息
     * @param request
     * @return
     */
    @RequestMapping(value = "/getMyMessage", method = RequestMethod.GET)
    public JSONObject getMyMessage(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
                MessageUser messageUser = new MessageUser();
                messageUser.setUserId(userInfo.getUserId());
                messageUser.setIsRead(MapUtils.getString(params, "isRead"));
                messageUser.setIsSend("1");
                json = messageInfoService.getMyMessage(pageable, messageUser, params);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51001", "获取我的消息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 设置消息已读
     * @param request
     * @return
     */
    @RequestMapping(value = "/setMessageRead", method = RequestMethod.POST)
    public JSONObject setMessageRead(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Long userMessageId = MapUtils.getLong(params, "userMessageId");
                json = messageInfoService.setMessageRead(userMessageId);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51001", "获取我的消息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 获取未读消息数
     * @param request
     * @return
     */
    @RequestMapping(value = "/getUnReadMessageCount", method = RequestMethod.GET)
    public JSONObject getUnReadMessageCount(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                int count = messageInfoService.getUnReadMessageCount(userInfo);
                json = RestfulRetUtils.getRetSuccess(count + "");
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51001", "获取我的消息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }
}
