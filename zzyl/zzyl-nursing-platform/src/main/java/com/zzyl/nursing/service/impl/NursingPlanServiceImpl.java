package com.zzyl.nursing.service.impl;

import java.util.List;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.dto.NursingPlanDto;
import com.zzyl.nursing.mapper.NursingProjectMapper;
import com.zzyl.nursing.mapper.NursingProjectPlanMapper;
import com.zzyl.nursing.vo.NursingPlanVo;
import com.zzyl.nursing.vo.NursingProjectPlanVo;
import com.zzyl.nursing.vo.NursingProjectVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingPlanMapper;
import com.zzyl.nursing.domain.NursingPlan;
import com.zzyl.nursing.service.INursingPlanService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 护理计划Service业务层处理
 *
 * @author ruoyi
 * @date 2025-07-13
 */
@Service
public class NursingPlanServiceImpl extends ServiceImpl<NursingPlanMapper, NursingPlan> implements INursingPlanService
{
    @Autowired
    private NursingPlanMapper nursingPlanMapper;

    @Autowired
    private NursingProjectPlanMapper nursingProjectPlanMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String KEY = "nursingPlan:all";

    /**
     * 查询护理计划
     *
     * @param id 护理计划主键
     * @return 护理计划
     */
    @Override
    public NursingPlanVo selectNursingPlanById(Integer id)
    {
        //查询护理计划
        NursingPlan nursingPlan = nursingPlanMapper.selectNursingPlanById(id);
        NursingPlanVo nursingPlanVo = new NursingPlanVo();
        BeanUtils.copyProperties(nursingPlan,nursingPlanVo);

        //根据护理计划ID查询护理项目的关系
        List<NursingProjectPlanVo> list = nursingProjectPlanMapper.selectByPlanId(Long.valueOf(id));
        nursingPlanVo.setProjectPlans(list);

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
     * @param
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertNursingPlan(NursingPlanDto dto)
    {
        //保存护理计划
        //属性拷贝
        NursingPlan nursingPlan = new NursingPlan();
        BeanUtils.copyProperties(dto,nursingPlan);
        nursingPlan.setCreateTime(DateUtils.getNowDate());
        nursingPlanMapper.insertNursingPlan(nursingPlan);

        //批量保存护理项目计划关系
        int count = nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(), nursingPlan.getId());
        //删除缓存
        deleteCache();
        return count == 0 ? 0 : 1;
    }

    private void deleteCache(){
        //删除缓存
        redisTemplate.delete(KEY);
    }

    /**
     * 修改护理计划
     *
     * @param
     * @return 结果
     */
    @Override
    public int updateNursingPlan(NursingPlanDto dto)
    {
        try {
            //属性拷贝
            NursingPlan nursingPlan = new NursingPlan();
            BeanUtils.copyProperties(dto,nursingPlan);

            //判断dto中的项目列表为空，如果不为空，则先删除护理计划与护理项目的关系，然后重新批量添加
            if(dto.getProjectPlans() != null && dto.getProjectPlans().size() > 0){
                //删除护理计划与护理项目的关系
                nursingProjectPlanMapper.deleteByPlanId(dto.getId());
                //批量添加护理计划与护理项目的关系
                nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(), Math.toIntExact(dto.getId()));
            }
            nursingPlan.setUpdateTime(DateUtils.getNowDate());
            //别管项目列表是否为空，都要修改护理计划
            //删除缓存
            deleteCache();

            return nursingPlanMapper.updateNursingPlan(nursingPlan);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量删除护理计划
     *
     * @param id 需要删除的护理计划主键
     * @return 结果
     */
    @Override
    public int deleteNursingPlanByIds(Integer id)
    {
        try {
            //删除关系
            //删除护理计划与护理项目的关系
            nursingProjectPlanMapper.deleteByPlanId(Long.valueOf(id));
            deleteCache();
            return nursingPlanMapper.deleteNursingPlanById(id);
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
        deleteCache();
        return removeById(id) == true ? 1 : 0;
    }

    /**
     * 查询所有护理计划
     * @return
     */
    @Override
    public List<NursingPlan> listAll() {
        //先从缓存中获取
        List<NursingPlan> list = (List<NursingPlan>) redisTemplate.opsForValue().get(KEY);
        //如果缓存命中,则返回
        if(list != null && !list.isEmpty()){
            log.debug("调用redis缓存,护理等级");
            return list;
        }
        //如果缓存未命中，需要先从数据库中查询,再设置缓存
        list = nursingPlanMapper.selectNursingPlanList(new NursingPlan());
        redisTemplate.opsForValue().set(KEY,list);
        return list;
    }
}