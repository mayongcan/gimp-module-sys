package com.gimplatform.module.sys.restful;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.gimplatform.core.entity.DictData;
import com.gimplatform.core.entity.DictType;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.service.DictService;
import com.gimplatform.core.service.DistrictService;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;

/**
 * 字典相关的Restful接口
 * @author zzd
 */
@RestController
@RequestMapping(value = "/api/system/dict")
public class DictRestful {

    private static final Logger logger = LogManager.getLogger(DictRestful.class);

    @Autowired
    private DictService dictService;

    @Autowired
    private DistrictService districtService;

    /**
     * 用于记录打开日志
     * @param request
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public JSONObject index(HttpServletRequest request) {
        return RestfulRetUtils.getRetSuccess();
    }

    /**
     * 根据字典类型值获取字典数据列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getDictDataByDictTypeValue", method = RequestMethod.GET)
    public JSONObject getDictDataByDictTypeValue(HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        String dictTypeValue = request.getParameter("dictTypeValue");
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null) {
                // json = RestfulRetUtils.getErrorNoUser();
                json = RestfulRetUtils.getRetSuccess(dictService.getDictDataByDictTypeValue(dictTypeValue, null));
            } else
                json = RestfulRetUtils.getRetSuccess(dictService.getDictDataByDictTypeValue(dictTypeValue, userInfo));
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("22001", "获取字典类型列表失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 获取字典类型列表
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/getDictList", method = RequestMethod.GET)
    public JSONObject getDictList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
                DictType dictType = (DictType) BeanUtils.mapToBean(params, DictType.class);
                Page<DictType> dictList = dictService.getDictTypeList(pageable, dictType);
                json = RestfulRetUtils.getRetSuccessWithPage(dictList.getContent(), dictList.getTotalElements());
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("22002", "获取字典类型列表失败");
            logger.error(e.getMessage(), e);
        }
        logger.info(json.toString());
        return json;
    }

    /**
     * 新增字典信息
     * @param request
     * @param dictType
     * @return
     */
    @RequestMapping(value = "/addDictType", method = RequestMethod.POST)
    public JSONObject addDictType(HttpServletRequest request, @RequestBody DictType dictType) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = dictService.addDictType(dictType, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("22003", "新增字典信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 编辑字典信息
     * @param request
     * @param dictType
     * @return
     */
    @RequestMapping(value = "/editDictType", method = RequestMethod.POST)
    public JSONObject editDictType(HttpServletRequest request, @RequestBody DictType dictType) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = dictService.editDictType(dictType, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("22004", "编辑字典信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 删除字典信息
     * @param request
     * @param idsList
     * @return
     */
    @RequestMapping(value = "/delDictType", method = RequestMethod.POST)
    public JSONObject delDictType(HttpServletRequest request, @RequestBody String idsList) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = dictService.delDictType(idsList, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("22005", "删除字典信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 刷新字典信息
     * @return
     */
    @RequestMapping(value = "/refreshDictType", method = RequestMethod.POST)
    public JSONObject refreshDictType() {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                // 同时刷新区域缓存
                if (dictService.loadDictDataToCache() && districtService.loadDistrictDataToCache())
                    json = RestfulRetUtils.getRetSuccess();
                else
                    json = RestfulRetUtils.getErrorMsg("22007", "刷新字典信息失败");
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("22007", "刷新字典信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 获取字典数据列表
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/getDictDataList", method = RequestMethod.GET)
    public JSONObject getDictDataList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                Pageable pageable = new PageRequest(SessionUtils.getPageIndex(request), SessionUtils.getPageSize(request));
                DictData dictData = new DictData();
                dictData.setDictTypeId(MapUtils.getLong(params, "dictTypeId"));
                dictData.setTenantsId(userInfo.getTenantsId());
                dictData.setOrganizerId(userInfo.getOrganizerId());
                Page<DictData> dictDataList = dictService.getDictDataList(pageable, dictData);
                json = RestfulRetUtils.getRetSuccessWithPage(dictDataList.getContent(), dictDataList.getTotalElements());
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("22008", "获取字典数据列表失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 新增字典数据信息
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/addDictData", method = RequestMethod.POST)
    public JSONObject addDictData(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                DictData dictData = (DictData) BeanUtils.mapToBean(params, DictData.class);
                String dataShare = MapUtils.getString(params, "dataShare");
                json = dictService.addDictDataType(dictData, userInfo, dataShare);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("22009", "新增字典数据信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 编辑字典数据信息
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/editDictData", method = RequestMethod.POST)
    public JSONObject editDictData(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                DictData dictData = (DictData) BeanUtils.mapToBean(params, DictData.class);
                String dataShare = MapUtils.getString(params, "dataShare");
                json = dictService.editDictDataType(dictData, userInfo, dataShare);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("22010", "编辑字典数据信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 删除字典数据信息
     * @param request
     * @param idsList
     * @return
     */
    @RequestMapping(value = "/delDictData", method = RequestMethod.POST)
    public JSONObject delDictData(HttpServletRequest request, @RequestBody String idsList) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = dictService.delDictDataType(idsList, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("22011", "删除字典数据信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 更新排序
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/updateDictDataSort", method = RequestMethod.POST)
    public JSONObject updateDictDataSort(HttpServletRequest request, @RequestBody String params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = dictService.updateDictDataSort(params);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("22011", "更新字典排序失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }
}
