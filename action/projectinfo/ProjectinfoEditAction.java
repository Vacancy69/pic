package com.epoint.xmcg.projectinfo.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.epoint.basic.controller.BaseController;
import com.epoint.basic.controller.RightRelation;
import com.epoint.basic.faces.util.DataUtil;
import com.epoint.core.dto.model.SelectItem;
import com.epoint.frame.service.metadata.code.api.ICodeItemsService;
import com.epoint.frame.service.metadata.mis.util.CodeModalFactory;
import com.epoint.frame.service.organ.user.api.IUserService;
import com.epoint.xmcg.projectinfo.api.IProjectinfoService;
import com.epoint.xmcg.projectinfo.api.entity.Projectinfo;

/**
 * 项目信息表修改页面对应的后台
 * 
 * @author Epoint
 * @version [版本号, 2024-06-05 16:03:42]
 */
@RightRelation(ProjectinfoListAction.class)
@RestController("projectinfoeditaction")
@Scope("request")
public class ProjectinfoEditAction extends BaseController
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

    @Autowired
    private ICodeItemsService codeItemsService;

    @Autowired
    private IUserService userService;

    public void pageLoad() {
        String guid = getRequestParameter("guid");
        dataBean = service.find(guid);
        if (dataBean == null) {
            dataBean = new Projectinfo();
        }

        // 将所属地区value转换为文本
        String belongZoneName = codeItemsService.getItemTextByCodeName("所属地区", dataBean.getBelongzone());
        addCallbackParam("belongZoneName", belongZoneName);

        // 获取项目经理的姓名
        String projectManagerName = userService.getUserNameByUserGuid(dataBean.getProjectmanager());
        addCallbackParam("projectmanager", projectManagerName);

        // 获取项目人员姓名
        String projectUserGuid = dataBean.getProjectuser();
        String projectUserName = service.getUserAllNamesByUserGuid(projectUserGuid);
        addCallbackParam("projectUserName", projectUserName);
    }

    /**
     * 保存修改
     * 
     */
    public void save() {
        dataBean.setOperatedate(new Date());

        String projectNameOld = service.find(dataBean.getRowguid()).getProjectname();
        String projectNameNew = dataBean.getProjectname();

        // 修改时的重名验证
        Projectinfo projectinfo = service.findProjectinfoByProjectname(projectNameOld, projectNameNew);
        if (projectinfo != null) {
            addCallbackParam("msg", projectNameNew + "已存在， 请勿重复添加");
            addCallbackParam("info", "exist");
        }
        else {
            service.update(dataBean);
            addCallbackParam("msg", "修改成功！");
        }
    }

    public Projectinfo getDataBean() {
        return dataBean;
    }

    public void setDataBean(Projectinfo dataBean) {
        this.dataBean = dataBean;
    }

    public List<SelectItem> getBelongzoneModel() {
        if (belongzoneModel == null) {
            belongzoneModel = DataUtil.convertMap2ComboBox(
                    (List<Map<String, String>>) CodeModalFactory.factory("下拉列表", "所属地区", null, false));
        }
        return this.belongzoneModel;
    }

    public List<SelectItem> getProjecttypeModel() {
        if (projecttypeModel == null) {
            projecttypeModel = DataUtil.convertMap2ComboBox(
                    (List<Map<String, String>>) CodeModalFactory.factory("下拉列表", "项目类别", null, false));
        }
        return this.projecttypeModel;
    }

}
