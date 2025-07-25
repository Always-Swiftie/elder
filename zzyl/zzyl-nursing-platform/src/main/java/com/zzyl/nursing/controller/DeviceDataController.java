package com.zzyl.nursing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zzyl.common.annotation.Log;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.enums.BusinessType;
import com.zzyl.nursing.domain.DeviceData;
import com.zzyl.nursing.service.IDeviceDataService;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.common.core.page.TableDataInfo;

/**
 * 设备数据Controller
 * 
 * @author ruoyi
 * @date 2025-07-25
 */
@RestController
@RequestMapping("/nursing/data")
@Api(tags =  "设备数据相关接口")
public class DeviceDataController extends BaseController
{
    @Autowired
    private IDeviceDataService deviceDataService;

    /**
     * 查询设备数据列表
     */
    @PreAuthorize("@ss.hasPermi('nursing:data:list')")
    @GetMapping("/list")
    @ApiOperation("查询设备数据列表")
    public TableDataInfo list(DeviceData deviceData)
    {
        startPage();
        List<DeviceData> list = deviceDataService.selectDeviceDataList(deviceData);
        return getDataTable(list);
    }

    /**
     * 导出设备数据列表
     */
    @PreAuthorize("@ss.hasPermi('nursing:data:export')")
    @Log(title = "设备数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出设备数据列表")
    public void export(HttpServletResponse response, DeviceData deviceData)
    {
        List<DeviceData> list = deviceDataService.selectDeviceDataList(deviceData);
        ExcelUtil<DeviceData> util = new ExcelUtil<DeviceData>(DeviceData.class);
        util.exportExcel(response, list, "设备数据数据");
    }

    /**
     * 获取设备数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('nursing:data:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取设备数据详细信息")
    public AjaxResult getInfo(@ApiParam(value = "设备数据ID", required = true)
            @PathVariable("id") Long id)
    {
        return success(deviceDataService.selectDeviceDataById(id));
    }

    /**
     * 新增设备数据
     */
    @PreAuthorize("@ss.hasPermi('nursing:data:add')")
    @Log(title = "设备数据", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增设备数据")
    public AjaxResult add(@ApiParam(value = "设备数据实体", required = true) @RequestBody DeviceData deviceData)
    {
        return toAjax(deviceDataService.insertDeviceData(deviceData));
    }

    /**
     * 修改设备数据
     */
    @PreAuthorize("@ss.hasPermi('nursing:data:edit')")
    @Log(title = "设备数据", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改设备数据")
    public AjaxResult edit(@ApiParam(value = "设备数据实体", required = true)  @RequestBody DeviceData deviceData)
    {
        return toAjax(deviceDataService.updateDeviceData(deviceData));
    }

    /**
     * 删除设备数据
     */
    @PreAuthorize("@ss.hasPermi('nursing:data:remove')")
    @Log(title = "设备数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除设备数据")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(deviceDataService.deleteDeviceDataByIds(ids));
    }
}
