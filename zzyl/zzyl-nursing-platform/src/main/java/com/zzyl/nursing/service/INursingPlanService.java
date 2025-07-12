package com.zzyl.nursing.service;

import java.util.List;
import com.zzyl.nursing.domain.NursingPlan;
import com.zzyl.nursing.dto.NursingPlanDto;
import com.zzyl.nursing.vo.NursingPlanVo;

/**
 * 护理计划Service接口
 * 
 * @author ruoyi
 * @date 2025-07-12
 */
public interface INursingPlanService 
{
    /**
     * 查询护理计划
     * 
     * @param id 护理计划主键
     * @return 护理计划
     */
    public NursingPlanVo selectNursingPlanById(Integer id);

    /**
     * 查询护理计划列表
     * 
     * @param nursingPlan 护理计划
     * @return 护理计划集合
     */
    public List<NursingPlan> selectNursingPlanList(NursingPlan nursingPlan);

    /**
     * 新增护理计划
     * 
     * @param dto 护理计划
     * @return 结果
     */
    public int insertNursingPlan(NursingPlanDto dto);

    /**
     * 修改护理计划
     * 
     * @param dto
     * @return 结果
     */
    public int updateNursingPlan(NursingPlanDto dto);

    /**
     * 批量删除护理计划
     * 
     * @param ids 需要删除的护理计划主键集合
     * @return 结果
     */
    public int deleteNursingPlanByIds(Integer[] ids);

    /**
     * 删除护理计划信息
     * 
     * @param id 护理计划主键
     * @return 结果
     */
    public int deleteNursingPlanById(Integer id);
}
