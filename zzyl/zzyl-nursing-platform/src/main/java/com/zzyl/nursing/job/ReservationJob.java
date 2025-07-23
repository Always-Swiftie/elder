package com.zzyl.nursing.job;

import com.zzyl.nursing.service.IFamilyMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 20784
 * 预约信息定时任务
 */
@Component("reservationJob")
@Slf4j
public class ReservationJob {

    @Autowired
    private IFamilyMemberService familyMemberService;

    /**
     * 定时任务:每小时的01分/31分自动检查是否有过时(预约参访时间已过)的预约,批量更新为已过期
     */
    @Scheduled(cron = "0 01,31 * * * *")
    public void checkExpireReservation(){
        log.info("定时任务执行:检查过时预约...");
        familyMemberService.handelExpireReservation();
    }


}
