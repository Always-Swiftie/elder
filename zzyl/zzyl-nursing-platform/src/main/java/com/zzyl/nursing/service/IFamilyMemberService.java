package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

import com.zzyl.nursing.domain.FamilyMember;
import com.zzyl.nursing.dto.ReservationDto;
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.vo.LoginVO;
import com.zzyl.nursing.vo.ReservationVo;

/**
 * 老人家属Service接口
 * 
 * @author ruoyi
 * @date 2025-07-21
 */
public interface IFamilyMemberService extends IService<FamilyMember>
{
    /**
     * 查询老人家属
     * 
     * @param id 老人家属主键
     * @return 老人家属
     */
    public FamilyMember selectFamilyMemberById(Long id);

    /**
     * 查询老人家属列表
     * 
     * @param familyMember 老人家属
     * @return 老人家属集合
     */
    public List<FamilyMember> selectFamilyMemberList(FamilyMember familyMember);

    /**
     * 新增老人家属
     * 
     * @param familyMember 老人家属
     * @return 结果
     */
    public int insertFamilyMember(FamilyMember familyMember);

    /**
     * 修改老人家属
     * 
     * @param familyMember 老人家属
     * @return 结果
     */
    public int updateFamilyMember(FamilyMember familyMember);

    /**
     * 批量删除老人家属
     * 
     * @param ids 需要删除的老人家属主键集合
     * @return 结果
     */
    public int deleteFamilyMemberByIds(Long[] ids);

    /**
     * 删除老人家属信息
     * 
     * @param id 老人家属主键
     * @return 结果
     */
    public int deleteFamilyMemberById(Long id);

    /**
     * 微信登录
     * @param userLoginRequestDto
     * @return
     */
    LoginVO login(UserLoginRequestDto userLoginRequestDto);

    /**
     * 获取当前用户本日预约取消数量
     * @return
     */
    Integer getCancelledCount();

    /**
     * 新增预约
     * @param reservationDto
     */
    void addReservation(ReservationDto reservationDto);

    /**
     * 查询当前用户的预约列表
     * @return
     */
    List<ReservationVo> getReservationPage(Map<String, Object> prammap);

    /**
     * 用户取消预约
     * @param id
     */
    void cancelReservation(Integer id);

    /**
     * 定时处理过时预约
     */
    void handelExpireReservation();
}
