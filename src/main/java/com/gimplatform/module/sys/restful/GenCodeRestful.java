/*
 * Copyright(c) 2018 gimplatform All rights reserved.
 * distributed with this file and available online at
 */
package com.gimplatform.module.sys.restful;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.generator.GeneratorApi;
import com.gimplatform.core.generator.GeneratorProperties;
import com.gimplatform.core.utils.DateUtils;
import com.gimplatform.core.utils.FileUtils;
import com.gimplatform.core.utils.JsonUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;
import com.gimplatform.core.utils.StringUtils;

import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.gimplatform.core.entity.GenCode;
import com.gimplatform.core.service.FuncInfoService;
import com.gimplatform.core.service.GenCodeService;

/**
 * Restful接口
 * @version 1.0
 * @author
 */
@RestController
@RequestMapping("/api/system/gencode")
public class GenCodeRestful {

    protected static final Logger logger = LogManager.getLogger(GenCodeRestful.class);

    @Autowired
    private GenCodeService genCodeService;

    @Autowired
    private FuncInfoService funcInfoService;

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
                GenCode genCode = new GenCode();
                genCode.setModuleName(MapUtils.getString(params, "moduleName"));
                genCode.setBasePackage(MapUtils.getString(params, "basePackage"));
                genCode.setSubPackage(MapUtils.getString(params, "subPackage"));
                genCode.setJdbcDriver(MapUtils.getString(params, "jdbcDriver"));
                genCode.setJdbcUrl(MapUtils.getString(params, "jdbcUrl"));
                genCode.setJdbcUsername(MapUtils.getString(params, "jdbcUsername"));
                genCode.setJdbcPassword(MapUtils.getString(params, "jdbcPassword"));
                genCode.setTableName(MapUtils.getString(params, "tableName"));
                genCode.setTableDesc(MapUtils.getString(params, "tableDesc"));
                genCode.setPageType(MapUtils.getString(params, "pageType"));
                genCode.setIsValid(MapUtils.getString(params, "isValid"));
                json = genCodeService.getList(pageable, genCode);
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
     * @param genCode
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JSONObject add(HttpServletRequest request, @RequestBody GenCode genCode) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = genCodeService.add(genCode, userInfo);
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
     * @param genCode
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public JSONObject edit(HttpServletRequest request, @RequestBody GenCode genCode) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo == null)
                json = RestfulRetUtils.getErrorNoUser();
            else {
                json = genCodeService.edit(genCode, userInfo);
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
                json = genCodeService.del(idsList, userInfo);
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51004", "删除信息失败");
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 导出SQL
     * @param request
     * @param params
     * @return
     */
    @RequestMapping(value = "/genTable", method = RequestMethod.GET)
    public void genTable(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) {
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo != null) {
                Long codeId = MapUtils.getLong(params, "codeId");
                GenCode genCode = genCodeService.getGenCode(codeId);
                // 获取表名等字段
                String tableName = genCode.getTableName();
                String tableDesc = genCode.getTableDesc();
                String tableColumn = genCode.getTableColumn();
                JSONObject json = JSONObject.parseObject(tableColumn);
                JSONArray jsonArray = json.getJSONArray("rows");
                StringBuffer sb = new StringBuffer();
                sb.append("-- ----------------------------\r\n");
                sb.append("--  Table structure for `" + tableName + "`\r\n");
                sb.append("-- ----------------------------\r\n");
                sb.append("DROP TABLE IF EXISTS `" + tableName + "`;\r\n");
                sb.append("CREATE TABLE `" + tableName + "` (\r\n");
                JSONObject tmpJson = null;
                String privateKey = "", isKey = "", columnName = "", columnDesc = "", columnType = "", columnDefault = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    tmpJson = jsonArray.getJSONObject(i);
                    isKey = tmpJson.getString("isKey");
                    columnName = tmpJson.getString("columnName");
                    columnDesc = tmpJson.getString("columnDesc");
                    columnType = tmpJson.getString("columnType");
                    columnDefault = tmpJson.getString("columnDefault");
                    // 添加主键列表
                    if ("Y".equals(isKey)) {
                        privateKey += "`" + columnName + "`,";
                    }
                    sb.append("\t`" + columnName + "` ");
                    if ("decimal".equals(columnType)) {
                        sb.append("decimal(" + tmpJson.getString("columnLen") + "," + tmpJson.getString("columnFloat") + ") ");
                    } else if ("varchar".equals(columnType)) {
                        sb.append("varchar(" + tmpJson.getString("columnLen") + ") ");
                    } else if ("date".equals(columnType)) {
                        sb.append("date ");
                    } else if ("datetime".equals(columnType)) {
                        sb.append("datetime ");
                    } else if ("int".equals(columnType)) {
                        sb.append("int(" + tmpJson.getString("columnLen") + ") ");
                    } else if ("text".equals(columnType)) {
                        sb.append("text ");
                    }
                    // 判断是否允许空值
                    if ("N".equals(tmpJson.getString("isNull"))) {
                        sb.append("NOT NULL ");
                        // 主键和text类型不输出DEFAULT
                        if (!"Y".equals(isKey)) {
                            if (!"text".equals(columnType)) {
                                if (!StringUtils.isBlank(columnDefault))
                                    sb.append("DEFAULT '" + columnDefault + "' ");
                            }
                        }
                    } else {
                        // 主键和text类型不输出DEFAULT
                        if (!"Y".equals(isKey)) {
                            if (!"text".equals(columnType)) {
                                if (StringUtils.isBlank(columnDefault))
                                    sb.append("DEFAULT NULL ");
                                else
                                    sb.append("DEFAULT '" + columnDefault + "' ");
                            }
                        }
                    }
                    sb.append("COMMENT '" + columnDesc + "', \r\n");
                }
                // 添加主键
                privateKey = privateKey.substring(0, privateKey.length() - 1);
                sb.append("\tPRIMARY KEY (" + privateKey + ")\r\n");
                sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='" + tableDesc + "';\r\n");
                // 获取自增表最大ID
                // Long maxId = StringUtils.toLong(tbGeneratorService.getMaxId("ID", "sys_tb_generator")) + 1;
                // 先删除自增表条目
                sb.append("\r\nBEGIN;\r\n");
                sb.append("DELETE FROM `sys_tb_generator` WHERE GEN_NAME = '" + tableName.toUpperCase() + "_PK" + "';\r\n");
                sb.append("COMMIT;\r\n");
                // 插入自增表
                sb.append("\r\nBEGIN;\r\n");
                sb.append("INSERT INTO sys_tb_generator(ID, GEN_TABLE, GEN_PRIMARY_KEY, GEN_NAME, GEN_VALUE) ");
                sb.append("SELECT ((SELECT MAX(ID) FROM sys_tb_generator) + 1) AS ID, '" + tableName + "' AS GEN_TABLE, '" + privateKey.replace("`", "") + "' AS GEN_PRIMARY_KEY, '" + tableName.toUpperCase() + "_PK"
                        + "' AS GEN_NAME, '1' AS GEN_VALUE;\r\n");
                // sb.append("INSERT INTO `sys_tb_generator` VALUES ('" + maxId + "', '" + tableName + "', '" + privateKey.replace("`", "") + "', '" + tableName.toUpperCase() + "_PK" + "', '1');\r\n");
                sb.append("COMMIT;\r\n");
                logger.info(sb.toString());
                // 导出数据
                String fileName = tableName + ".sql";
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/octet-stream; charset=UTF-8");
                response.setHeader("content-type", "application/octet-stream; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                PrintWriter out = response.getWriter();
                out.write(sb.toString());
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 生成权限
     * @param request
     * @param response
     * @param params
     */
    @RequestMapping(value = "/genFun", method = RequestMethod.GET)
    public void genFun(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) {
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo != null) {
                Long codeId = MapUtils.getLong(params, "codeId");
                GenCode genCode = genCodeService.getGenCode(codeId);

                String tableName = genCode.getTableName();
                String moduleName = genCode.getModuleName();
                String restfulPath = genCode.getRestfulPath();
                String pagePath = genCode.getPagePath() + "/" + tableName.toLowerCase().replace("_", "") + ".html";
                String tableNameCase = StringUtils.toCamelCase(tableName);
                // 构造权限了列表（菜单，新增、编辑、修改三个默认权限）
                List<Map<String, Object>> listFunc = new ArrayList<Map<String, Object>>();
                listFunc.add(getFunMapInfo("10000_", moduleName, "9999_", 100, "100300", "2", pagePath, restfulPath + "/index", "Y", "N", "", moduleName, "N"));
                listFunc.add(getFunMapInfo("10001_", "新增", "10000_", 1, "100400", "3", restfulPath + "/add", tableNameCase + "Add", "Y", "N", "glyphicon glyphicon-plus", "新增数据", "N"));
                listFunc.add(getFunMapInfo("10002_", "编辑", "10000_", 2, "100400", "3", restfulPath + "/edit", tableNameCase + "Edit", "Y", "N", "glyphicon glyphicon-edit", "编辑数据", "N"));
                listFunc.add(getFunMapInfo("10003_", "删除", "10000_", 3, "100400", "3", restfulPath + "/del", tableNameCase + "Del", "Y", "N", "glyphicon glyphicon-trash", "删除数据", "N"));
                JSONObject json = new JSONObject();
                json.put("RetData", funcInfoService.getJsonTree(listFunc, "9999_"));
                String funcXml = JsonUtils.jsonToXml(json);

                // 导出数据
                String fileName = tableName + "_func.xml";
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

    private Map<String, Object> getFunMapInfo(String funcId, String funcName, String parentFuncId, int dispOrder, String funcType, String funcLevel, String funcLink, String funcFlag, String isShow, String isBlank, String funcIcon, String funcDesc,
            String isBase) {
        Map<String, Object> menuMap = new HashMap<String, Object>();
        menuMap.put("funcId", funcId);
        menuMap.put("funcName", funcName);
        menuMap.put("parentFuncId", parentFuncId);
        menuMap.put("dispOrder", dispOrder);
        menuMap.put("funcType", funcType);
        menuMap.put("funcLevel", funcLevel);
        menuMap.put("funcLink", funcLink);
        menuMap.put("funcFlag", funcFlag);
        menuMap.put("isShow", isShow);
        menuMap.put("isBlank", isBlank);
        menuMap.put("funcIcon", funcIcon);
        menuMap.put("funcDesc", funcDesc);
        menuMap.put("isBase", isBase);
        return menuMap;
    }

    /**
     * 生成模块代码
     * @param request
     * @param response
     * @param params
     */
    @RequestMapping(value = "/genFile", method = RequestMethod.GET)
    public JSONObject genFile(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) {
        JSONObject json = new JSONObject();
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo != null) {
                Long codeId = MapUtils.getLong(params, "codeId");
                GenCode genCode = genCodeService.getGenCode(codeId);
                // 读取生成器配置文件
                Resource resource = new ClassPathResource("generatorTemplate.xml");
                String template = FileUtils.toString(resource.getInputStream());
                String outRootPath = GeneratorProperties.getProperty("outRoot");
                String templatePath = GeneratorProperties.getProperty("templatePath");
                String filePath = outRootPath.replace("/code", "");
                // 生成文件前，先删除目录的旧代码，防止有其他文件
                FileUtils.deleteDirectory(outRootPath);
                // 替换对应的内容
                template = template.replace("${basePackage}", genCode.getBasePackage());
                template = template.replace("${subPackage}", genCode.getSubPackage());
                template = template.replace("${restfulPath}", genCode.getRestfulPath());
                template = template.replace("${pagePath}", genCode.getPagePath());
                template = template.replace("${jdbcDriver}", genCode.getJdbcDriver());
                template = template.replace("${jdbcUrl}", genCode.getJdbcUrl().replace("&", "&amp;"));
                template = template.replace("${jdbcUsername}", genCode.getJdbcUsername());
                template = template.replace("${jdbcPassword}", genCode.getJdbcPassword());
                template = template.replace("${outRoot}", outRootPath);
                template = template.replace("${templatePath}", templatePath);
                template = template.replace("${pageType}", genCode.getPageType());
                template = template.replace("${moduleName}", genCode.getModuleName());
                template = template.replace("${tableColumn}", genCode.getTableColumn());
                if (!StringUtils.isBlank(genCode.getTreeInfo()))
                    template = template.replace("${treeInfo}", genCode.getTreeInfo());
                else
                    template = template.replace("${treeInfo}", "");
                // 重新加载属性文件
                GeneratorProperties.addPropertiesFile(new ByteArrayInputStream(template.getBytes("UTF-8")));
                // 获取模板路径(先从Resource目录下获取，如果不存在，则从配置文件中获取)
                Resource templateResource = new ClassPathResource("GenCodeTemplate/");
                URL templateUrl = templateResource.getURL();
                File templateDir = new File(templateUrl.getPath());
                if (templateDir.exists())
                    templatePath = templateUrl.getPath();
                logger.info("模板文件路径:" + templatePath);
                // 生成代码
                GeneratorApi generatorApi = new GeneratorApi();
                generatorApi.generateByTable(genCode.getTableName(), templatePath);
                String zipFileName = genCode.getTableName() + "_" + DateUtils.getDate("yyyy_MM_dd_HH_mm_ss") + ".zip";
                logger.info("开始压缩文件:" + zipFileName);
                FileUtils.zipFiles(outRootPath, "*", filePath + "/" + zipFileName);
                // 压缩成功后，删除生成的代码
                FileUtils.deleteDirectory(outRootPath);
                json = RestfulRetUtils.getRetSuccess(zipFileName);
            } else {
                json = RestfulRetUtils.getErrorNoUser();
            }
        } catch (Exception e) {
            json = RestfulRetUtils.getErrorMsg("51002", "生成文件出错:" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return json;
    }

    /**
     * 生成模块代码
     * @param request
     * @param response
     * @param params
     */
    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) {
        try {
            UserInfo userInfo = SessionUtils.getUserInfo();
            if (userInfo != null) {
                String zipFileName = MapUtils.getString(params, "zipFileName");
                logger.info("开始下载文件:" + zipFileName);
                String outRootPath = GeneratorProperties.getProperty("outRoot");
                String filePath = outRootPath.replace("/code", "");
                File zipFile = new File(filePath + "/" + zipFileName);
                response.setCharacterEncoding("UTF-8");
                response.setContentType(FileUtils.getContentType(zipFileName) + "; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");
                OutputStream out = response.getOutputStream();
                BufferedInputStream br = new BufferedInputStream(new FileInputStream(zipFile));
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = br.read(buf)) > 0)
                    out.write(buf, 0, len);
                out.flush();
                br.close();
                out.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
