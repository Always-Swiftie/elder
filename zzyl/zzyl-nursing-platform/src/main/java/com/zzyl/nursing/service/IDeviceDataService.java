package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

import com.zzyl.nursing.Iot.IotMsgNotifyData;
import com.zzyl.nursing.domain.DeviceData;

/**
 * 设备数据Service接口
 * 
 * @author ruoyi
 * @date 2025-07-25
 */
public interface IDeviceDataService extends IService<DeviceData>
{
    /**
     * 查询设备数据
     * 
     * @param id 设备数据主键
     * @return 设备数据
     */
    public DeviceData selectDeviceDataById(Long id);

    /**
     * 查询设备数据列表
     * 
     * @param deviceData 设备数据
     * @return 设备数据集合
     */
    public List<DeviceData> selectDeviceDataList(DeviceData deviceData);

    /**
     * 新增设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    public int insertDeviceData(DeviceData deviceData);

    /**
     * 修改设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    public int updateDeviceData(DeviceData deviceData);

    /**
     * 批量删除设备数据
     * 
     * @param ids 需要删除的设备数据主键集合
     * @return 结果
     */
    public int deleteDeviceDataByIds(Long[] ids);

    /**
     * 删除设备数据信息
     * 
     * @param id 设备数据主键
     * @return 结果
     */
    public int deleteDeviceDataById(Long id);

    /**
     * 批量保存数据
     * @param iotMsgNotifyData
     */
    void batchInsertDeviceData(IotMsgNotifyData iotMsgNotifyData);
}
