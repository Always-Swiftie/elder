package com.zzyl.nursing.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.generator.util.CodeGenerator;
import com.zzyl.nursing.domain.*;
import com.zzyl.nursing.dto.CheckInApplyDto;
import com.zzyl.nursing.dto.CheckInElderDto;
import com.zzyl.nursing.mapper.*;
import com.zzyl.nursing.vo.CheckInConfigVo;
import com.zzyl.nursing.vo.CheckInDetailVo;
import com.zzyl.nursing.vo.CheckInElderVo;
import com.zzyl.nursing.vo.ElderFamilyVo;
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

    /**
     * 获取入住信息详情
     * @param id -- 入住信息的主键id
     * @return
     */
    @Override
    public CheckInDetailVo getCheckInDetailById(Long id) throws JsonProcessingException {
        //STEP1.根据id先拿到CheckIn对象
        CheckIn checkIn = checkInMapper.selectById(id);
        CheckInConfigVo checkInConfigVo = new CheckInConfigVo();
        BeanUtils.copyProperties(checkIn,checkInConfigVo);//完成属性赋值
        CheckInConfig checkInConfig = checkInConfigMapper.selectById(id);
        checkInConfigVo.setNursingLevelId(checkInConfig.getNursingLevelId());
        checkInConfigVo.setCheckInId(id);
        checkInConfigVo.setFeeStartDate(checkInConfig.getFeeStartDate());
        checkInConfigVo.setFeeEndDate(checkInConfig.getFeeEndDate());
        checkInConfigVo.setDeposit(checkInConfig.getDeposit());
        checkInConfigVo.setInsurancePayment(checkInConfig.getInsurancePayment());
        checkInConfigVo.setGovernmentSubsidy(checkInConfig.getGovernmentSubsidy());
        checkInConfigVo.setNursingFee(checkInConfig.getNursingFee());
        checkInConfigVo.setOtherFees(checkInConfig.getOtherFees());
        checkInConfigVo.setBedFee(checkInConfig.getBedFee());
        //STEP2.通过checkIn对象拿到老人的elder_id,进而获取elder对象
        Long elderId = checkIn.getElderId();
        Elder elder = elderMapper.selectById(elderId);
        CheckInElderVo checkInElderVo = new CheckInElderVo();
        BeanUtils.copyProperties(elder,checkInElderVo);//还差age属性需要自己计算得到
        String birthYear = elder.getBirthday().substring(0,4);
        Integer age = LocalDate.now().getYear() - Integer.parseInt(birthYear);
        checkInElderVo.setAge(age);

        //STEP3.获取Contract对象--通过主键id拿到即可
        Contract contract = contractMapper.selectContractById(id);

        //STEP4.获取ElderFamily对象
        String remark = checkInMapper.selectCheckInRemarkById(id);
        log.error(remark);
        ObjectMapper objectMapper = new ObjectMapper();
        List<ElderFamilyVo> list = objectMapper.readValue(remark, new TypeReference<List<ElderFamilyVo>>() {});
        //STEP5.属性获取完成,赋值
        CheckInDetailVo checkInDetailVo = CheckInDetailVo
                .builder()
                .elderFamilyVoList(list)
                .checkInConfigVo(checkInConfigVo)
                .checkInElderVo(checkInElderVo)
                .contract(contract)
                .build();

        return checkInDetailVo;
    }
}
