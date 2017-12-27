package com.gimplatform.module.sys.listen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.gimplatform.core.service.DictService;
import com.gimplatform.core.service.DistrictService;
import com.gimplatform.core.service.FuncInfoService;
import com.gimplatform.core.utils.SchedulerUtils;
import com.gimplatform.core.utils.SpringContextHolder;

/**
 * 应用程序启动后执行的监听器
 * @author zzd
 *
 */
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LogManager.getLogger(StartupListener.class);
    
    private DictService dictService = null;
    
    private FuncInfoService funcInfoService = null;
    
    private DistrictService districtService = null;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("初始化自定义监听器...");
		//使用上下文工具获取服务类Bean
		dictService = SpringContextHolder.getBean(DictService.class);
		funcInfoService = SpringContextHolder.getBean(FuncInfoService.class);
		districtService = SpringContextHolder.getBean(DistrictService.class);
		if(dictService == null || funcInfoService == null || districtService == null){
			logger.error("容器注入失败！");
			return;
		}
		//1.加载字典数据
		if(!dictService.loadDictDataToCache())
			logger.error("加载字典数据失败！");
		//2.加载所有权限数据到内存中
		if(!funcInfoService.loadFuncDataToCache())
			logger.error("加载权限数据失败！");
		//3.加载所有区域数据
		if(!districtService.loadDistrictDataToCache())
			logger.error("加载区域数据失败！");
		
		//启动任务调度
		SchedulerUtils.startScheduler();
	}
}
