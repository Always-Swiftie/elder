package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;
import com.zzyl.nursing.domain.Contract;

/**
 * 合同Mapper接口
 * 
 * @author ruoyi
 * @date 2025-07-14
 */
@Mapper

public interface ContractMapper extends BaseMapper<Contract>
{
    /**
     * 查询合同
     * 
     * @param id 合同主键
     * @return 合同
     */
    public Contract selectContractById(Long id);

    /**
     * 查询合同列表
     * 
     * @param contract 合同
     * @return 合同集合
     */
    public List<Contract> selectContractList(Contract contract);

    /**
     * 新增合同
     * 
     * @param contract 合同
     * @return 结果
     */
    public int insertContract(Contract contract);

    /**
     * 修改合同
     * 
     * @param contract 合同
     * @return 结果
     */
    public int updateContract(Contract contract);

    /**
     * 删除合同
     * 
     * @param id 合同主键
     * @return 结果
     */
    public int deleteContractById(Long id);

    /**
     * 批量删除合同
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteContractByIds(Long[] ids);

    /**
     * 根据老人ID查询合同信息
     * @return
     */
    Contract selectContractByElderId(Long id);

    /**
     * 获取所有生效日期在今日之后的合同
     * @param now
     * @return
     */
    List<Contract> selectByTimeAfter(LocalDate now);

    /**
     * 获取所有生效日期在今日之前的合同
     * @param now
     * @return
     */
    List<Contract> selectByTimeBefore(LocalDate now);
}
