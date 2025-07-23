package com.zzyl.nursing.controller.member;

import com.github.pagehelper.PageInfo;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.nursing.dto.ReservationDto;
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.service.INursingProjectService;
import com.zzyl.nursing.vo.LoginVO;
import com.zzyl.nursing.vo.NursingProjectPageVo;
import com.zzyl.nursing.vo.ReservationVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.nursing.service.IFamilyMemberService;
import com.zzyl.common.core.page.TableDataInfo;

/**
 * 老人家属Controller
 * 
 * @author ruoyi
 * @date 2025-07-21
 */
@RestController
@RequestMapping("/member")
@Api(tags =  "老人家属相关接口")
public class FamilyMemberController extends BaseController
{
    @Autowired
    private IFamilyMemberService familyMemberService;

    @Autowired
    private INursingProjectService nursingProjectService;



    /**
     * 小程序登录
     * @param userLoginRequestDto
     * @return
     */
    @PostMapping("/user/login")
    @ApiOperation("小程序登录")
    public AjaxResult login(@RequestBody UserLoginRequestDto userLoginRequestDto){
        LoginVO loginVO = familyMemberService.login(userLoginRequestDto);
        return AjaxResult.success(loginVO);
    }

    /**
     * 分页查询护理项目列表
     * @param pageNum
     * @param pageSize
     * @param name
     * @param status
     * @return
     */
    @GetMapping("/orders/project/page")
    @ApiOperation("分页查询护理项目列表")
    public TableDataInfo pageQueryProject(
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "status", required = false) Integer status
    ){
        startPage();
        Map<String,Object> map = new HashMap<>();
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);
        if(name != null){
            map.put("name",name);
        }
        if(status != null){
            map.put("status",status);
        }
        // 封装分页元信息（PageInfo会包含 total/pageNum/pageSize 等信息）
        List<NursingProjectPageVo> list = nursingProjectService.pageQuery(map);
        // 返回符合规范的结构
        return getDataTable(list);
    }

    /**
     * 根据编号查询单个护理项目详情
     * @param id
     * @return
     */
    @GetMapping("/orders/project/{id}")
    @ApiOperation("根据编号查询护理项目信息")
    public AjaxResult getNursingProjectById(@PathVariable Long id){
        NursingProjectPageVo vo = nursingProjectService.getNursingProjectById(id);
        return success(vo);
    }

    /**
     * 查询取消预约数量
     * @param
     * @return
     */
    @GetMapping("/reservation/cancelled-count")
    @ApiOperation("查询取消预约数量")
    public AjaxResult getCancelledCount(){
        Integer cancelledCount = familyMemberService.getCancelledCount();
        return AjaxResult.success(cancelledCount);
    }

    /**
     * 新增预约
     * @param reservationDto
     * @return
     */
    @PostMapping("/reservation")
    @ApiOperation("新增预约")
    public AjaxResult addReservation(@RequestBody ReservationDto reservationDto){
        familyMemberService.addReservation(reservationDto);
        return AjaxResult.success();
    }


    @GetMapping("/reservation/page")
    @ApiOperation("分页查询预约")
    public AjaxResult pageQueryReservation(
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam(value = "status", required = false) Integer status
    ){
        startPage();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", UserThreadLocal.getUserId());
        if (status != null) {
            map.put("status", status);
        }

        List<ReservationVo> list = familyMemberService.getReservationPage(map);
        PageInfo<ReservationVo> pageInfo = new PageInfo<>(list);

        Map<String, Object> data = new HashMap<>();
        data.put("rows", pageInfo.getList());
        data.put("total", pageInfo.getTotal());

        return AjaxResult.success("查询成功", data);
    }

}
