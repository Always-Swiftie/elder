package com.zzyl.nursing.controller;

import com.zzyl.common.utils.PDFUtil;
import com.zzyl.common.utils.file.FileUtils;
import com.zzyl.nursing.dto.HealthAssessmentDto;
import com.zzyl.oss.client.OSSAliyunFileStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
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
import com.zzyl.nursing.domain.HealthAssessment;
import com.zzyl.nursing.service.IHealthAssessmentService;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 健康评估Controller
 * 
 * @author ruoyi
 * @date 2025-07-18
 */
@RestController
@RequestMapping("/nursing/healthAssessment")
@Api(tags =  "健康评估相关接口")
public class HealthAssessmentController extends BaseController
{
    @Autowired
    private IHealthAssessmentService healthAssessmentService;

    @Autowired
    private OSSAliyunFileStorageService fileStorageService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 通用上传请求（单个）
     */
    @ApiOperation("健康文档上传")
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file,String idCardNo) throws IOException{
        try{
            //生成一个名字，保证不重复，唯一
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
            String url = fileStorageService.store(fileName, file.getInputStream());

            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", url);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            //PDF文件内容读取为字符串
            String content = PDFUtil.pdfToString(file.getInputStream());
            //临时存储到redis中
            redisTemplate.opsForHash().put("healthReport", idCardNo, content);

            return ajax;
        }catch (Exception e){
            return AjaxResult.error(e.getMessage());
        }

    }

    /**
     * 查询健康评估列表
     */
    @PreAuthorize("@ss.hasPermi('nursing:healthAssessment:list')")
    @GetMapping("/list")
    @ApiOperation("查询健康评估列表")
    public TableDataInfo list(HealthAssessment healthAssessment)
    {
        startPage();
        List<HealthAssessment> list = healthAssessmentService.selectHealthAssessmentList(healthAssessment);
        return getDataTable(list);
    }

    /**
     * 导出健康评估列表
     */
    @PreAuthorize("@ss.hasPermi('nursing:healthAssessment:export')")
    @Log(title = "健康评估", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出健康评估列表")
    public void export(HttpServletResponse response, HealthAssessment healthAssessment)
    {
        List<HealthAssessment> list = healthAssessmentService.selectHealthAssessmentList(healthAssessment);
        ExcelUtil<HealthAssessment> util = new ExcelUtil<HealthAssessment>(HealthAssessment.class);
        util.exportExcel(response, list, "健康评估数据");
    }

    /**
     * 获取健康评估详细信息
     */
    @PreAuthorize("@ss.hasPermi('nursing:healthAssessment:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取健康评估详细信息")
    public AjaxResult getInfo(@ApiParam(value = "健康评估ID", required = true)
            @PathVariable("id") Long id)
    {
        return success(healthAssessmentService.selectHealthAssessmentById(id));
    }

    /**
     * 新增健康评估
     */
    @PreAuthorize("@ss.hasPermi('nursing:healthAssessment:add')")
    @Log(title = "健康评估", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增健康评估")
    public AjaxResult add(@ApiParam(value = "健康评估实体")
                          @RequestBody HealthAssessmentDto healthAssessmentDto)
    {
        Long id = healthAssessmentService.insertHealthAssessment(healthAssessmentDto);
        return success(id);
    }

    /**
     * 修改健康评估
     */
    @PreAuthorize("@ss.hasPermi('nursing:healthAssessment:edit')")
    @Log(title = "健康评估", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改健康评估")
    public AjaxResult edit(@ApiParam(value = "健康评估实体", required = true)  @RequestBody HealthAssessment healthAssessment)
    {
        return toAjax(healthAssessmentService.updateHealthAssessment(healthAssessment));
    }

    /**
     * 删除健康评估
     */
    @PreAuthorize("@ss.hasPermi('nursing:healthAssessment:remove')")
    @Log(title = "健康评估", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除健康评估")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(healthAssessmentService.deleteHealthAssessmentByIds(ids));
    }
}
