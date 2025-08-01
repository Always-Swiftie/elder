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
import com.zzyl.nursing.domain.Elder;
import com.zzyl.nursing.service.IElderService;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.common.core.page.TableDataInfo;

/**
 * 老人Controller
 * 
 * @author ruoyi
 * @date 2025-07-14
 */
@RestController
@RequestMapping("/nursing/elder")
@Api(tags =  "老人相关接口")
public class ElderController extends BaseController
{
    @Autowired
    private IElderService elderService;

    /**
     * 查询老人列表
     */
    @PreAuthorize("@ss.hasPermi('nursing:elder:list')")
    @GetMapping("/list")
    @ApiOperation("查询老人列表")
    public TableDataInfo list(Elder elder)
    {
        startPage();
        List<Elder> list = elderService.selectElderList(elder);
        return getDataTable(list);
    }

    /**
     * 导出老人列表
     */
    @PreAuthorize("@ss.hasPermi('nursing:elder:export')")
    @Log(title = "老人", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出老人列表")
    public void export(HttpServletResponse response, Elder elder)
    {
        List<Elder> list = elderService.selectElderList(elder);
        ExcelUtil<Elder> util = new ExcelUtil<Elder>(Elder.class);
        util.exportExcel(response, list, "老人数据");
    }

    /**
     * 获取老人详细信息
     */
    @PreAuthorize("@ss.hasPermi('nursing:elder:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取老人详细信息")
    public AjaxResult getInfo(@ApiParam(value = "老人ID", required = true)
            @PathVariable("id") Long id)
    {
        return success(elderService.selectElderById(id));
    }

    /**
     * 新增老人
     */
    @PreAuthorize("@ss.hasPermi('nursing:elder:add')")
    @Log(title = "老人", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增老人")
    public AjaxResult add(@ApiParam(value = "老人实体", required = true) @RequestBody Elder elder)
    {
        return toAjax(elderService.insertElder(elder));
    }

    /**
     * 修改老人
     */
    @PreAuthorize("@ss.hasPermi('nursing:elder:edit')")
    @Log(title = "老人", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改老人")
    public AjaxResult edit(@ApiParam(value = "老人实体", required = true)  @RequestBody Elder elder)
    {
        return toAjax(elderService.updateElder(elder));
    }

    /**
     * 删除老人
     */
    @PreAuthorize("@ss.hasPermi('nursing:elder:remove')")
    @Log(title = "老人", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除老人")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(elderService.deleteElderByIds(ids));
    }

    /**
     * 老人分页查询
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/pageQuery")
    @ApiOperation(value = "老人分页查询")
    public TableDataInfo pageQuery(Integer pageSize, Integer pageNum, Integer status) {
        return  elderService.pageQuery(status, pageNum, pageSize);
    }
}
