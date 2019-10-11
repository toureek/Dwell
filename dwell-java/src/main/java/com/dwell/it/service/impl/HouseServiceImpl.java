package com.dwell.it.service.impl;

import com.dwell.it.dao.IHouseDao;
import com.dwell.it.entities.House;
import com.dwell.it.entities.HouseDetail;
import com.dwell.it.exception.DBManipulateException;
import com.dwell.it.exception.MessageRuntimeException;
import com.dwell.it.service.IHouseService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class HouseServiceImpl implements IHouseService {

    @Autowired
    private IHouseDao iHouseDao;


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean addNewHouseOne(House house) {  // providerId有个外键依赖
        if (house != null && house.getProviderId() != null && house.getTitle() != null && house.getDetailPageUrl() != null
                && house.getTitle() != null && house.getMainImageUrl() != null && house.getLastUpdateTime() != null
                && house.getTradePrice() != null && house.getTradePriceUnit() != null) {
            try {
                int effectedNumber = iHouseDao.insertNewHouseObject(house);
                if (effectedNumber > 0) {
                    return true;
                }
                throw new DBManipulateException("插入这条house数据失败" + "URL:" + house.getDetailPageUrl());
            } catch (Exception ex) {
                throw new DBManipulateException(String.format("插入这条house数据失败: URL-%s %s", house.getDetailPageUrl(), ex.getMessage()));
            }
        }
        throw new DBManipulateException("插入这条house数据失败: 信息记录不完整");
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean modifyHouseDetailOne(HouseDetail houseDetail) {
        if (houseDetail == null || (houseDetail.getId() == null || houseDetail.getId() < 1)) {
            throw new DBManipulateException("这条house记录为空 或 ID信息不正确");
        }

        String type = houseDetail.getConfirmApartmentType();
        if (type == null || type.length() == 0) {
            throw new MessageRuntimeException("缺少ConfirmApartmentType参数");
        }

        if (type.equals("2")) {  // 住宅类型的数据更新
            return updateResidenceTypeHouse(houseDetail);
        }

        throw new DBManipulateException("修改这条house数据失败: type记录不完整");
    }


    @Transactional(propagation = Propagation.REQUIRED)
    private boolean updateResidenceTypeHouse(HouseDetail houseDetail) {
        try {
            int effectedNumber = iHouseDao.updateResidenceDetailObject(houseDetail);
            if (effectedNumber > 0) {
                return true;
            }
            throw new DBManipulateException("修改这条住宅类型的house数据失败");
        } catch (Exception ex) {
            throw new DBManipulateException(String.format("修改这条住宅类型的house数据失败: %s", ex.getMessage()));
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean removeHouseById(int id) {
        if (id < 1) {
            throw new DBManipulateException("删除这条house数据失败：Id不存在");
        }

        try {
            int effectedNumber = iHouseDao.deleteHouseObjectById(id);
            if (effectedNumber > 0) {
                return true;
            }
            throw new DBManipulateException("删除这条公寓类型的house数据失败");
        } catch (Exception ex) {
            throw new DBManipulateException(String.format("删除这条住宅类型的house数据失败: %s", ex.getMessage()));
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean batchAddNewHouseList(List<House> list) {
        if (list == null || list.size() == 0) throw new DBManipulateException("没有要插入的数据");

        try {
            if (iHouseDao.batchInsertNewHouseList(list)) {
                return true;
            }
            throw new DBManipulateException(String.format("本次批处理插入数据失败"));
        } catch (Exception ex) {
            throw new DBManipulateException(String.format("本次批处理插入数据失败: %s", ex.getMessage()));
        }
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isAllowedToInsertNewHouseRecord(String houseTitle, String detailPageURL) {
        if (detailPageURL == null || detailPageURL.trim().length() == 0) {
            throw new DBManipulateException("这条houseItem detailPageURL条件为空");
        }

        try {
            return iHouseDao.searchTargetHouseByTitleAndURL(houseTitle, detailPageURL) == null;
        } catch (Exception ex) {
            throw new DBManipulateException("这条houseItem detailPageURL 查询为空");
        }
    }


    // 二级页面的数据以来逻辑
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public House findTargetHouseByPageUrl(String pageURL) {
        if (pageURL == null || pageURL.length() == 0) {
            throw new DBManipulateException("这条house记录的 detailPageURL条件为空");
        }

        try {
            List<House> list = iHouseDao.searchHousesByPageURL(pageURL);
            if (list.size() > 0) {
                House house = list.get(0);
                return house;
            } else {
                throw new DBManipulateException(String.format("没有找到该url对应的记录"));
            }
        } catch (Exception ex) {
            throw new DBManipulateException(String.format("没有找到该url对应的记录: %s", ex.getMessage()));
        }
    }


    // Amap-SDK Requesting condition
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<House> queryQualifiedAddressHouseListWithoutGeoInfo() {
        try {
            List<House> list = iHouseDao.fetchExistedAddressQualifiedHouseListWithoutGeo();
            if (list == null && list.size() == 0) {
                throw new MessageRuntimeException("没有符合条件的房屋信息");
            }
            return list;
        } catch (Exception ex) {
            throw new MessageRuntimeException("没有符合条件的房屋信息");
        }
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<House> queryQualifiedAddressHouseListWithinGeoInfo() {
        try {
            List<House> list = iHouseDao.fetchExistedAddressQualifiedHouseListWithinGeo();
            if (list == null && list.size() == 0) {
                throw new MessageRuntimeException("没有符合条件的房屋信息");
            }
            return list;
        } catch (Exception ex) {
            throw new MessageRuntimeException("没有符合条件的房屋信息");
        }
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean batchUpdateLocationGeoList(List<House> list) {
        if (list == null || list.size() == 0) {
            throw new MessageRuntimeException("没有符合条件的房屋信息");
        }
        try {
            if (iHouseDao.batchUpdateHouseGeoInfo(list)) {
                return true;
            }
            throw new DBManipulateException(String.format("本次批处理修改GEO数据失败"));
        } catch (DBManipulateException ex) {
            throw new DBManipulateException(String.format("本次批处理修改GEO数据失败: %s", ex.getMessage()));
        }
    }


    /**
     * 简化地址信息 只保留小区名称
     *
     * @param houseList 需要处理的数据
     * @return 简化后的数据
     */
    public List<House> makeHouseAddressShorter(List<House> houseList) {
        List<House> resultList = new ArrayList<>(houseList.size());
        for (House house : houseList) {
            house.updateAddressNameShorter();
            resultList.add(house);
        }
        return resultList;
    }


    /**
     * @param pageNumber        当前页数
     * @param pageSize          分页大小
     * @param whereConditionSql 分页查询语句
     * @return 分页的数据列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Page<House> queryHousesByPaging(int pageNumber, int pageSize, String whereConditionSql) {
        if (pageNumber < 1) pageNumber = 1;
        if (pageSize < 1) pageSize = 1;

        try {
            PageHelper.startPage(pageNumber, pageSize);
            Page<House> pageList = iHouseDao.fetchingHousesListPageByPagingCondition(whereConditionSql);
            if (pageList.size() > 0) {
                return pageList;
            }
            throw new MessageRuntimeException("没有符合条件的房屋信息");
        } catch (Exception ex) {
            throw new MessageRuntimeException("没有符合条件的房屋信息");
        }
    }


    /**queryHousesByPaging
     * 查询符合App API条件的房源总数
     *
     * @param whereConditionSql whereSQL
     * @return 查询符合App API条件的房源总数
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer queryTotalQualifiedHousesCountByCondition(String whereConditionSql) {
        try {
            Integer count = iHouseDao.fetchTotalHousesCountByCondition(whereConditionSql);
            if (count > 0) {
                return count;
            }
            throw new MessageRuntimeException("暂无房屋数据");
        } catch (Exception ex) {
            throw new MessageRuntimeException("暂无房屋数据");
        }
    }
}
