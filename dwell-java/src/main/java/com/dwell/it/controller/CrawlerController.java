package com.dwell.it.controller;

import com.dwell.it.entities.House;
import com.dwell.it.enums.DataSingleton;
import com.dwell.it.exception.InternalMethodInvokeException;
import com.dwell.it.model.HttpJSONResponse;
import com.dwell.it.service.IHouseService;
import com.dwell.it.utils.FileInputOutputUtils;
import com.dwell.it.utils.database.DatabaseStorageUtils;
import com.dwell.it.utils.geo.AMapApiGeoUtils;
import com.dwell.it.webspider.CrawlerConfigHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/fetch")
@RestController
public class CrawlerController {

    @Autowired
    private CrawlerConfigHelper crawlerConfigHelper;

    @Autowired
    private IHouseService iHouseService;

    private final String locationGeoFileName = "final_coordinates.txt";

    @Value("${amap.geofilename}")
    private String geoExcelFileName;


    @RequestMapping(value = "/crawlers")
    public HttpJSONResponse fetchOriginalDataSource() throws InternalMethodInvokeException {
        if (DataSingleton.INSTANCE.isFetchingServiceRunning()) {
            return HttpJSONResponse.ok("网络爬虫正在获取网页基本数据 请耐心等待");
        }

        try {
            DataSingleton.INSTANCE.setFetchingServiceRunning(true);
            crawlerConfigHelper.setUpWebSpiderConfigAndStartCrawlerService();
        } finally {
            DataSingleton.INSTANCE.setFetchingServiceRunning(false);
            DataSingleton.INSTANCE.setDatasourceFetched(true);
        }
        return HttpJSONResponse.ok("网络爬虫已完成基本数据爬取工作");
    }


    /**
     * 使用高德地图GEO 从原始地址数据 解析为坐标数据
     *
     * @return Json-Response
     */
    @RequestMapping(value = "/coordinates", method = RequestMethod.GET)
    public HttpJSONResponse batchFetchingGPSInfoAndUpdateDatabaseToLatest() {
        if (!DataSingleton.INSTANCE.isDatasourceFetched()) {
            return HttpJSONResponse.ok("请先调用/fetch/crawlers 为获取GPS数据做准备");
        }

        List<House> qualifiedHouseList = iHouseService.queryQualifiedAddressHouseListWithoutGeoInfo();
        AMapApiGeoUtils geoUtils = new AMapApiGeoUtils();
        List<String> geoInfoList = geoUtils.fetchQualifiedHouseListAfterRequestedAMapApi(qualifiedHouseList);
        System.out.println(geoInfoList);
        try {
            FileInputOutputUtils.saveContentToFiles(locationGeoFileName, geoInfoList);
            DatabaseStorageUtils.batchUpdateHouseItemForGeoInfo(qualifiedHouseList);
            DataSingleton.INSTANCE.setShouldExportingExcel(true);
            return HttpJSONResponse.ok("数据整理完毕： 已将符合地理信息编码的地址 转化为经纬度信息");
        } catch (InternalMethodInvokeException e) {
            e.printStackTrace();  // 只记录
        }

        return HttpJSONResponse.errorMessage("数据整理出现异常 请查询错误日志");
    }


    /**
     * 创建excel 用于高德地图 数据可视化方案
     *
     * @return Json-Response
     */
    @RequestMapping(value = "/excel", method = RequestMethod.GET)
    public HttpJSONResponse exportExcelFileWithinDataSource() {
        if (!DataSingleton.INSTANCE.isShouldExportingExcel()) {
            return HttpJSONResponse.ok("请先调用/fetch/coordinates 为导出EXCEL数据做准备");
        }

        List<House> houseList = iHouseService.queryQualifiedAddressHouseListWithinGeoInfo();
        List<House> dataList = iHouseService.makeHouseAddressShorter(houseList);
        boolean result = FileInputOutputUtils.createExcelFileFromDatasource(geoExcelFileName, dataList);
        if (result) {
            return HttpJSONResponse.ok("Excel可视化数据导出成功");
        }
        return HttpJSONResponse.errorMessage("Excel可视化数据导出时出现异常 详情请查询错误日志");
    }


    /**
     * Using seleniumhq for caching images from cdn-server to local web-browser
     *
     * @return Json-Response
     */
    @RequestMapping(value = "/images", method = RequestMethod.GET)
    public HttpJSONResponse cachingImageLinkOnWebBrowserFromCDN() {
        List<House> houseList = iHouseService.queryQualifiedAddressHouseListWithinGeoInfo();
        System.out.println(houseList.size() + "items");
        for (House house : houseList) {
            WebDriver webDriver = new ChromeDriver();
            String requestUrl = house.getMainImageUrl();
            try {
                System.out.println(requestUrl);
                webDriver.get(requestUrl);
                Thread.sleep(5000); // Workaround step for caching images in local web-browser
            } catch (Exception ex) {
                // do nothing...
            } finally {
                webDriver.quit();
            }
        }
        return HttpJSONResponse.ok("CDN依赖的图片 已缓存到浏览器");
    }
}
