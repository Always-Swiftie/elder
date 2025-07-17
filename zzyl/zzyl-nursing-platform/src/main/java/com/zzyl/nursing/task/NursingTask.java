package com.zzyl.nursing.task;


import com.zzyl.nursing.domain.Contract;
import com.zzyl.nursing.mapper.ContractMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 护理模块定时任务类
 */
@Component
@Slf4j
public class NursingTask {

    @Autowired
    private ContractMapper contractMapper;

    /**
     * 每天01:00检查合同状态是否有效
     * 判断标准：合同开始时间是否大于等于当前时间
     * 要执行的操作:对于检查到合同开始时间晚于本日的，判定为合同未生效;开始时间早于本日，且结束日期晚于今日的，判定为生效中;结束时间早于今日的，判断为失效
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processContractStatus(){
        log.debug("处理合同状态");
        //获取今天日期
        LocalDate now = LocalDate.now();
        //获取所有生效日期在今日之后的合同
        List<Contract> invalidContracts = contractMapper.selectByTimeAfter(now);
        for (Contract contract : invalidContracts) {
            if(contract.getStartDate().isEqual(LocalDateTime.now().minusMinutes(60))){
                //今日启用的合同
                contract.setStatus(1);
            }else{
                //否则还没到启用日期,设置为未启用
                contract.setStatus(0);
            }
        }
        //获取所有生效日期在今日之前的合同
        invalidContracts = contractMapper.selectByTimeBefore(now);
        for (Contract contract : invalidContracts) {
            //检查结束日期
            if(contract.getEndDate().isEqual(LocalDateTime.now().minusMinutes(60))){
                contract.setStatus(2);
            }
        }

    }

}
