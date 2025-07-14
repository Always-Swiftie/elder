package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.generator.util.CodeGenerator;
import com.zzyl.nursing.domain.*;
import com.zzyl.nursing.dto.CheckInApplyDto;
import com.zzyl.nursing.dto.CheckInElderDto;
import com.zzyl.nursing.mapper.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.service.ICheckInService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 入住Service业务层处理
 * 
 * @author anthony
 * @date 2025-07-14
 */
@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements ICheckInService
{
    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private BedMapper bedMapper;

    @Autowired
    private CheckInConfigMapper checkInConfigMapper;
    /**
     * 查询入住
     * 
     * @param id 入住主键
     * @return 入住
     */
    @Override
    public CheckIn selectCheckInById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询入住列表
     * 
     * @param checkIn 入住
     * @return 入住
     */
    @Override
    public List<CheckIn> selectCheckInList(CheckIn checkIn)
    {
        return checkInMapper.selectCheckInList(checkIn);
    }

    /**
     * 新增入住
     * 
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int insertCheckIn(CheckIn checkIn)
    {

                return save(checkIn) == true? 1 : 0;
    }

    /**
     * 修改入住
     * 
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int updateCheckIn(CheckIn checkIn)
    {

        return updateById(checkIn) == true ? 1 : 0;
    }

    /**
     * 批量删除入住
     * 
     * @param ids 需要删除的入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) == true ? 1 : 0;
    }

    /**
     * 删除入住信息
     * 
     * @param id 入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInById(Long id)
    {
        return removeById(id) == true ? 1 : 0;
    }

    /**
     * 申请入住
     * @param checkInApplyDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(CheckInApplyDto checkInApplyDto) {
        //需要先判断老人是否"已入住",根据唯一id--身份证确定
        LambdaQueryWrapper<Elder> elderQueryWrapper = new LambdaQueryWrapper<>();
        elderQueryWrapper.eq(Elder::getIdCardNo,checkInApplyDto.getCheckInElderDto().getIdCardNo());
        elderQueryWrapper.eq(Elder::getStatus,1);
        Elder elder = elderMapper.selectOne(elderQueryWrapper);
        if(ObjectUtils.isNotEmpty(elder)){
            throw new BaseException("老人已经入住!");
        }
        //未入住，更新床位状态
        Bed bed = bedMapper.selectById(checkInApplyDto.getCheckInConfigDto().getBedId());
        bed.setBedStatus(1);//表面床位已启用
        bedMapper.updateById(bed);
        //保存或更新老人数据
        elder = insertOrUpdate(bed,checkInApplyDto.getCheckInElderDto());
        //生成合同编号
        String contractNo = "HT"+CodeGenerator.generateContractNumber();
        //新增签约办理
        insertContract(contractNo,elder,checkInApplyDto);
        //新增入住信息
        CheckIn checkIn = insertCheckIn(elder,checkInApplyDto);
        //新增入住配置信息
        insertCheckInConfig(checkIn.getId(),checkInApplyDto);
    }

    /**
     * 新增入住配置
     * @param checkInApplyDto
     */
    private void insertCheckInConfig(Long checkInId, CheckInApplyDto checkInApplyDto) {
        CheckInConfig checkInConfig = new CheckInConfig();
        BeanUtils.copyProperties(checkInApplyDto.getCheckInConfigDto(),checkInConfig);
        checkInConfig.setCheckInId(checkInId);
        checkInConfigMapper.insert(checkInConfig);
    }

    /**
     * 新增入住信息
     * @param elder
     * @param checkInApplyDto
     */
    private CheckIn insertCheckIn(Elder elder, CheckInApplyDto checkInApplyDto) {
        CheckIn checkIn = new CheckIn();
        checkIn.setElderId(elder.getId());
        checkIn.setElderName(elder.getName());
        checkIn.setIdCardNo(elder.getIdCardNo());
        checkIn.setNursingLevelName(checkInApplyDto.getCheckInConfigDto().getNursingLevelName());
        checkIn.setStartDate(checkInApplyDto.getCheckInConfigDto().getStartDate());
        checkIn.setEndDate(checkInApplyDto.getCheckInConfigDto().getEndDate());
        checkIn.setBedNumber(elder.getBedNumber());
        checkIn.setRemark(JSON.toJSONString(checkInApplyDto.getElderFamilyDtoList()));
        checkIn.setStatus(0);
        checkInMapper.insert(checkIn);
        return checkIn;
    }

    @Autowired
    private ContractMapper contractMapper;

    /**
     * 新增合同
     * @param contractNo
     * @param elder
     * @param checkInApplyDto
     */
    private void insertContract(String contractNo,Elder elder, CheckInApplyDto checkInApplyDto) {

        Contract contract = new Contract();
        //属性拷贝
        BeanUtils.copyProperties(checkInApplyDto.getCheckInContractDto(),contract);
        contract.setContractNumber(contractNo);
        contract.setElderId(elder.getId());
        contract.setElderName(elder.getName());
        //状态、开始时间、结束时间
        //签约时间小于等于当前时间，合同生效中
        LocalDateTime checkInStartTime = checkInApplyDto.getCheckInConfigDto().getStartDate();
        LocalDateTime checkInEndTime = checkInApplyDto.getCheckInConfigDto().getEndDate();
        Integer status = checkInStartTime.isAfter(LocalDateTime.now()) ? 1 : 0;
        contract.setStatus(status);
        contract.setStartDate(checkInStartTime);
        contract.setEndDate(checkInEndTime);
        contractMapper.insert(contract);
    }

    /**
     * 新增或更新老人
     * @param bed
     * @param checkInElderDto
     * @return
     */
    private Elder insertOrUpdate(Bed bed, CheckInElderDto checkInElderDto) {

        //准备老人数据
        Elder elder = new Elder();
        //属性拷贝
        BeanUtils.copyProperties(checkInElderDto,elder);
        elder.setBedNumber(bed.getBedNumber());
        elder.setBedId(bed.getId());
        elder.setStatus(1);
        //查询老人信息，（身份证号和状态  0）
        LambdaQueryWrapper<Elder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Elder::getIdCardNo,checkInElderDto.getIdCardNo()).eq(Elder::getStatus,0);
        Elder elderDb =  elderMapper.selectOne(lambdaQueryWrapper);
        if(ObjectUtils.isNotEmpty(elderDb)){
            //修改
            elderMapper.updateById(elder);
        }else {
            //新增
            elderMapper.insert(elder);
        }
        return elder;
    }
}
