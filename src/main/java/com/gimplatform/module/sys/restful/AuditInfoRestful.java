package com.gimplatform.module.sys.restful;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
import com.gimplatform.core.entity.AuditInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.AuditInfoService;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.JsonUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 审核信息表
 * 
 * @author zzd
 *
 */
@RestController
@RequestMapping(value = "/api/system/audit")
public class AuditInfoRestful {

	protected static final Logger logger = LogManager.getLogger(AuditInfoRestful.class);

	@Autowired
	private AuditInfoService auditInfoService;

	/**
	 * 用于记录打开日志
	 * 
	 * @param request
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public JSONObject index(HttpServletRequest request){ return RestfulRetUtils.getRetSuccess();}

	/**
	 * 获取列表
	 * 
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
				AuditInfo auditInfo = (AuditInfo) BeanUtils.mapToBean(params, AuditInfo.class);
				Page<AuditInfo> list = auditInfoService.getList(pageable, auditInfo);
				//转换
				List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
				List<AuditInfo> auditInfoList = list.getContent();
				Map<String, Object> tmpMap = null;
				JSONObject tmpJson = null;
				for(AuditInfo info : auditInfoList){
					if(!StringUtils.isBlank(info.getEditCache())){
						tmpJson = JSONObject.parseObject(info.getEditCache());
						if(tmpJson != null){
							tmpMap = JsonUtils.jsonToMap(tmpJson);
						}
					}
					if(tmpMap == null) tmpMap = new HashMap<String, Object>();
					tmpMap.put("id", info.getId());
					tmpMap.put("auditId", info.getAuditId());
					tmpMap.put("auditType", info.getAuditType());
					tmpMap.put("auditStatus", info.getAuditStatus());
					tmpMap.put("editStatus", info.getEditStatus());
					tmpMap.put("createDate", info.getCreateDate());
					mapList.add(tmpMap);
				}
				json = RestfulRetUtils.getRetSuccessWithPage(mapList, list.getTotalElements());
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("51001", "获取列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 新增信息
	 * 
	 * @param request
	 * @param ClientVersion
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public JSONObject add(HttpServletRequest request, @RequestBody AuditInfo auditInfo) {
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if (userInfo == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				json = auditInfoService.add(auditInfo, userInfo);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("51002", "新增信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 编辑信息
	 * 
	 * @param request
	 * @param ClientVersion
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public JSONObject edit(HttpServletRequest request, @RequestBody AuditInfo auditInfo) {
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if (userInfo == null)
				json = RestfulRetUtils.getErrorNoUser();
			else {
				json = auditInfoService.edit(auditInfo, userInfo);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("51003", "编辑信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 删除
	 * 
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
				json = auditInfoService.del(idsList, userInfo);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("51004", "删除信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

}
