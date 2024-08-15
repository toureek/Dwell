package com.dwell.it.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper=false)
@Data
@Table(name = "t_houses")
public class HouseDetail extends House {

    private String identifier;                                         // 公寓类型使用NO.8888  住宅类型使用 房源编号XA88888888
    private Integer contactId;                                         // 联系人Id
    private String bannerImageUrls;                                    // 轮播图URLs
    private String address;                                            // 房屋地址
    private String confirmApartmentType;                               // 是否公寓类型           0 未知；  1 是公寓  2 是住宅
    private String houseDescription;                                   // 房屋描述

    // ########################### ApartmentType Starts  需要修改父类的infoTags ###########################
    private String existSubway;                                           // 是否近地铁             0 未知（不显示）； 1 是； 2 否
    private String existElevator;                                       // 是否提供电梯           0 未知（不显示）； 1 提供； 2 不提供
    private String existShop;                                           // 是否近便利店           0 未知（不显示）； 1 是； 2 否
    private String existParking;                                       // 是否提供停车场         0 未知（不显示）； 1 提供； 2 不提供
    private String existGym;                                           // 是否有健身房           0 未知（不显示）； 1 提供； 2 不提供
    private String existPlayground;                                       // 是否提供活动场地        0 未知（不显示）； 1 提供； 2 不提供
    private String existSecurityMonitoring;                               // 是否提供视频防盗监控    0 未知（不显示）； 1 提供； 2 不提供
    private String existBookBar;                                       // 是否提供书吧           0 未知（不显示）； 1 提供； 2 不提供
    private String existClubBar;                                       // 是否提供吧台           0 未知（不显示）； 1 提供； 2 不提供
    // ########################### ApartmentType Ends ###########################


    // ########################### ResidentType Starts ###########################
    private String publishDateTime;                                    // 详细的发布时间
    private String rentHouseType;                                      // 租赁方式： 整组/合租/未知

    private String publishedDateTime;                                  // 发布
    private String rentLease;                                          // 租期
    private String floorHigh;                                          // 楼层
    private String parkingSpace;                                       // 车位
    private String electronicConsume;                                  // 用电
    private String moveInCondition;                                    // 入住
    private String visitingCondition;                                  // 看房
    private String liftCondition;                                      // 电梯
    private String waterConsume;                                       // 用水
    private String gasConsume;                                         // 燃气
    private String heatingConsume;                                     // 供暖

    private String tvSupported;                                        // 电视
    private String refrigeratorSupported;                              // 冰箱
    private String washingMachineSupported;                            // 洗衣机
    private String airConditionSupported;                              // 空调
    private String waterHeatingSupported;                              // 热水器
    private String bedSupported;                                       // 床
    private String heatingSupported;                                   // 暖气
    private String wifiSupported;                                      // 宽带
    private String wardrobeSupported;                                  // 衣柜
    private String gasSupported;                                       // 天然气
    // ########################### ResidentType Ends ###########################

    public HouseDetail() {
    }

    public HouseDetail(House house) {
        super();
        if (house.getId() != null && house.getId() > 0) {
            super.setId(house.getId());
            super.setProviderId(house.getProviderId());
            super.setTitle(house.getTitle());
            super.setDetailPageUrl(house.getDetailPageUrl());
            super.setArea(house.getArea());
            super.setAspect(house.getAspect());
            super.setLivingDiningKitchenInfo(house.getLivingDiningKitchenInfo());
            super.setStockCount(house.getStockCount());
            super.setMainImageUrl(house.getMainImageUrl());
            super.setCityZone(house.getCityZone());
            super.setInfoTags(house.getInfoTags());
            super.setLastUpdateTime(house.getLastUpdateTime());
            super.setTradePrice(house.getTradePrice());
            super.setTradePriceUnit(house.getTradePriceUnit());
        }
    }

    /**
     * 将sub-class多出的五个字段更新数据 (将house-object 更新为houseDetail-object)
     *
     * @param houseDetail house对象
     */
    private void updateToLatestInfo(HouseDetail houseDetail) {
        this.identifier = houseDetail.getIdentifier();
        this.contactId = houseDetail.getContactId();
        this.bannerImageUrls = houseDetail.getBannerImageUrls();
        this.address = houseDetail.getAddress();
        this.houseDescription = houseDetail.getHouseDescription();
    }


    /**
     * 根据页面获取的datasource所构造的mapInfo 更新houseDetail对象的内部数据(房源基本信息)
     *
     * @param mapInfo 由页面获取的datasource所构造的mapInfo
     */
    public void updateResidentBasicInfoFromMapData(LinkedHashMap<String, String> mapInfo) {
        if (mapInfo == null || mapInfo.size() == 0) {
            return;
        }

        List list = new ArrayList<>(mapInfo.values());
        for (int i = 0; i < list.size(); i++) {
            String result = list.get(i).toString();
            if (i == 0) {
                this.publishedDateTime = result;
            } else if (i == 1) {
                this.moveInCondition = result;
            } else if (i == 2) {
                this.rentLease = result;
            } else if (i == 3) {
                this.visitingCondition = result;
            } else if (i == 4) {
                this.floorHigh = result;
            } else if (i == 5) {
                this.liftCondition = result;
            } else if (i == 6) {
                this.parkingSpace = result;
            } else if (i == 7) {
                this.waterConsume = result;
            } else if (i == 8) {
                this.electronicConsume = result;
            } else if (i == 9) {
                this.gasConsume = result;
            } else if (i == 10) {
                this.heatingConsume = result;
            } else {
                // do nothing...
            }
        }
    }


    /**
     * 根据页面获取的datasource所构造的mapInfo 更新houseDetail对象的内部数据(房源基础设施信息)
     *
     * @param mapInfo 由页面获取的datasource所构造的mapInfo
     */
    public void updateResidentFacilitiesInfoFromMapData(Map<String, String> mapInfo) {
        if (mapInfo == null || mapInfo.size() == 0) {
            return;
        }

        List list = new ArrayList<>(mapInfo.values());
        for (int i = 0; i < list.size(); i++) {
            String result = list.get(i).toString();
            if (i == 0) {
                this.tvSupported = result;
            } else if (i == 1) {
                this.refrigeratorSupported = result;
            } else if (i == 2) {
                this.washingMachineSupported = result;
            } else if (i == 3) {
                this.airConditionSupported = result;
            } else if (i == 4) {
                this.waterHeatingSupported = result;
            } else if (i == 5) {
                this.bedSupported = result;
            } else if (i == 6) {
                this.heatingSupported = result;
            } else if (i == 7) {
                this.wifiSupported = result;
            } else if (i == 8) {
                this.wardrobeSupported = result;
            } else if (i == 9) {
                this.gasSupported = result;
            } else {
                // do nothing...
            }
        }
    }


    /**
     * 由网页数据向数据库中添加时，处理数据一致的更新（列表页面的数据不全，进入到详情页面后，构造数据齐全的houseDetail对象 再向数据库更新）
     *
     * @param houseDetail 由网页数据构造的houseDetail对象
     */
    public void updateToLatestResidentType(HouseDetail houseDetail) {
        if (houseDetail != null) {
            updateToLatestInfo(houseDetail);
            this.confirmApartmentType = "2";

            this.publishDateTime = houseDetail.getPublishDateTime();
            this.rentHouseType = houseDetail.getRentHouseType();

            this.publishedDateTime = houseDetail.getPublishedDateTime();
            this.rentLease = houseDetail.getRentLease();
            this.floorHigh = houseDetail.getFloorHigh();
            this.parkingSpace = houseDetail.getParkingSpace();
            this.electronicConsume = houseDetail.getElectronicConsume();
            this.moveInCondition = houseDetail.getMoveInCondition();
            this.visitingCondition = houseDetail.getVisitingCondition();
            this.liftCondition = houseDetail.getLiftCondition();
            this.waterConsume = houseDetail.getWaterConsume();
            this.gasConsume = houseDetail.getGasConsume();
            this.heatingConsume = houseDetail.getHeatingConsume();

            this.tvSupported = houseDetail.getTvSupported();
            this.refrigeratorSupported = houseDetail.getRefrigeratorSupported();
            this.washingMachineSupported = houseDetail.getWashingMachineSupported();
            this.airConditionSupported = houseDetail.getAirConditionSupported();
            this.waterHeatingSupported = houseDetail.getWaterHeatingSupported();
            this.bedSupported = houseDetail.getBedSupported();
            this.heatingSupported = houseDetail.getHeatingSupported();
            this.wifiSupported = houseDetail.getWifiSupported();
            this.wardrobeSupported = houseDetail.getWardrobeSupported();
            this.gasSupported = houseDetail.getGasSupported();
        }
    }
}