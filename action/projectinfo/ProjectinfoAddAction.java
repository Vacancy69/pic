package com.epoint.xmcg.projectinfo.action;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.epoint.basic.controller.BaseController;
import com.epoint.basic.controller.RightRelation;
import com.epoint.basic.faces.tree.LazyTreeModal9;
import com.epoint.basic.faces.util.DataUtil;
import com.epoint.core.dto.model.SelectItem;
import com.epoint.frame.service.metadata.mis.util.CodeModalFactory;
import com.epoint.frame.service.metadata.mis.util.CodeTreeHandler;
import com.epoint.xmcg.projectinfo.api.IProjectinfoService;
import com.epoint.xmcg.projectinfo.api.entity.Projectinfo;

/**
 * 项目信息表新增页面对应的后台
 * 
 * @author Epoint
 * @version [版本号, 2024-06-05 16:03:42]
 */
@RightRelation(ProjectinfoListAction.class)
@RestController("projectinfoaddaction")
@Scope("request")
public class ProjectinfoAddAction extends BaseController
{
    @Autowired
    private IProjectinfoService service;
    /**
     * 项目信息表实体对象
     */
    private Projectinfo dataBean = null;

    /**
     * 所属地区下拉列表model
     */
    private List<SelectItem> belongzoneModel = null;
    /**
     * 项目类别下拉列表model
     */
    private List<SelectItem> projecttypeModel = null;

    /**
     * 树模型
     */
    private LazyTreeModal9 lazyTreeModal9;

    public void pageLoad() {
        dataBean = new Projectinfo();
    }

    /**
     * 保存并关闭
     * 
     */
    public void add() {
        dataBean.setRowguid(UUID.randomUUID().toString());
        dataBean.setOperatedate(new Date());
        dataBean.setOperateusername(userSession.getDisplayName());

        // 保存时的重名验证
        String projectname = dataBean.getProjectname();
        Projectinfo projectinfo = service.findProjectinfoByProjectname(projectname);
        if (projectinfo != null) {
            addCallbackParam("msg", projectname + "已存在，请勿重复添加");
            addCallbackParam("info", "exist");
        }
        else {
            service.insert(dataBean);
            addCallbackParam("msg", "保存成功！");
        }
        dataBean = null;
    }

    /**
     * 保存并新建
     * 
     */
    public void addNew() {
        add();
        dataBean = new Projectinfo();
    }

    public Projectinfo getDataBean() {
        if (dataBean == null) {
            dataBean = new Projectinfo();
        }
        return dataBean;
    }

    public void setDataBean(Projectinfo dataBean) {
        this.dataBean = dataBean;
    }

    /**
     * 地区树
     * 
     * @return
     */
    public LazyTreeModal9 getBelongzoneModel() {
        // if (belongzoneModel == null) {
        // belongzoneModel = DataUtil.convertMap2ComboBox(
        // (List<Map<String, String>>) CodeModalFactory.factory("下拉列表", "所属地区",
        // null, false));
        // }
        // return this.belongzoneModel;
        if (lazyTreeModal9 == null) {
            lazyTreeModal9 = new LazyTreeModal9(
                    // 将代码项处理成树结构
                    new CodeTreeHandler("所属地区", false));
            // 给下拉书设置根节点文本
            lazyTreeModal9.setRootName("所属地区");
        }
        return this.lazyTreeModal9;
    }

    public List<SelectItem> getProjecttypeModel() {
        if (projecttypeModel == null) {
            projecttypeModel = DataUtil.convertMap2ComboBox(
                    (List<Map<String, String>>) CodeModalFactory.factory("下拉列表", "项目类别", null, false));
        }
        return this.projecttypeModel;
    }

}
