package com.epoint.xmcg.assetinfo.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.epoint.basic.controller.BaseController;
import com.epoint.basic.controller.RightRelation;
import com.epoint.frame.service.organ.user.api.IUserService;
import com.epoint.xmcg.assetinfo.api.IAssetinfoService;
import com.epoint.xmcg.assetinfo.api.entity.Assetinfo;
import com.epoint.xmcg.projectbudgetinfo.api.IProjectbudgetinfoService;

/**
 * 项目采购数据表详情页面对应的后台
 * 
 * @author Epoint
 * @version [版本号, 2024-06-07 14:18:09]
 */
@RightRelation(AssetinfoListAction.class)
@RestController("assetinfodetailaction")
@Scope("request")
public class AssetinfoDetailAction extends BaseController
{
    @Autowired
    private IAssetinfoService service;

    @Autowired
    private IUserService userService;

    @Autowired
    private IProjectbudgetinfoService projectbudgetinfoService;

    /**
     * 项目采购数据表实体对象
     */
    private Assetinfo dataBean = null;

    public void pageLoad() {
        String guid = getRequestParameter("guid");
        dataBean = service.find(guid);
        if (dataBean == null) {
            dataBean = new Assetinfo();
        }
        addCallbackParam("applyusername", userService.getUserNameByUserGuid(dataBean.getApplyuserguid()));
        addCallbackParam("budgetname", projectbudgetinfoService.find(dataBean.getBudgetguid()).getBudgetname());
    }

    public Assetinfo getDataBean() {
        return dataBean;
    }
}
