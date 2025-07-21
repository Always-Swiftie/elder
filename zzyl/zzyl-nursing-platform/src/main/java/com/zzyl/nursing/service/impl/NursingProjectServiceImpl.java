package com.zzyl.nursing.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.dto.NursingProjectPageDto;
import com.zzyl.nursing.vo.NursingProjectPageVo;
import com.zzyl.nursing.vo.NursingProjectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingProjectMapper;
import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.service.INursingProjectService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
import java.util.Map;

/**
 * 护理项目Service业务层处理
 *
 * @author ruoyi
 * @date 2025-07-13
 */
@Service
public class NursingProjectServiceImpl extends ServiceImpl<NursingProjectMapper, NursingProject> implements INursingProjectService
{
    @Autowired
    private NursingProjectMapper nursingProjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String KEY = "nursingProject:all";

    private static final String USER_KEY = "nursingProject:user:all";

    /**
     * 查询护理项目
     *
     * @param id 护理项目主键
     * @return 护理项目
     */
    @Override
    public NursingProject selectNursingProjectById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询护理项目列表
     *
     * @param nursingProject 护理项目
     * @return 护理项目
     */
    @Override
    public List<NursingProject> selectNursingProjectList(NursingProject nursingProject)
    {
        return nursingProjectMapper.selectNursingProjectList(nursingProject);
    }

    /**
     * 新增护理项目
     *
     * @param nursingProject 护理项目
     * @return 结果
     */
    @Override
    public int insertNursingProject(NursingProject nursingProject)
    {
        //删除缓存
        deleteCache();
        return save(nursingProject) == true? 1 : 0;
    }

    private void deleteCache(){
        redisTemplate.delete(KEY);
        redisTemplate.delete(USER_KEY);
    }

    /**
     * 修改护理项目
     *
     * @param nursingProject 护理项目
     * @return 结果
     */
    @Override
    public int updateNursingProject(NursingProject nursingProject)
    {
        deleteCache();
        return updateById(nursingProject) == true ? 1 : 0;
    }

    /**
     * 批量删除护理项目
     *
     * @param ids 需要删除的护理项目主键
     * @return 结果
     */
    @Override
    public int deleteNursingProjectByIds(Long[] ids)
    {
        deleteCache();
        return removeByIds(Arrays.asList(ids)) == true ? 1 : 0;
    }

    /**
     * 删除护理项目信息
     *
     * @param id 护理项目主键
     * @return 结果
     */
    @Override
    public int deleteNursingProjectById(Long id)
    {
        deleteCache();
        return removeById(id) == true ? 1 : 0;
    }

    /**
     * 查询所有护理项目
     *
     * @return
     */
    @Override
    public List<NursingProjectVo> selectAll() {
        //先从缓存中获取
        List<NursingProjectVo> list = (List<NursingProjectVo>) redisTemplate.opsForValue().get(KEY);
        //如果成功命中缓存
        if(list != null && !list.isEmpty()){
            log.debug("调用redis缓存,护理项目");
            return list;
        }
        //没有命中缓存,先从数据库查
        list = nursingProjectMapper.selectAll();
        redisTemplate.opsForValue().set(KEY, list);
        return list;
    }

    /**
     * 用户端分页查询护理项目
     * @param params
     * @return
     */
    @Override
    public List<NursingProjectPageVo> pageQuery(Map<String, Object> params) {
        //先从缓存中获取
        List<NursingProjectPageVo> list = (List<NursingProjectPageVo>) redisTemplate.opsForValue().get(USER_KEY);
        //如果成功命中缓存
        if(list != null && !list.isEmpty()){
            log.debug("调用redis缓存");
            return list;
        }
        //没有命中缓存,先从数据库中查
        list = nursingProjectMapper.pageQuery(params);
        redisTemplate.opsForValue().set(USER_KEY, list);
        return list;
    }

    /**
     * 根据id查询单个护理项目信息
     * @param id
     * @return
     */
    @Override
    public NursingProjectPageVo getNursingProjectById(Long id) {
        NursingProjectPageVo vo = nursingProjectMapper.getNursingProjectVoById(id);
        return vo;
    }
}
