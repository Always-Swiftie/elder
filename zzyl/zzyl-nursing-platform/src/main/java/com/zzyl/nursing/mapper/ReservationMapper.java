package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzyl.nursing.domain.Reservation;
import com.zzyl.nursing.vo.ReservationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author 20784
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

//    List<Reservation> selectByNameAndId(String name, Long userId);
//
//    List<Reservation> selectByPhoneAndId(String phone, Long userId);

    List<Reservation> selectByPhone(String phone);

    void insertOne(Reservation reservation);

    List<ReservationVo> pageQuery(Map<String, Object> prammap);

    void cancelById(Integer id);
}
