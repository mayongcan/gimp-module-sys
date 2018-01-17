package com.gimplatform.module.sys.restful;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.entity.scheduler.JobInfo;
import com.gimplatform.core.entity.scheduler.ProcInfo;
import com.gimplatform.core.entity.scheduler.QrtzFiredDetails;
import com.gimplatform.core.entity.scheduler.RestfulInfo;
import com.gimplatform.core.entity.scheduler.TriggerInfo;
import com.gimplatform.core.service.SchedulerService;
import com.gimplatform.core.utils.DateUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SchedulerUtils;
import com.gimplatform.core.utils.SessionUtils;

/**
 * 任务调度相关的Restful接口
 * @author zzd
 *
 */
@RestController
@RequestMapping(value = "/api/system/scheduler")
public class SchedulerRestful {

    private static final Logger logger = LogManager.getLogger(DictRestful.class);
    
    @Autowired
    private SchedulerService schedulerService;

	/**
	 * 用于记录打开日志
	 * @param request
	 */
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public JSONObject index(HttpServletRequest request){ return RestfulRetUtils.getRetSuccess();}
	
	/**
	 * 获取job列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/jobList",method=RequestMethod.GET)
	public JSONObject jobList(HttpServletRequest request){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else json = SchedulerUtils.jobList();
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","获取数据列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 新增任务
	 * @param request
	 * @param procInfo
	 * @return
	 */
	@RequestMapping(value="/addProcJob",method=RequestMethod.POST)
	public JSONObject addProcJob(HttpServletRequest request,@RequestBody ProcInfo procInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				//任务已存在
				if(SchedulerUtils.isJobExsit(procInfo.getJobName(), procInfo.getJobGroup())){
					json = RestfulRetUtils.getErrorMsg("42001", "任务已存在");
				}else{
					json = schedulerService.saveProcJob(userInfo, procInfo);
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","添加任务失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 编辑任务
	 * @param request
	 * @param procInfo
	 * @return
	 */
	@RequestMapping(value="/editProcJob",method=RequestMethod.POST)
	public JSONObject editProcJob(HttpServletRequest request,@RequestBody ProcInfo procInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
//				//先删除旧的
//				String oldName = request.getParameter("oldName");
//				String oldGroup = request.getParameter("oldGroup");
//				if(!StringUtils.isBlank(oldName) && !StringUtils.isBlank(oldGroup)){
//					SchedulerUtils.deleteJob(oldName, oldGroup);
//				}
				json = schedulerService.saveProcJob(userInfo, procInfo);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","添加任务失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 新增Restful任务
	 * @param request
	 * @param restfulInfo
	 * @return
	 */
	@RequestMapping(value="/addRestfulJob",method=RequestMethod.POST)
	public JSONObject addRestfulJob(HttpServletRequest request,@RequestBody RestfulInfo restfulInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				if(SchedulerUtils.isJobExsit(restfulInfo.getJobName(), restfulInfo.getJobGroup())){
					json = RestfulRetUtils.getErrorMsg("42001","任务已存在");
				}else{
					json = schedulerService.saveRestfulJob(userInfo, restfulInfo);
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","新增Restful任务");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 编辑Restful任务
	 * @param request
	 * @param restfulInfo
	 * @return
	 */
	@RequestMapping(value="/editRestfulJob",method=RequestMethod.POST)
	public JSONObject editRestfulJob(HttpServletRequest request,@RequestBody RestfulInfo restfulInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
//				//先删除旧的
//				String oldName = request.getParameter("oldName");
//				String oldGroup = request.getParameter("oldGroup");
//				if(!StringUtils.isBlank(oldName) && !StringUtils.isBlank(oldGroup)){
//					SchedulerUtils.deleteJob(oldName, oldGroup);
//				}
				json = schedulerService.saveRestfulJob(userInfo, restfulInfo);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","编辑Restful任务");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 新增自定义任务
	 * @param request
	 * @param jobInfo
	 * @return
	 */
	@RequestMapping(value="/addCustomJob",method=RequestMethod.POST)
	public JSONObject addCustomJob(HttpServletRequest request,@RequestBody JobInfo jobInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				if(SchedulerUtils.isJobExsit(jobInfo.getJobName(), jobInfo.getJobGroup())){
					json = RestfulRetUtils.getErrorMsg("42001","任务已存在");
				}else{
					json = schedulerService.saveCustomJob(userInfo, jobInfo);
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","新增自定义任务任务");
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	/**
	 * 新增自定义任务
	 * @param request
	 * @param jobInfo
	 * @return
	 */
	@RequestMapping(value="/editCustomJob",method=RequestMethod.POST)
	public JSONObject editCustomJob(HttpServletRequest request,@RequestBody JobInfo jobInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
//				//先删除旧的
//				String oldName = request.getParameter("oldName");
//				String oldGroup = request.getParameter("oldGroup");
//				if(!StringUtils.isBlank(oldName) && !StringUtils.isBlank(oldGroup)){
//					SchedulerUtils.deleteJob(oldName, oldGroup);
//				}
				json = schedulerService.saveCustomJob(userInfo, jobInfo);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","新增自定义任务任务");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 操作任务
	 * @param request
	 * @param jobInfo
	 * @return
	 */
	@RequestMapping(value="/delJob",method=RequestMethod.POST)
	public JSONObject delJob(HttpServletRequest request,@RequestBody JobInfo jobInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				if(SchedulerUtils.deleteJob(jobInfo.getJobName(), jobInfo.getJobGroup())){
					json = RestfulRetUtils.getRetSuccess();
				}else{
					json = RestfulRetUtils.getErrorMsg("42001", "操作任务失败");
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","操作任务失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 操作任务
	 * @param request
	 * @param jobInfo
	 * @return
	 */
	@RequestMapping(value="/fireJob",method=RequestMethod.POST)
	public JSONObject fireJob(HttpServletRequest request,@RequestBody JobInfo jobInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				if(SchedulerUtils.fireJob(jobInfo.getJobName(), jobInfo.getJobGroup())){
					json = RestfulRetUtils.getRetSuccess();
				}else{
					json = RestfulRetUtils.getErrorMsg("42001", "操作任务失败");
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","操作任务失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 操作任务
	 * @param request
	 * @param jobInfo
	 * @return
	 */
	@RequestMapping(value="/pauseJob",method=RequestMethod.POST)
	public JSONObject pauseJob(HttpServletRequest request,@RequestBody JobInfo jobInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				if(SchedulerUtils.pauseJob(jobInfo.getJobName(), jobInfo.getJobGroup())){
					json = RestfulRetUtils.getRetSuccess();
				}else{
					json = RestfulRetUtils.getErrorMsg("42001", "操作任务失败");
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","操作任务失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 操作任务
	 * @param request
	 * @param jobInfo
	 * @return
	 */
	@RequestMapping(value="/resumeJob",method=RequestMethod.POST)
	public JSONObject resumeJob(HttpServletRequest request,@RequestBody JobInfo jobInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				if(SchedulerUtils.resumeJob(jobInfo.getJobName(), jobInfo.getJobGroup())){
					json = RestfulRetUtils.getRetSuccess();
				}else{
					json = RestfulRetUtils.getErrorMsg("42001", "操作任务失败");
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","操作任务失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 获取存储过程参数
	 * @param request
	 * @param procInfo
	 * @return
	 */
	@RequestMapping(value="/getProcParams",method=RequestMethod.GET)
	public JSONObject getProcParams(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				ProcInfo procInfo = new ProcInfo();
				procInfo.setDbType(MapUtils.getString(params, "dbType"));
				procInfo.setDbUrl(MapUtils.getString(params, "dbUrl"));
				procInfo.setDbUser(MapUtils.getString(params, "dbUser"));
				procInfo.setDbPwd(MapUtils.getString(params, "dbPwd"));
				procInfo.setDbName(MapUtils.getString(params, "dbName"));
				procInfo.setProcName(MapUtils.getString(params, "procName"));
				json = schedulerService.getProcParams(procInfo);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","获取存储过程参数失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 获取trigger列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/triggerList",method=RequestMethod.GET)
	public JSONObject triggerList(HttpServletRequest request, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				String jobName = MapUtils.getString(params, "jobName");
				String jobGroup = MapUtils.getString(params, "jobGroup");
				if(StringUtils.isBlank(jobName) || StringUtils.isBlank(jobName)){
					json = RestfulRetUtils.getErrorMsg("42001","参数传输有误");
				}else
					json = SchedulerUtils.getJobTriggers(jobName, jobGroup);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","获取数据列表失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 新增触发器
	 * @param request
	 * @param trigger
	 * @return
	 */
	@RequestMapping(value="/addTrigger",method=RequestMethod.POST)
	public JSONObject addTrigger(HttpServletRequest request,@RequestBody TriggerInfo trigger){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				//触发器已存在
				if(SchedulerUtils.isTriggerExsit(trigger.getTriggerName(), trigger.getTriggerGroup())){
					json = RestfulRetUtils.getErrorMsg("42001", "触发器已存在");
				}else{
					if(SchedulerUtils.addTrigger(trigger))
						json = RestfulRetUtils.getRetSuccess();
					else
						json = RestfulRetUtils.getErrorMsg("42001","新增触发器失败");
				}
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","新增触发器失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 编辑触发器
	 * @param request
	 * @param trigger
	 * @return
	 */
	@RequestMapping(value="/editTrigger",method=RequestMethod.POST)
	public JSONObject editTrigger(HttpServletRequest request,@RequestBody TriggerInfo trigger){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				if(SchedulerUtils.editTrigger(trigger))
					json = RestfulRetUtils.getRetSuccess();
				else
					json = RestfulRetUtils.getErrorMsg("42001","编辑触发器失败");
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","编辑触发器失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 删除触发器
	 * @param request
	 * @param trigger
	 * @return
	 */
	@RequestMapping(value="/delTrigger",method=RequestMethod.POST)
	public JSONObject delTrigger(HttpServletRequest request,@RequestBody TriggerInfo trigger){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				if(SchedulerUtils.deleteTrigger(trigger.getTriggerName(), trigger.getTriggerGroup()))
					json = RestfulRetUtils.getRetSuccess();
				else
					json = RestfulRetUtils.getErrorMsg("42001","删除触发器失败");
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","删除触发器失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 获取历史数据
	 * @param request
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/jobHistoryList",method=RequestMethod.GET)
	public JSONObject jobHistoryList(HttpServletRequest request,  @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));  
				QrtzFiredDetails qrtzFiredDetails = new QrtzFiredDetails();
				qrtzFiredDetails.setJobName(MapUtils.getString(params, "jobName"));
				qrtzFiredDetails.setJobGroup(MapUtils.getString(params, "jobGroup"));
				qrtzFiredDetails.setStartDate(DateUtils.parseDate(MapUtils.getString(params, "startDate")));
				qrtzFiredDetails.setEndDate(DateUtils.parseDate(MapUtils.getString(params, "endDate")));
				json = schedulerService.getJobHistory(pageable, qrtzFiredDetails);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("42001","获取历史数据失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
}
