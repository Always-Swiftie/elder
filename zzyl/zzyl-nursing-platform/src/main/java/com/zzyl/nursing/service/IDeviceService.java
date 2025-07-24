package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.vo.ProductPageVo;
import com.zzyl.nursing.vo.ProductVo;

/**
 * DeviceService接口
 * 
 * @author ruoyi
 * @date 2025-07-24
 */
public interface IDeviceService extends IService<Device>
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
     * 批量删除Device
     * 
     * @param ids 需要删除的Device主键集合
     * @return 结果
     */
    public int deleteDeviceByIds(Long[] ids);

    /**
     * 删除Device信息
     * 
     * @param id Device主键
     * @return 结果
     */
    public int deleteDeviceById(Long id);

    /**
     * 从物联网平台同步产品列表
     */
    void syncProductList();

    /**
     * 查询所有产品列表
     * @return
     */
    List<ProductPageVo> allProduct();

    /**
     * 注册设备
     * @param deviceDto
     */
    void registerDevice(DeviceDto deviceDto);
}
