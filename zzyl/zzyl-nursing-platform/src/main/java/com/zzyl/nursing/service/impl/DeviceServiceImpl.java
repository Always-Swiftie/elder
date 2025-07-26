package com.zzyl.nursing.service.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.*;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.vo.DeviceDetailVo;
import com.zzyl.nursing.vo.ProductPageVo;
import com.zzyl.nursing.vo.ProductVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.service.IDeviceService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.servlet.ThemeResolver;

/**
 * DeviceService业务层处理
 * 
 * @author ruoyi
 * @date 2025-07-24
 */
@Service
@Slf4j
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

    private final String InstanceId = "13bc6d99-b449-4fc5-b229-1f72143a7881";

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
     * @param
     * @return 结果
     */
    @Override
    public int updateDevice(DeviceDto deviceDto)
    {
        Device device = new Device();
        BeanUtil.copyProperties(deviceDto, device);
        UpdateDeviceRequest updateDeviceRequest = new UpdateDeviceRequest();
        UpdateDevice body = new UpdateDevice();
        BeanUtil.copyProperties(deviceDto, body);

        updateDeviceRequest.setDeviceId(device.getIotId());
        updateDeviceRequest.setInstanceId(InstanceId);

        body.setDescription(device.getDeviceDescription());
        updateDeviceRequest.setBody(body);

        ioTDAClient.updateDevice(updateDeviceRequest);
        return deviceMapper.updateDevice(device);
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

    /**
     * 注册设备
     * @param deviceDto
     */
    @Override
    public void registerDevice(DeviceDto deviceDto) {
        //判断设备名称是否重复
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Device::getDeviceName, deviceDto.getDeviceName());
        if(count(queryWrapper) > 0){
            throw new BaseException("设备名称已存在，请重新输入");
        }
        //检验设备标识码是否重复
        LambdaQueryWrapper<Device> queryWrapperNodeId = new LambdaQueryWrapper<>();
        queryWrapperNodeId.eq(Device::getNodeId, deviceDto.getNodeId());
        if(count(queryWrapperNodeId) > 0){
            throw new BaseException("设备标识码已存在，请重新输入");
        }

        //校验同一位置是否绑定了同一类产品
        LambdaQueryWrapper<Device> condition = new LambdaQueryWrapper<>();
        condition.eq(Device::getProductKey, deviceDto.getProductKey())
                .eq(Device::getLocationType, deviceDto.getLocationType())
                .eq(Device::getPhysicalLocationType, deviceDto.getPhysicalLocationType())
                .eq(Device::getBindingLocation, deviceDto.getBindingLocation());
        if (count(condition) > 0) {
            throw new BaseException("该老人/位置已绑定该产品，请重新选择");
        }

        String nodeId = UUID.randomUUID() + deviceDto.getProductKey();
        deviceDto.setNodeId(nodeId);

        //iot中新增设备
        AddDeviceRequest request = new AddDeviceRequest();
        AddDevice body = new AddDevice();
        body.withProductId(deviceDto.getProductKey());
        body.withDeviceName(deviceDto.getDeviceName());
        body.withNodeId(deviceDto.getNodeId());
        request.withBody(body);
        AuthInfo authInfo = new AuthInfo();
        //秘钥
        String secret = UUID.randomUUID().toString().replaceAll("-", "");
        authInfo.withSecret(secret);
        body.setAuthInfo(authInfo);
        AddDeviceResponse response;
        try {
            response = ioTDAClient.addDevice(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("物联网接口 - 注册设备，调用失败");
        }

        //设备数据保存到数据库
        //属性拷贝
        Device device = BeanUtil.toBean(deviceDto, Device.class);
        //设备id、设备绑定状态
        device.setIotId(response.getDeviceId());
        //秘钥
        device.setSecret(secret);
        //根据device对象的
        String jsonStr =  redisTemplate.opsForValue().get(CacheConstants.ALL_PRODUCT_KEY);
        if(!StringUtils.isEmpty(jsonStr)){
            device.setProductName(getNameByProductId(jsonStr,deviceDto.getProductKey()));
        }
        //在数据库中新增设备
        deviceMapper.insert(device);
    }

    // ObjectMapper 是 Jackson 的核心类，用于 JSON 和 Java 对象之间的转换
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据 productId 从 JSON 字符串中获取对应的 name。
     * @param jsonString 包含产品信息的 JSON 数组字符串
     * @param targetProductId 目标 productId
     * @return 匹配的 name 字符串，如果未找到则返回 null
     */
    private static String getNameByProductId(String jsonString, String targetProductId) {
        try {
            // 将 JSON 字符串解析为 JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 检查根节点是否是数组
            if (rootNode.isArray()) {
                // 遍历数组中的每个 JSON 对象
                for (JsonNode node : rootNode) {
                    // 检查当前节点是否包含 "productId" 和 "name" 字段
                    if (node.has("productId") && node.has("name")) {
                        // 获取 productId 的文本值
                        String productId = node.get("productId").asText();

                        // 如果 productId 匹配，则返回对应的 name
                        if (productId.equals(targetProductId)) {
                            return node.get("name").asText();
                        }
                    }
                }
            }
        } catch (IOException e) {
            // 处理 JSON 解析或 IO 错误
            System.err.println("JSON 解析错误: " + e.getMessage());
            e.printStackTrace();
        }
        // 如果未找到匹配项或发生错误
        return null;
    }

    /**
     * 查询设备详情
     *
     * @return
     */
    @Override
    public DeviceDetailVo queryDeviceDetail(String iotId) {
        // 查询数据库
        Device device = getOne(Wrappers.<Device>lambdaQuery().eq(Device::getIotId, iotId));
        if (ObjectUtil.isEmpty(device)) {
            return null;
        }

        // 调用华为云物联网接口
        ShowDeviceRequest request = new ShowDeviceRequest();
        request.setDeviceId(iotId);
        ShowDeviceResponse response;
        try {
            response = ioTDAClient.showDevice(request);
        } catch (Exception e) {
            log.info("物联网接口 - 查询设备详情，调用失败:{}", e.getMessage());
            throw new BaseException("物联网接口 - 查询设备详情，调用失败");
        }

        // 属性拷贝
        DeviceDetailVo deviceVo = BeanUtil.toBean(device, DeviceDetailVo.class);
        deviceVo.setDeviceStatus(response.getStatus());

        String jsonStr = redisTemplate.opsForValue().get(CacheConstants.ALL_PRODUCT_KEY);
        deviceVo.setProductName(getNameByProductId(jsonStr, deviceVo.getProductKey()));

        // 日期格式化工具
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 处理 activeTime
        String activeTimeStr = response.getActiveTime();
        if (StringUtils.isNotEmpty(activeTimeStr)) {
            LocalDateTime activeTime = LocalDateTimeUtil.parse(activeTimeStr, DatePattern.UTC_MS_PATTERN);
            activeTime = activeTime.atZone(ZoneOffset.UTC)
                    .withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                    .toLocalDateTime();
            deviceVo.setActiveTime(activeTime.format(formatter));
        }

        // 如果你还希望设置 createTime 也为字符串（取自 device.getCreateTime()）
        if (device.getCreateTime() != null) {
            LocalDateTime createTime = device.getCreateTime()
                    .toInstant()
                    .atZone(ZoneId.of("Asia/Shanghai"))
                    .toLocalDateTime();
            deviceVo.setCreateTime(createTime.format(formatter));
        }

        return deviceVo;
    }

    /**
     * 查询设备上报数据
     *
     * @param iotId
     * @return
     */
    @Override
    public AjaxResult queryServiceProperties(String iotId) {

        ShowDeviceShadowRequest request = new ShowDeviceShadowRequest();
        request.setDeviceId(iotId);
        ShowDeviceShadowResponse response = ioTDAClient.showDeviceShadow(request);
        if (response.getHttpStatusCode() != 200) {
            throw new BaseException("物联网接口 - 查询设备上报数据，调用失败");
        }
        List<DeviceShadowData> shadow = response.getShadow();
        if(CollUtil.isEmpty(shadow)){
            List<Object> emptyList = Collections.emptyList();
            return AjaxResult.success(emptyList);
        }
        //返回数据
        JSONObject jsonObject = JSONUtil.parseObj(shadow.get(0).getReported().getProperties());

        List<Map<String,Object>> list = new ArrayList<>();

        //处理上报时间日期
        LocalDateTime activeTime =  LocalDateTimeUtil.parse(shadow.get(0).getReported().getEventTime(), "yyyyMMdd'T'HHmmss'Z'");
        //日期时区转换
        LocalDateTime eventTime = activeTime.atZone(ZoneId.from(ZoneOffset.UTC))
                .withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                .toLocalDateTime();

        jsonObject.forEach((k,v)->{
            Map<String,Object> map = new HashMap<>();
            map.put("functionId",k);
            map.put("value",v);
            map.put("eventTime",eventTime);
            list.add(map);
        });

        return AjaxResult.success(list);
    }

    /**
     * 删除设备
     * @param iotId
     */
    @Override
    public void deleteDevice(String iotId) {
        DeleteDeviceRequest request = new DeleteDeviceRequest();
        request.setDeviceId(iotId);
        request.setInstanceId(InstanceId);

        DeleteDeviceResponse response;
        try {
            response = ioTDAClient.deleteDevice(request);
            deviceMapper.delete(Wrappers.<Device>lambdaQuery().eq(Device::getIotId, iotId));
        }catch (Exception e){
            log.info("删除失败:{}", e.getMessage());
            throw new BaseException("删除失败!");
        }
    }

    /**
     * 查询产品详情
     * @param productKey
     * @return
     */
    @Override
    public AjaxResult queryProduct(String productKey) {
        //参数校验
        if(StringUtils.isEmpty(productKey)){
            throw new BaseException("请输入正确的参数");
        }
        //调用华为云物联网接口
        ShowProductRequest showProductRequest = new ShowProductRequest();
        showProductRequest.setProductId(productKey);
        ShowProductResponse response;

        try {
            response = ioTDAClient.showProduct(showProductRequest);
        } catch (Exception e) {
            throw new BaseException("查询产品详情失败");
        }
        //判断是否存在服务数据
        List<ServiceCapability> serviceCapabilities = response.getServiceCapabilities();
        if(CollUtil.isEmpty(serviceCapabilities)){
            return AjaxResult.success(Collections.emptyList());
        }

        return AjaxResult.success(serviceCapabilities);
    }
}
