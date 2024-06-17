package com.epoint.xmcg.projectbudgetinfo.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.epoint.basic.controller.BaseController;
import com.epoint.basic.controller.RightRelation;
import com.epoint.xmcg.projectbudgetinfo.api.IProjectbudgetinfoService;
import com.epoint.xmcg.projectbudgetinfo.api.entity.Projectbudgetinfo;
import com.epoint.xmcg.projectinfo.api.IProjectinfoService;

/**
 * 项目预算信息表详情页面对应的后台
 * 
 * @author Epoint
 * @version [版本号, 2024-06-05 16:03:53]
 */
@RightRelation(ProjectbudgetinfoListAction.class)
@RestController("projectbudgetinfodetailaction")
@Scope("request")
public class ProjectbudgetinfoDetailAction extends BaseController
{
    @Autowired
    private IProjectbudgetinfoService service;

    @Autowired
    private IProjectinfoService projectinfoService;

    /**
     * 项目预算信息表实体对象
     */
    private Projectbudgetinfo dataBean = null;

    public void pageLoad() {
        String guid = getRequestParameter("guid");
        dataBean = service.find(guid);
        if (dataBean == null) {
            dataBean = new Projectbudgetinfo();
        }
        else {
            dataBean.put("projectName", projectinfoService.find(dataBean.getProjectguid()).getProjectname());
        }
    }

    public Projectbudgetinfo getDataBean() {
        return dataBean;
    }
}
