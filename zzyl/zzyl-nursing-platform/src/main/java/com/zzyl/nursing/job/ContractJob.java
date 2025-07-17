package com.zzyl.nursing.job;

import com.zzyl.nursing.service.IContractService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("contractJob")
@Slf4j
public class ContractJob {

    @Autowired
    private IContractService contractService;

    public void updateContractStatusJob(){
        contractService.updateContractStatus();
        log.info("定时更新合同状态成功！");
    }
}
