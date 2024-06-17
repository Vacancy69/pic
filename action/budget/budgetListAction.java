package com.epoint.xmcg.projectbudgetinfo.action;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.epoint.basic.controller.BaseController;
import com.epoint.basic.faces.export.ExportModel;
import com.epoint.core.dto.model.DataGridModel;
import com.epoint.core.utils.sql.SqlConditionUtil;
import com.epoint.database.peisistence.crud.impl.model.PageData;
import com.epoint.frame.service.metadata.mis.util.ListGenerator;
import com.epoint.xmcg.projectbudgetinfo.api.IProjectbudgetinfoService;
import com.epoint.xmcg.projectbudgetinfo.api.entity.Projectbudgetinfo;
import com.epoint.xmcg.projectinfo.api.IProjectinfoService;

/**
 * 项目预算信息表list页面对应的后台
 * 
 * @author Epoint
 * @version [版本号, 2024-06-05 16:03:53]
 */
@RestController("projectbudgetinfolistaction")
@Scope("request")
public class ProjectbudgetinfoListAction extends BaseController
{
    @Autowired
    private IProjectbudgetinfoService service;

    @Autowired
    private IProjectinfoService projectinfoService;

    /**
     * 项目预算信息表实体对象
     */
    private Projectbudgetinfo dataBean;

    /**
     * 表格控件model
     */
    private DataGridModel<Projectbudgetinfo> model;

    /**
     * 导出模型
     */
    private ExportModel exportModel;

    private String projectGuid;

    private String flag;

    public void pageLoad() {
        projectGuid = getRequestParameter("projectGuid");
        // 限定查询条件
        flag = getRequestParameter("flag");
    }

    /**
     * 删除选定
     * 
     */
    public void deleteSelect() {
        List<String> select = getDataGridData().getSelectKeys();
        for (String sel : select) {
            service.deleteByGuid(sel);
        }
        addCallbackParam("msg", "成功删除！");
    }

    public DataGridModel<Projectbudgetinfo> getDataGridData() {
        // 获得表格对象
        if (model == null) {
            model = new DataGridModel<Projectbudgetinfo>()
            {

                @Override
                public List<Projectbudgetinfo> fetchData(int first, int pageSize, String sortField, String sortOrder) {

                    // 获取where条件Map集合
                    Map<String, Object> conditionMap = ListGenerator.getSearchMap(getRequestContext().getComponents(),
                            sortField, sortOrder);

                    SqlConditionUtil sqlConditionUtil = new SqlConditionUtil();
                    sqlConditionUtil.eq("projectGuid", projectGuid);

                    // if ("flag".equals(flag)) {
                    // sqlConditionUtil.eq("budgettype", "2");
                    // }

                    conditionMap.putAll(sqlConditionUtil.getMap());

                    PageData<Projectbudgetinfo> pageData = service.paginatorList(conditionMap, first, pageSize);
                    this.setRowCount(pageData.getRowCount());

                    List<Projectbudgetinfo> list = pageData.getList();
                    for (Projectbudgetinfo projectbudget : list) {
                        projectbudget.put("projectName",
                                projectinfoService.find(projectbudget.getProjectguid()).getProjectname());
                    }

                    return list;
                }

            };
        }
        return model;
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

    public ExportModel getExportModel() {
        if (exportModel == null) {
            exportModel = new ExportModel("", "");
        }
        return exportModel;
    }

}
