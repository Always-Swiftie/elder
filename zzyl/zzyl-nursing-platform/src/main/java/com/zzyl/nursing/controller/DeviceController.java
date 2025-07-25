package com.zzyl.nursing.controller;

import com.zzyl.nursing.dto.DeviceDataDto;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.dto.DeviceInfoQueryDto;
import com.zzyl.nursing.vo.DeviceInfo;
import io.swagger.annotations.*;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.zzyl.common.annotation.Log;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.enums.BusinessType;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.service.IDeviceService;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.common.core.page.TableDataInfo;

/**
 * DeviceController
 * 
 * @author ruoyi
 * @date 2025-07-24
 */
@RestController
@RequestMapping("/nursing/device")
@Api(tags =  "智能设备相关接口")
public class DeviceController extends BaseController
{
    @Autowired
    private IDeviceService deviceService;

    /**
     * 查询Device列表
     */
    @PreAuthorize("@ss.hasPermi('nursing:device:list')")
    @GetMapping("/list")
    @ApiOperation("查询Device列表")
    public TableDataInfo list(Device device)
    {
        startPage();
        List<Device> list = deviceService.selectDeviceList(device);
        return getDataTable(list);
    }

    /**
     * 同步产品列表
     * @return
     */
    @PostMapping("/syncProductList")
    @ApiOperation(value = "从物联网平台同步产品列表")
    public AjaxResult syncProductList() {
        deviceService.syncProductList();
        return success();
    }

    /**
     * 查询所有产品列表
     * @return
     */
    @GetMapping("/allProduct")
    @ApiOperation(value = "查询所有产品列表")
    public AjaxResult allProduct() {
        return success(deviceService.allProduct());
    }

    /**
     * 注册设备
     * @param deviceDto
     * @return
     */
    @PostMapping("/register")
    @ApiOperation(value = "注册设备")
    public AjaxResult registerDevice(@RequestBody DeviceDto deviceDto){
        deviceService.registerDevice(deviceDto);
        return success();
    }

    /**
     * 获取设备详细信息
     */
    @PostMapping("/queryDeviceDetail")
    @ApiOperation("获取设备详细信息")
    public AjaxResult getInfo(@RequestBody DeviceInfoQueryDto  deviceInfoQueryDto) {
        String iotId = deviceInfoQueryDto.getIotId();
        return success(deviceService.queryDeviceDetail(iotId));
    }

    /**
     * 查询设备上报数据
     */
    @PostMapping("/queryThingModelPublished")
    @ApiOperation("查询设备上报数据")
    public AjaxResult queryServiceProperties(@RequestBody DeviceDataDto dto) {
        String iotId = dto.getIotId();
        AjaxResult ajaxResult = deviceService.queryServiceProperties(iotId);
        return ajaxResult;
    }

    /**
     * 修改设备
     */
    @PutMapping
    @ApiOperation(value = "修改设备")
    public AjaxResult updateDevice(@RequestBody DeviceDto deviceDto){
        deviceService.updateDevice(deviceDto);
        return success();
    }





}
