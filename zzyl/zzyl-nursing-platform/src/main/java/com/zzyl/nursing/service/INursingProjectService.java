package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.dto.NursingProjectPageDto;
import com.zzyl.nursing.vo.NursingProjectPageVo;
import com.zzyl.nursing.vo.NursingProjectVo;

/**
 * 护理项目Service接口
 *
 * @author ruoyi
 * @date 2025-07-13
 */
public interface INursingProjectService extends IService<NursingProject>
{
    /**
     * 查询护理项目
     *
     * @param id 护理项目主键
     * @return 护理项目
     */
    public NursingProject selectNursingProjectById(Long id);

    /**
     * 查询护理项目列表
     *
     * @param nursingProject 护理项目
     * @return 护理项目集合
     */
    public List<NursingProject> selectNursingProjectList(NursingProject nursingProject);

    /**
     * 新增护理项目
     *
     * @param nursingProject 护理项目
     * @return 结果
     */
    public int insertNursingProject(NursingProject nursingProject);

    /**
     * 修改护理项目
     *
     * @param nursingProject 护理项目
     * @return 结果
     */
    public int updateNursingProject(NursingProject nursingProject);

    /**
     * 批量删除护理项目
     *
     * @param ids 需要删除的护理项目主键集合
     * @return 结果
     */
    public int deleteNursingProjectByIds(Long[] ids);

    /**
     * 删除护理项目信息
     *
     * @param id 护理项目主键
     * @return 结果
     */
    public int deleteNursingProjectById(Long id);

    /**
     * 查询所有护理项目
     * @return
     */
    List<NursingProjectVo> selectAll();

    /**
     * 用户端分页查询护理项目
     * @param
     * @return
     */
    List<NursingProjectPageVo> pageQuery(Map<String, Object> params);

    /**
     * 根据id查询单个护理项目信息(用户端)
     * @param id
     * @return
     */
    NursingProjectPageVo getNursingProjectById(Long id);
}
