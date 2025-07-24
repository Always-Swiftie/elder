package com.zzyl.nursing.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.hutool.json.JSONUtil;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.ListProductsRequest;
import com.huaweicloud.sdk.iotda.v5.model.ListProductsResponse;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.nursing.vo.ProductPageVo;
import com.zzyl.nursing.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.service.IDeviceService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.servlet.ThemeResolver;

import java.util.Arrays;

/**
 * DeviceService业务层处理
 * 
 * @author ruoyi
 * @date 2025-07-24
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService
{
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private IoTDAClient  ioTDAClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ThemeResolver themeResolver;

    /**
     * 查询Device
     * 
     * @param id Device主键
     * @return Device
     */
    @Override
    public Device selectDeviceById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询Device列表
     * 
     * @param device Device
     * @return Device
     */
    @Override
    public List<Device> selectDeviceList(Device device)
    {
        return deviceMapper.selectDeviceList(device);
    }

    /**
     * 新增Device
     * 
     * @param device Device
     * @return 结果
     */
    @Override
    public int insertDevice(Device device)
    {

                return save(device) == true? 1 : 0;
    }

    /**
     * 修改Device
     * 
     * @param device Device
     * @return 结果
     */
    @Override
    public int updateDevice(Device device)
    {

        return updateById(device) == true ? 1 : 0;
    }

    /**
     * 批量删除Device
     * 
     * @param ids 需要删除的Device主键
     * @return 结果
     */
    @Override
    public int deleteDeviceByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) == true ? 1 : 0;
    }

    /**
     * 删除Device信息
     * 
     * @param id Device主键
     * @return 结果
     */
    @Override
    public int deleteDeviceById(Long id)
    {
        return removeById(id) == true ? 1 : 0;
    }

    /**
     * 同步产品列表
     */
    @Override
    public void syncProductList() {
        //请求参数
        ListProductsRequest listProductsRequest = new ListProductsRequest();
        //设置条数
        listProductsRequest.setLimit(50);
        //发起请求
        ListProductsResponse response = ioTDAClient.listProducts(listProductsRequest);
        if(response.getHttpStatusCode() != 200){
            throw new BaseException("物联网接口-查询产品列表,同步失败");
        }
        //查询完成,存入redis
        redisTemplate.opsForValue().set(CacheConstants.ALL_PRODUCT_KEY, JSONUtil.toJsonStr(response.getProducts()));
    }

    /**
     * 查询所有产品列表
     * @return
     */
    @Override
    public List<ProductPageVo> allProduct() {
        //由于上一步已经从IoT平台同步数据,只需要从redis中获取即可
        String jsonStr =  redisTemplate.opsForValue().get(CacheConstants.ALL_PRODUCT_KEY);
        //如果为空,说明缓存不存在
        if(StringUtils.isEmpty(jsonStr)){
            return Collections.emptyList();
        }
        //解析json数据(转换成实体类对象)
        List<ProductVo> voList = JSONUtil.toList(jsonStr, ProductVo.class);
        List<ProductPageVo> productPageVoList = new ArrayList<>();
        for(ProductVo vo : voList){
            ProductPageVo productPageVo = new ProductPageVo();
            productPageVo.setProductKey(vo.getProductId());
            productPageVo.setProductName(vo.getName());
            productPageVoList.add(productPageVo);
        }
        return productPageVoList;
    }
}
