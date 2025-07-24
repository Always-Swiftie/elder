package com.zzyl.nursing.service.impl;

import java.util.List;
import com.zzyl.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.service.IDeviceService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
}
