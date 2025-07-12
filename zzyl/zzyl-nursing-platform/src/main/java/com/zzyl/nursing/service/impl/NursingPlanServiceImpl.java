package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.dto.NursingPlanDto;
import com.zzyl.nursing.mapper.NursingProjectPlanMapper;
import com.zzyl.nursing.vo.NursingPlanVo;
import com.zzyl.nursing.vo.NursingProjectPlanVo;
import com.zzyl.nursing.vo.NursingProjectVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingPlanMapper;
import com.zzyl.nursing.domain.NursingPlan;
import com.zzyl.nursing.service.INursingPlanService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 护理计划Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-07-12
 */
@Service
@Transactional
public class NursingPlanServiceImpl implements INursingPlanService 
{
    @Autowired
    private NursingPlanMapper nursingPlanMapper;
    @Autowired
    private NursingProjectPlanMapper nursingProjectPlanMapper;

    /**
     * 查询护理计划
     * 
     * @param id 护理计划主键
     * @return 护理计划
     */
    @Override
    public NursingPlanVo selectNursingPlanById(Integer id)
    {
        //先查询到护理计划
        NursingPlan nursingPlan = nursingPlanMapper.selectNursingPlanById(id);
        NursingPlanVo nursingPlanVo = new NursingPlanVo();
        BeanUtils.copyProperties(nursingPlan,nursingPlanVo);
        //根据护理计划的id查询其护理项目
        List<NursingProjectPlanVo> list = nursingProjectPlanMapper.selectByPlanId(Long.valueOf(id));
        nursingPlanVo.setProjectPlans(list);
        nursingPlanVo.setId(Long.valueOf(id));
        return nursingPlanVo;
    }

    /**
     * 查询护理计划列表
     * 
     * @param nursingPlan 护理计划
     * @return 护理计划
     */
    @Override
    public List<NursingPlan> selectNursingPlanList(NursingPlan nursingPlan)
    {
        return nursingPlanMapper.selectNursingPlanList(nursingPlan);
    }

    /**
     * 新增护理计划
     * 
     * @param dto 护理计划
     * @return 结果
     */
    @Override
    public int insertNursingPlan(NursingPlanDto dto)
    {
        //保存护理计划
        NursingPlan nursingPlan = new NursingPlan();
        //属性拷贝:dto -> nursingPlan
        BeanUtils.copyProperties(dto,nursingPlan);
        nursingPlan.setCreateTime(DateUtils.getNowDate());
        nursingPlanMapper.insertNursingPlan(nursingPlan);
        //批量保存护理计划，护理项目的中间表
        int count = nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(),nursingPlan.getId());
        return count == 0 ? 0 : 1;
    }

    /**
     * 修改护理计划
     * 
     * @param dto
     * @return 结果
     */
    @Override
    public int updateNursingPlan(NursingPlanDto dto)
    {
        try {
            //属性拷贝
            NursingPlan nursingPlan = new NursingPlan();
            BeanUtils.copyProperties(dto,nursingPlan);
            //先判断dto中的项目nursingProject是否为空，如果不为空，则先删除护理计划与护理项目的关系（中间表），再批量添加
            if(dto.getProjectPlans() != null && dto.getProjectPlans().size() > 0){
                //删除护理计划与护理项目的关系
                nursingProjectPlanMapper.deleteByPlanId(dto.getId());
                //批量添加护理计划与护理项目的关系
                Integer id = dto.getId().intValue();
                nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(),id);
            }
            //不管项目列表是否为空,都需要修改护理计划
            nursingPlan.setId(dto.getId().intValue());
            return nursingPlanMapper.updateNursingPlan(nursingPlan);
        }catch (BeansException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量删除护理计划
     * 
     * @param ids 需要删除的护理计划主键
     * @return 结果
     */
    @Override
    public int deleteNursingPlanByIds(Integer[] ids)
    {
        try {
            //批量删除中间表中信息
            List<Integer> idList = Arrays.asList(ids);
            nursingProjectPlanMapper.deleteByPlanIds(idList);
            return nursingPlanMapper.deleteNursingPlanByIds(ids);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除护理计划信息
     * 
     * @param id 护理计划主键
     * @return 结果
     */
    @Override
    public int deleteNursingPlanById(Integer id)
    {
        return nursingPlanMapper.deleteNursingPlanById(id);
    }
}
