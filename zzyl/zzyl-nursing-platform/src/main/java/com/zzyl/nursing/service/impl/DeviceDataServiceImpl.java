package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.constant.HttpStatus;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.nursing.Iot.IotMsgNotifyData;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.dto.DeviceDataPageReqDto;
import com.zzyl.nursing.mapper.DeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.DeviceDataMapper;
import com.zzyl.nursing.domain.DeviceData;
import com.zzyl.nursing.service.IDeviceDataService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Map;

/**
 * 设备数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-07-25
 */
@Service
@Slf4j
public class DeviceDataServiceImpl extends ServiceImpl<DeviceDataMapper, DeviceData> implements IDeviceDataService
{
    @Autowired
    private DeviceDataMapper deviceDataMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 查询设备数据
     * 
     * @param id 设备数据主键
     * @return 设备数据
     */
    @Override
    public DeviceData selectDeviceDataById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询设备数据列表
     *
     * @param dto 设备数据
     * @return 设备数据
     */
    @Override
    public TableDataInfo selectDeviceDataList(DeviceDataPageReqDto dto){

        LambdaQueryWrapper<DeviceData> lambdaQueryWrapper =  new LambdaQueryWrapper<>();
        Page<DeviceData> page = new Page(dto.getPageNum(),dto.getPageSize());
        //模糊查询设备名称
        if(StringUtils.isNotEmpty(dto.getDeviceName())){
            lambdaQueryWrapper.eq(DeviceData::getDeviceName,dto.getDeviceName());
        }
        //精确查询功能名称
        if (StringUtils.isNotEmpty(dto.getFunctionId())) {
            lambdaQueryWrapper.eq(DeviceData::getFunctionId, dto.getFunctionId());
        }
        //时间范围查询
        if(ObjectUtils.isNotEmpty(dto.getStartTime()) && ObjectUtils.isNotEmpty(dto.getEndTime())){
            lambdaQueryWrapper.between(DeviceData::getAlarmTime,dto.getStartTime(),dto.getEndTime());
        }

        //分页查询
        page = page(page,lambdaQueryWrapper);

        //封装分页对象
        return getTableDataInfo(page);

    }

    /**
     * 封装分页对象
     * @param page
     * @return
     */
    @NotNull
    private static TableDataInfo getTableDataInfo(Page<DeviceData> page) {
        TableDataInfo tableData = new TableDataInfo();
        tableData.setCode(HttpStatus.SUCCESS);
        tableData.setMsg("查询成功");
        tableData.setRows(page.getRecords());
        tableData.setTotal(page.getTotal());
        return tableData;
    }

    /**
     * 新增设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    @Override
    public int insertDeviceData(DeviceData deviceData)
    {

                return save(deviceData) == true? 1 : 0;
    }

    /**
     * 修改设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    @Override
    public int updateDeviceData(DeviceData deviceData)
    {

        return updateById(deviceData) == true ? 1 : 0;
    }

    /**
     * 批量删除设备数据
     * 
     * @param ids 需要删除的设备数据主键
     * @return 结果
     */
    @Override
    public int deleteDeviceDataByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) == true ? 1 : 0;
    }

    /**
     * 删除设备数据信息
     * 
     * @param id 设备数据主键
     * @return 结果
     */
    @Override
    public int deleteDeviceDataById(Long id)
    {
        return removeById(id) == true ? 1 : 0;
    }

    /**
     * 批量保存数据
     * @param iotMsgNotifyData
     */
    @Override
    public void batchInsertDeviceData(IotMsgNotifyData iotMsgNotifyData) {
        //判断设备是否存在
        String iotId = iotMsgNotifyData.getHeader().getDeviceId();
        Device device = deviceMapper.selectOne(Wrappers.<Device>lambdaQuery().eq(Device::getIotId, iotId));
        if(device == null){
            //日志的记录，方便后期查找问题
            log.error("设备不存在，iotId:{}",iotId);
            return;
        }
        //获取到设备上报的数据，一个设备可以有多个service
        iotMsgNotifyData.getBody().getServices().forEach(s->{

            //所有数据都已经装入到map中
            Map<String, Object> properties = s.getProperties();
            if (ObjectUtil.isEmpty(properties)){
                return;
            }
            //处理上报时间日期
            LocalDateTime eventTime =  LocalDateTimeUtil.parse(s.getEventTime(), "yyyyMMdd'T'HHmmss'Z'");
            //日期时区转换
            LocalDateTime alarmTime = eventTime.atZone(ZoneId.from(ZoneOffset.UTC))
                    .withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                    .toLocalDateTime();

            //转入多个设备数据
            List<DeviceData> deviceDataList = new ArrayList<>();

            //遍历map集合，批量新增数据
            properties.forEach((key, value)->{
                DeviceData deviceData = DeviceData.builder()
                        .iotId(iotId)
                        .deviceName(device.getDeviceName())
                        .productKey(device.getProductKey())
                        .productName(device.getProductName())
                        .functionId(key)
                        .accessLocation(device.getRemark())
                        .locationType(device.getLocationType())
                        .physicalLocationType(device.getPhysicalLocationType())
                        .deviceDescription(device.getDeviceDescription())
                        .alarmTime(alarmTime)
                        .dataValue(value + "")
                        .nickname(device.getNickname())
                        .build();
                deviceDataList.add(deviceData);

            });

            //批量保存
            saveBatch(deviceDataList);
            //除了持久化数据到db中,还保留一份到最新的数据到redis中,覆盖redis中当前设备的旧数据
            redisTemplate.opsForHash().put(CacheConstants.IOT_DEVICE_LAST_DATA, iotId,JSONUtil.toJsonStr(deviceDataList));

        });

    }
}
