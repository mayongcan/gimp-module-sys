package com.gimplatform.module.sys.restful;

import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.FuncInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.FuncInfoService;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;
import com.gimplatform.core.utils.JsonUtils;

/**
 * 权限相关的Restful接口
 * @author zzd
 *
 */
@RestController
@RequestMapping(value = "/api/system/func")
public class FuncRestful {

    protected static final Logger logger = LogManager.getLogger(FuncRestful.class);
    
    @Autowired
    private FuncInfoService funcInfoService;

	/**
	 * 用于记录打开日志
	 * @param request
	 */
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public JSONObject index(HttpServletRequest request){ return RestfulRetUtils.getRetSuccess();}
	

	/**
	 * 获取权限树列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getFuncTree",method=RequestMethod.GET)
	public JSONObject getFuncTree(HttpServletRequest request, HttpServletResponse response){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = funcInfoService.getFuncTree();
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("24001","获取权限菜单树失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 新增权限信息
	 * @param request
	 * @param funcInfo
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public JSONObject add(HttpServletRequest request, @RequestBody FuncInfo funcInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = funcInfoService.addFunc(funcInfo, userInfo);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("24002","新增权限信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 编辑权限信息
	 * @param request
	 * @param funcInfo
	 * @return
	 */
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	public JSONObject edit(HttpServletRequest request, @RequestBody FuncInfo funcInfo){
		JSONObject json = new JSONObject();
		try{
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = funcInfoService.editFunc(funcInfo, userInfo);
			}
		}catch(Exception e){
			json = RestfulRetUtils.getErrorMsg("24003","编辑权限信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 删除权限信息
	 * @param request
	 * @param idsList
	 * @return
	 */
	@RequestMapping(value="/del",method=RequestMethod.POST)
	public JSONObject del(HttpServletRequest request,@RequestBody String idsList){
		JSONObject json = new JSONObject();
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo == null) json = RestfulRetUtils.getErrorNoUser();
			else {
				json = funcInfoService.delFunc(idsList, userInfo);
			}
		} catch (Exception e) {
			json = RestfulRetUtils.getErrorMsg("24004","删除权限信息失败");
			logger.error(e.getMessage(), e);
		}
		return json;
	}
	
	/**
	 * 导出
	 * @param request
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/exportFunc",method=RequestMethod.GET)
	public void exportFunc(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params){
		try {
			UserInfo userInfo = SessionUtils.getUserInfo();
			if(userInfo != null){
				Long funcAndSubId = MapUtils.getLong(params, "funcId");
				String funcXml = funcInfoService.getFuncTreeByFuncId(funcAndSubId);
				logger.info(funcXml);
				//导出数据
				String fileName = "ExportFuncData.xml";
				response.setCharacterEncoding("UTF-8");
			    response.setContentType("application/octet-stream; charset=UTF-8");
			    response.setHeader("content-type", "application/octet-stream; charset=UTF-8");
			    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
				PrintWriter out = response.getWriter();
			    out.write(funcXml);
			    out.flush();
			    out.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 导入文件
	 * @param file
	 * @return
	 */
	@RequestMapping("/importFunc")
    @ResponseBody
    public JSONObject importFunc(@RequestParam("file")MultipartFile file, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		if(!file.isEmpty()){
			try {
				//获取参数
				String funcId = MapUtils.getString(params, "funcId");
				//获取所有权限
				JSONArray tmpJsonArray = funcInfoService.getFuncTree().getJSONArray("RetData");
				logger.info(tmpJsonArray.toString());
				//获取插入的权限
				json = JSONObject.parseObject(JsonUtils.xmlToJson(null, new String(file.getBytes())));
				//插入节点
				JSONArray retJsonArray = insertJson(tmpJsonArray, funcId, handleUploadFunc(json.getJSONObject("RetData")));
				logger.info(retJsonArray.toString());
				json = RestfulRetUtils.getRetSuccess(retJsonArray);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
   				json = RestfulRetUtils.getErrorMsg("10001","文件上传失败：" + e.getMessage());
			}
			return json;
       }else{
    	   return RestfulRetUtils.getErrorMsg("10002","文件上传失败：获取上传文件失败");
       }
    }
	
	/**
	 * 插入json
	 * @param jsonArray
	 * @param funcId
	 * @param insertJson
	 * @return
	 */
	private JSONArray insertJson(JSONArray jsonArray, String funcId, JSONObject insertJson){
		JSONObject tmpJson = new JSONObject();
		for(int i = 0; i < jsonArray.size(); i++){
			tmpJson = jsonArray.getJSONObject(i);
			if(tmpJson.containsKey("id") && tmpJson.getString("id").equals(funcId)){
				Object obj = tmpJson.get("children");
				if(obj instanceof Boolean){tmpJson.put("children", insertJson);}
				else if(obj instanceof JSONArray){
					JSONArray subJson = (JSONArray) obj;
					subJson.add(insertJson);
				}
				break;
			}
			if(tmpJson.containsKey("children")){
				Object obj = tmpJson.get("children");
				if(obj instanceof Boolean){}
				else if(obj instanceof JSONArray){
					insertJson(tmpJson.getJSONArray("children"), funcId, insertJson);
				}
				else 
					logger.info("导入权限菜单----类型有误");
			}
		}
		return jsonArray;
	}
	
	/**
	 * 处理文件读取的json，将children对象转换为JSON数组
	 * @param json
	 * @return
	 */
	private JSONObject handleUploadFunc(JSONObject json){
		if(json == null) return json;
		if(json.containsKey("children")){
			Object obj = json.get("children");
			if(obj instanceof Boolean){}
			else if(obj instanceof JSONObject){
				JSONArray subJson = new JSONArray();
				subJson.add(obj);
				json.put("children", subJson);
			}
			else if(obj instanceof JSONArray){
				JSONArray subJson = (JSONArray) obj;
				for(int i = 0; i < subJson.size(); i++){
					handleUploadFunc(subJson.getJSONObject(i));
				}
			}
		}
		return json;
	}

	/**
	 * 保存导入的权限树
	 * @param file
	 * @param params
	 * @return
	 */
	@RequestMapping("/saveImportFunc")
    @ResponseBody
    public JSONObject saveImportFunc(@RequestParam("file")MultipartFile file, @RequestParam Map<String, Object> params){
		JSONObject json = new JSONObject();
		if(!file.isEmpty()){
			try {
				//获取参数
				UserInfo userInfo = SessionUtils.getUserInfo();
				String funcId = MapUtils.getString(params, "funcId");
				JSONObject funcFileJson = handleUploadFunc(JSONObject.parseObject(JsonUtils.xmlToJson(null, new String(file.getBytes()))).getJSONObject("RetData"));
				json = funcInfoService.saveImportFunc(funcId, funcFileJson, userInfo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
   				json = RestfulRetUtils.getErrorMsg("10001","保存导入的权限文件失败：" + e.getMessage());
			}
			return json;
       }else{
    	   return RestfulRetUtils.getErrorMsg("10002","保存导入的权限文件");
       }
    }
}
