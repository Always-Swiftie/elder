package com.zzyl.nursing.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.AlertRuleMapper;
import com.zzyl.nursing.domain.AlertRule;
import com.zzyl.nursing.service.IAlertRuleService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;

/**
 * 报警规则Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-07-25
 */
@Service
public class AlertRuleServiceImpl extends ServiceImpl<AlertRuleMapper, AlertRule> implements IAlertRuleService
{
    @Autowired
    private AlertRuleMapper alertRuleMapper;

    /**
     * 查询报警规则
     * 
     * @param id 报警规则主键
     * @return 报警规则
     */
    @Override
    public AlertRule selectAlertRuleById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询报警规则列表
     * 
     * @param alertRule 报警规则
     * @return 报警规则
     */
    @Override
    public List<AlertRule> selectAlertRuleList(AlertRule alertRule)
    {
        return alertRuleMapper.selectAlertRuleList(alertRule);
    }

    /**
     * 新增报警规则
     * 
     * @param alertRule 报警规则
     * @return 结果
     */
    @Override
    public int insertAlertRule(AlertRule alertRule)
    {

                return save(alertRule) == true? 1 : 0;
    }

    /**
     * 修改报警规则
     * 
     * @param alertRule 报警规则
     * @return 结果
     */
    @Override
    public int updateAlertRule(AlertRule alertRule)
    {

        return updateById(alertRule) == true ? 1 : 0;
    }

    /**
     * 批量删除报警规则
     * 
     * @param ids 需要删除的报警规则主键
     * @return 结果
     */
    @Override
    public int deleteAlertRuleByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) == true ? 1 : 0;
    }

    /**
     * 删除报警规则信息
     * 
     * @param id 报警规则主键
     * @return 结果
     */
    @Override
    public int deleteAlertRuleById(Long id)
    {
        return removeById(id) == true ? 1 : 0;
    }
}
