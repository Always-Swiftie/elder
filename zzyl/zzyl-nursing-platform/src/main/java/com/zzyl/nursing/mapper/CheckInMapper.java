package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.zzyl.nursing.domain.CheckIn;

/**
 * 入住Mapper接口
 * 
 * @author anthony
 * @date 2025-07-14
 */
@Mapper

public interface CheckInMapper extends BaseMapper<CheckIn>
{
    /**
     * 查询入住
     * 
     * @param id 入住主键
     * @return 入住
     */
    public CheckIn selectCheckInById(Long id);

    /**
     * 查询入住列表
     * 
     * @param checkIn 入住
     * @return 入住集合
     */
    public List<CheckIn> selectCheckInList(CheckIn checkIn);

    /**
     * 新增入住
     * 
     * @param checkIn 入住
     * @return 结果
     */
    public int insertCheckIn(CheckIn checkIn);

    /**
     * 修改入住
     * 
     * @param checkIn 入住
     * @return 结果
     */
    public int updateCheckIn(CheckIn checkIn);

    /**
     * 删除入住
     * 
     * @param id 入住主键
     * @return 结果
     */
    public int deleteCheckInById(Long id);

    /**
     * 批量删除入住
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCheckInByIds(Long[] ids);

    /**
     * 根据老人id获取checkIn信息
     * @param id
     * @return
     */
    List<CheckIn> selectCheckInByElderId(Long id);

    /**
     * 通过主键id获取备注信息（包含第三方，家属信息）
     * @param id
     * @return
     */
    String selectCheckInRemarkById(Long id);
}
