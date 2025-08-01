package com.zzyl.nursing.service.impl;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzyl.common.constant.HttpStatus;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.ElderMapper;
import com.zzyl.nursing.domain.Elder;
import com.zzyl.nursing.service.IElderService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;

/**
 * 老人Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-07-14
 */
@Service
public class ElderServiceImpl extends ServiceImpl<ElderMapper, Elder> implements IElderService
{
    @Autowired
    private ElderMapper elderMapper;

    /**
     * 查询老人
     * 
     * @param id 老人主键
     * @return 老人
     */
    @Override
    public Elder selectElderById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询老人列表
     * 
     * @param elder 老人
     * @return 老人
     */
    @Override
    public List<Elder> selectElderList(Elder elder)
    {
        return elderMapper.selectElderList(elder);
    }

    /**
     * 新增老人
     * 
     * @param elder 老人
     * @return 结果
     */
    @Override
    public int insertElder(Elder elder)
    {

                return save(elder) == true? 1 : 0;
    }

    /**
     * 修改老人
     * 
     * @param elder 老人
     * @return 结果
     */
    @Override
    public int updateElder(Elder elder)
    {

        return updateById(elder) == true ? 1 : 0;
    }

    /**
     * 批量删除老人
     * 
     * @param ids 需要删除的老人主键
     * @return 结果
     */
    @Override
    public int deleteElderByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) == true ? 1 : 0;
    }

    /**
     * 删除老人信息
     * 
     * @param id 老人主键
     * @return 结果
     */
    @Override
    public int deleteElderById(Long id)
    {
        return removeById(id) == true ? 1 : 0;
    }

    /**
     * 老人分页查询
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public TableDataInfo pageQuery(Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Elder> queryWrapper = new LambdaQueryWrapper<Elder>();
        //创建分页对象
        Page<Elder> page = new Page<>(pageNum, pageSize);
        //按照状态查询
        if(ObjectUtil.isNotNull(status)){
            queryWrapper.eq(Elder::getStatus, status);
        }
        //sql返回结果，保留哪些字段
        queryWrapper.select(Elder::getId, Elder::getName, Elder::getIdCardNo, Elder::getBedNumber);
        //执行查询
        page = page(page, queryWrapper);
        //返回分页结果
        return getDataTable(page);
    }

    /**
     * 封装分页结果
     * @param page
     * @return
     */
    private TableDataInfo getDataTable(Page<Elder> page) {
        //封装分页结果
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setTotal(page.getTotal());
        tableDataInfo.setCode(HttpStatus.SUCCESS);
        tableDataInfo.setMsg("请求成功");
        tableDataInfo.setRows(page.getRecords());
        return tableDataInfo;
    }
}
