package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.zzyl.nursing.domain.Device;

/**
 * DeviceMapper接口
 * 
 * @author ruoyi
 * @date 2025-07-24
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device>
{
    /**
     * 查询Device
     * 
     * @param id Device主键
     * @return Device
     */
    public Device selectDeviceById(Long id);

    /**
     * 查询Device列表
     * 
     * @param device Device
     * @return Device集合
     */
    public List<Device> selectDeviceList(Device device);

    /**
     * 新增Device
     * 
     * @param device Device
     * @return 结果
     */
    public int insertDevice(Device device);

    /**
     * 修改Device
     * 
     * @param device Device
     * @return 结果
     */
    public int updateDevice(Device device);

    /**
     * 删除Device
     * 
     * @param id Device主键
     * @return 结果
     */
    public int deleteDeviceById(Long id);

    /**
     * 批量删除Device
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeviceByIds(Long[] ids);
}
