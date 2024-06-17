package com.epoint.xmcg.projectbudgetinfo.action;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.epoint.basic.controller.BaseController;
import com.epoint.basic.controller.RightRelation;
import com.epoint.basic.faces.util.DataUtil;
import com.epoint.core.dto.model.SelectItem;
import com.epoint.frame.service.metadata.mis.util.CodeModalFactory;
import com.epoint.xmcg.projectbudgetinfo.api.IProjectbudgetinfoService;
import com.epoint.xmcg.projectbudgetinfo.api.entity.Projectbudgetinfo;
import com.epoint.xmcg.projectinfo.api.IProjectinfoService;

/**
 * 项目预算信息表新增页面对应的后台
 * 
 * @author Epoint
 * @version [版本号, 2024-06-05 16:03:53]
 */
@RightRelation(ProjectbudgetinfoListAction.class)
@RestController("projectbudgetinfoaddaction")
@Scope("request")
public class ProjectbudgetinfoAddAction extends BaseController
{
    @Autowired
    private IProjectbudgetinfoService service;
    /**
     * 项目预算信息表实体对象
     */
    private Projectbudgetinfo dataBean = null;

    /**
     * 预算类型单选按钮组model
     */
    private List<SelectItem> budgettypeModel = null;

    @Autowired
    private IProjectinfoService projectinfoService;

    public void pageLoad() {
        dataBean = new Projectbudgetinfo();
        projectGuid = getRequestParameter("projectGuid");
        dataBean.setUpdatetime(new Date());
    }

    private String projectGuid;

    /**
     * 保存并关闭
     * 
     */
    public void add() {
        dataBean.setRowguid(UUID.randomUUID().toString());
        dataBean.setOperatedate(new Date());
        dataBean.setOperateusername(userSession.getDisplayName());

        dataBean.setProjectguid(projectGuid);
        dataBean.setUsedbudgetmoney(0);

        // 验证当前项目的预算总额不能超过当前项目的资金金额

        // 获取当前项目已有的预算总额
        System.out.println(projectGuid);
        int moneyBefore = service.getMoneyBefore(projectGuid);
        int money = dataBean.getBudgetmoney();
        int projectmoney = projectinfoService.find(projectGuid).getProjectmoney();

        if (moneyBefore + money > projectmoney) {
            addCallbackParam("msg", "预算总额超过了当前项目的资金金额");
            return;
        }

        service.insert(dataBean);
        addCallbackParam("msg", "保存成功！");
        dataBean = null;
    }

    /**
     * 保存并新建
     * 
     */
    public void addNew() {
        add();
        dataBean = new Projectbudgetinfo();
    }

    public Projectbudgetinfo getDataBean() {
        if (dataBean == null) {
            dataBean = new Projectbudgetinfo();
        }
        return dataBean;
    }

    public void setDataBean(Projectbudgetinfo dataBean) {
        this.dataBean = dataBean;
    }

    public List<SelectItem> getBudgettypeModel() {
        if (budgettypeModel == null) {
            budgettypeModel = DataUtil.convertMap2ComboBox(
                    (List<Map<String, String>>) CodeModalFactory.factory("下拉列表", "预算类型", null, false));
        }
        return this.budgettypeModel;
    }

}
