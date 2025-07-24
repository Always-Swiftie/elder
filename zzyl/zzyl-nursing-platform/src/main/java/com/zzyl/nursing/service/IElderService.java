package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.Elder;

/**
 * 老人Service接口
 * 
 * @author ruoyi
 * @date 2025-07-14
 */
public interface IElderService extends IService<Elder>
{
    /**
     * 查询老人
     * 
     * @param id 老人主键
     * @return 老人
     */
    public Elder selectElderById(Long id);

    /**
     * 查询老人列表
     * 
     * @param elder 老人
     * @return 老人集合
     */
    public List<Elder> selectElderList(Elder elder);

    /**
     * 新增老人
     * 
     * @param elder 老人
     * @return 结果
     */
    public int insertElder(Elder elder);

    /**
     * 修改老人
     * 
     * @param elder 老人
     * @return 结果
     */
    public int updateElder(Elder elder);

    /**
     * 批量删除老人
     * 
     * @param ids 需要删除的老人主键集合
     * @return 结果
     */
    public int deleteElderByIds(Long[] ids);

    /**
     * 删除老人信息
     * 
     * @param id 老人主键
     * @return 结果
     */
    public int deleteElderById(Long id);

    /**
     * 老人分页查询
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    TableDataInfo pageQuery(Integer status, Integer pageNum, Integer pageSize);
}
