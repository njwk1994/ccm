package ccm.server.controller;

import ccm.server.business.ICCMP6HandleService;
import ccm.server.module.vo.ResultVo;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/9/9 17:21
 */
@Slf4j
@RestController
@Api(tags = "P6接口相关")
@RequestMapping("/ccm/P6")
public class CCMP6HandleController {

    @Autowired
    private ICCMP6HandleService p6HandleService;

    @ApiOperation(value = "同步P6计划", notes = "同步P6计划")
    @PostMapping("/syncSchedule")
    public String syncSchedule(@RequestBody JSONObject requestBody) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            p6HandleService.syncSchedule();
            result.successResult(true);
        } catch (Exception exception) {
            result.errorResult("获取计划管理表单失败," + ExceptionUtil.getSimpleMessage(exception));
        }
        return JSON.toJSONString(result);
    }

}
