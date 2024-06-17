package com.epoint.xmcg.projectinfo.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.epoint.basic.controller.BaseController;
import com.epoint.basic.controller.RightRelation;
import com.epoint.frame.service.organ.user.api.IUserService;
import com.epoint.xmcg.projectinfo.api.IProjectinfoService;
import com.epoint.xmcg.projectinfo.api.entity.Projectinfo;

/**
 * 项目信息表详情页面对应的后台
 * 
 * @author Epoint
 * @version [版本号, 2024-06-05 16:03:42]
 */
@RightRelation(ProjectinfoListAction.class)
@RestController("projectinfodetailaction")
@Scope("request")
public class ProjectinfoDetailAction extends BaseController
{
    @Autowired
    private IProjectinfoService service;

    @Autowired
    private IUserService userService;

    /**
     * 项目信息表实体对象
     */
    private Projectinfo dataBean = null;

    public void pageLoad() {
        String guid = getRequestParameter("guid");
        dataBean = service.find(guid);
        if (dataBean == null) {
            dataBean = new Projectinfo();
        }

        // 获取项目经理的姓名
        String projectManagerName = userService.getUserNameByUserGuid(dataBean.getProjectmanager());
        addCallbackParam("projectmanager", projectManagerName);

        // 获取项目人员姓名
        String projectUserGuid = dataBean.getProjectuser();
        String projectUserName = service.getUserAllNamesByUserGuid(projectUserGuid);
        addCallbackParam("projectUserName", projectUserName);
    }

    public Projectinfo getDataBean() {
        return dataBean;
    }
}
