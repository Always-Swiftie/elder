package com.zzyl.nursing.service.impl;

import java.sql.Wrapper;
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
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.mapper.ReservationMapper;
import com.zzyl.nursing.service.WechatService;
import com.zzyl.nursing.vo.LoginVO;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
        //需要根据userId查到预约人的name,phone 作为唯一索引定位reservation表中的记录
        FamilyMember familyMember = familyMemberMapper.selectFamilyMemberById(userId);
        String phone = familyMember.getPhone();
        String name = familyMember.getName();
        //查询当前预约用户
        List<Reservation> reservationList = reservationMapper.selectList(Wrappers.<Reservation>lambdaQuery()
                .eq(Reservation::getName, name)
                .eq(Reservation::getPhone, phone));
        //统计状态
        if(ObjectUtils.isEmpty(reservationList)){
            throw new RuntimeException("当前用户无预约预约信息!");
        }
        Integer count = 0;
        for(Reservation reservation : reservationList){
            if(reservation.getStatus() == 2){
                count++;
            }
        }
        return count;
    }
}
