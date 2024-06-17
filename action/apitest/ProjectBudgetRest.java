package com.epoint.xmcg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epoint.basic.api.common.ApiBaseController;
import com.epoint.core.utils.json.JsonUtil;
import com.epoint.frame.service.metadata.code.api.ICodeItemsService;
import com.epoint.xmcg.projectbudgetinfo.api.IProjectbudgetinfoService;
import com.epoint.xmcg.projectbudgetinfo.api.entity.Projectbudgetinfo;

@RestController
@RequestMapping("/getbudgetinfo")
public class ProjectBudgetRest extends ApiBaseController
{

    @Autowired
    private IProjectbudgetinfoService budgetService;

    @Autowired
    private ICodeItemsService itemService;

    // http://localhost:8089/epoint-web/rest/getbudgetinfo/getbudgetbyprojectname
    @RequestMapping(value = "/getbudgetbyprojectname", method = RequestMethod.POST)
    public String getInfoByProjectName(@RequestParam(name = "params") String params) {
        Map<String, Object> requestMap = JsonUtil.jsonToMap(params);
        String projectName = String.valueOf(requestMap.get("projectName"));

        // IProjectbudgetinfoService budgetService =
        // ContainerFactory.getContainInfo().getComponent(IProjectbudgetinfoService.class);
        List<Projectbudgetinfo> infoList = budgetService.getInfosByProjectName(projectName);

        for (Projectbudgetinfo b : infoList) {
            b.put("budgettype", itemService.getItemTextByCodeName("预算类型", b.getBudgettype()));
        }
        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("data", infoList);
        return buildSuccessResponse(JsonUtil.objectToJson(returnMap));
    }
}
