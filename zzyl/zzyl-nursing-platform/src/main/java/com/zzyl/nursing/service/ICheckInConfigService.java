package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import com.zzyl.nursing.domain.CheckInConfig;

/**
 * 入住配置Service接口
 * 
 * @author ruoyi
 * @date 2025-07-14
 */
public interface ICheckInConfigService extends IService<CheckInConfig>
{
    /**
     * 查询入住配置
     * 
     * @param id 入住配置主键
     * @return 入住配置
     */
    public CheckInConfig selectCheckInConfigById(Long id);

    /**
     * 查询入住配置列表
     * 
     * @param checkInConfig 入住配置
     * @return 入住配置集合
     */
    public List<CheckInConfig> selectCheckInConfigList(CheckInConfig checkInConfig);

    /**
     * 新增入住配置
     * 
     * @param checkInConfig 入住配置
     * @return 结果
     */
    public int insertCheckInConfig(CheckInConfig checkInConfig);

    /**
     * 修改入住配置
     * 
     * @param checkInConfig 入住配置
     * @return 结果
     */
    public int updateCheckInConfig(CheckInConfig checkInConfig);

    /**
     * 批量删除入住配置
     * 
     * @param ids 需要删除的入住配置主键集合
     * @return 结果
     */
    public int deleteCheckInConfigByIds(Long[] ids);

    /**
     * 删除入住配置信息
     * 
     * @param id 入住配置主键
     * @return 结果
     */
    public int deleteCheckInConfigById(Long id);
}
