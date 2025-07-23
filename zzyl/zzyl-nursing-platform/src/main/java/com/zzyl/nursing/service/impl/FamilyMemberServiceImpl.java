package com.zzyl.nursing.service.impl;

import java.sql.Wrapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.framework.web.service.TokenService;
import com.zzyl.nursing.domain.Reservation;
import com.zzyl.nursing.dto.ReservationDto;
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.mapper.ReservationMapper;
import com.zzyl.nursing.service.WechatService;
import com.zzyl.nursing.vo.LoginVO;
import com.zzyl.nursing.vo.ReservationVo;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.FamilyMemberMapper;
import com.zzyl.nursing.domain.FamilyMember;
import com.zzyl.nursing.service.IFamilyMemberService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
import java.util.Map;

/**
 * 老人家属Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-07-21
 */
@Service
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper, FamilyMember> implements IFamilyMemberService
{
    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private TokenService tokenService;

    static List<String> DEFAULT_NICKNAME_PREFIX = ListUtil.of("生活更美好",
            "大桔大利",
            "日富一日",
            "好柿开花",
            "柿柿如意",
            "一椰暴富",
            "大柚所为",
            "杨梅吐气",
            "天生荔枝"
    );

    /**
     * 查询老人家属
     * 
     * @param id 老人家属主键
     * @return 老人家属
     */
    @Override
    public FamilyMember selectFamilyMemberById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询老人家属列表
     * 
     * @param familyMember 老人家属
     * @return 老人家属
     */
    @Override
    public List<FamilyMember> selectFamilyMemberList(FamilyMember familyMember)
    {
        return familyMemberMapper.selectFamilyMemberList(familyMember);
    }

    /**
     * 新增老人家属
     * 
     * @param familyMember 老人家属
     * @return 结果
     */
    @Override
    public int insertFamilyMember(FamilyMember familyMember)
    {

                return save(familyMember) == true? 1 : 0;
    }

    /**
     * 修改老人家属
     * 
     * @param familyMember 老人家属
     * @return 结果
     */
    @Override
    public int updateFamilyMember(FamilyMember familyMember)
    {

        return updateById(familyMember) == true ? 1 : 0;
    }

    /**
     * 批量删除老人家属
     * 
     * @param ids 需要删除的老人家属主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) == true ? 1 : 0;
    }

    /**
     * 删除老人家属信息
     * 
     * @param id 老人家属主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberById(Long id)
    {
        return removeById(id) == true ? 1 : 0;
    }

    /**
     * 家属小程序端登录
     * @param userLoginRequestDto
     * @return
     */
    @Override
    public LoginVO login(UserLoginRequestDto userLoginRequestDto) {
        //1.调用微信api,根据code获取openId
        String openId = wechatService.getOpenId(userLoginRequestDto.getCode());
        //2.拿到openId,查询用户
        FamilyMember familyMember = getOne(Wrappers.<FamilyMember>lambdaQuery(FamilyMember.class)
                .eq(FamilyMember::getOpenId, openId));
        //3.如果用户为空,说明为新用户，则新创建一个用户
        if(ObjectUtils.isEmpty(familyMember)){
            familyMember = FamilyMember.builder()
                    .openId(openId)
                    .build();
        }
        //4.再次调用微信api,获取当前登录用户的手机号
        String phone = wechatService.getPhone(userLoginRequestDto.getPhoneCode());
        //5.保存或修改用户
        saveOrUpdateFamilyMember(familyMember,phone);
        //6.将用户id存入token,生成token后返回
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",familyMember.getId());
        claims.put("userName",familyMember.getName());

        String token = tokenService.createToken(claims);
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setNickName(familyMember.getName());
        return loginVO;
    }

    /**
     * 保存或修改用户
     */
    private void saveOrUpdateFamilyMember(FamilyMember member,String phone){
        //1.判断获取到的手机号和数据库中保存的手机号是否一致
        if(ObjectUtil.notEqual(phone,member.getPhone())){
            member.setPhone(phone);
        }
        //2.判断id是否存在
        if(ObjectUtil.isNotEmpty(member.getId())){
            updateById(member);
            return;
        }
        //3.id不存在,新建用户
        //随机组装昵称，词组+手机号后四位
        String nickName = DEFAULT_NICKNAME_PREFIX.get((int) (Math.random() * DEFAULT_NICKNAME_PREFIX.size()))
                + StringUtils.substring(member.getPhone(), 7);
        member.setName(nickName);
        save(member);
    }

    /**
     * 获取当前用户本日预约取消数量
     * @return
     */
    @Override
    public Integer getCancelledCount() {
        Long userId = UserThreadLocal.getUserId();
        //需要根据userId查到预约人的name,Userid 作为唯一索引定位reservation表中的记录
        FamilyMember familyMember = familyMemberMapper.selectFamilyMemberById(userId);
        String phone = familyMember.getPhone();
        //查询当前预约用户(根据业务实际要求,应该把预约手机号当做唯一约束)
        List<Reservation> reservationList = reservationMapper.selectByPhone(phone);
        //统计状态
        Integer count = 0;
        for(Reservation reservation : reservationList){
            if(reservation.getStatus() == 2){
                count++;
            }
        }
        return count;
    }

    /**
     * 新增预约
     * @param reservationDto
     */
    @Override
    public void addReservation(ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        reservation.setPhone(reservationDto.getMobile());
        reservation.setName(reservationDto.getName());
        reservation.setTime(reservationDto.getTime());
        reservation.setVisitor(reservationDto.getVisitor());
        reservation.setType(reservationDto.getType());
        reservation.setCreatBy(UserThreadLocal.getUserId());
        reservation.setCreateTime(LocalDateTime.now());
        reservation.setStatus(0);
        reservationMapper.insertOne(reservation);
    }

    /**
     * 分页查询当前用户的预约记录
     * @param prammap
     * @return
     */
    @Override
    public List<ReservationVo> getReservationPage(Map<String, Object> prammap) {
        Long userId = UserThreadLocal.getUserId();
        List<ReservationVo> list = reservationMapper.pageQuery(prammap);
        return list;
    }

    /**
     * 用户取消预约
     * @param id 预约主键id
     */
    @Override
    public void cancelReservation(Integer id) {
        reservationMapper.cancelById(id);
    }

    /**
     * 定时处理过时预约
     */
    @Override
    public void handelExpireReservation() {
        //查询出所有状态为0待报道 并且预约探访时间已经过了的预约信息
        List<Reservation> list = reservationMapper.selectExpireList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //批量更新预约状态
        for(Reservation reservation : list){
            if(LocalDateTime.now().isAfter(LocalDateTime.parse( reservation.getTime(),formatter))){
                reservation.setStatus(3);
            }
            reservationMapper.updateExpireStatus(reservation);
        }
    }
}
