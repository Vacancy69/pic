package com.epoint.xmcg.assetinfo.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.epoint.basic.controller.BaseController;
import com.epoint.basic.faces.export.ExportModel;
import com.epoint.core.dto.base.TreeNode;
import com.epoint.core.dto.model.DataGridModel;
import com.epoint.core.dto.model.TreeModel;
import com.epoint.core.utils.collection.EpointCollectionUtils;
import com.epoint.core.utils.sql.SqlConditionUtil;
import com.epoint.core.utils.string.StringUtil;
import com.epoint.database.peisistence.crud.impl.model.PageData;
import com.epoint.frame.service.metadata.mis.util.ListGenerator;
import com.epoint.frame.service.organ.user.api.IUserService;
import com.epoint.workflow.service.common.custom.ProcessVersionInstance;
import com.epoint.workflow.service.common.entity.execute.WorkflowWorkItem;
import com.epoint.workflow.service.core.api.IWFInstanceAPI9;
import com.epoint.xmcg.assetinfo.api.IAssetinfoService;
import com.epoint.xmcg.assetinfo.api.entity.Assetinfo;
import com.epoint.xmcg.projectbudgetinfo.api.IProjectbudgetinfoService;
import com.epoint.xmcg.projectbudgetinfo.api.entity.Projectbudgetinfo;
import com.epoint.xmcg.projectinfo.api.IProjectinfoService;
import com.epoint.xmcg.projectinfo.api.entity.Projectinfo;

/**
 * 项目采购数据表list页面对应的后台
 * 
 * @author Epoint
 * @version [版本号, 2024-06-07 14:18:09]
 */
@RestController("assetinfolistaction")
@Scope("request")
public class AssetinfoListAction extends BaseController
{
    @Autowired
    private IAssetinfoService service;

    /**
     * 项目采购数据表实体对象
     */
    private Assetinfo dataBean;

    /**
     * 表格控件model
     */
    private DataGridModel<Assetinfo> model;

    /**
     * 导出模型
     */
    private ExportModel exportModel;

    @Autowired
    private IProjectinfoService projectinfoService;

    @Autowired
    private IProjectbudgetinfoService projectbudgetinfoService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IWFInstanceAPI9 wfInstanceAPI9;

    public void pageLoad() {
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

    public DataGridModel<Assetinfo> getDataGridData() {

        // 获得表格对象
        if (model == null) {
            model = new DataGridModel<Assetinfo>()
            {

                @Override
                public List<Assetinfo> fetchData(int first, int pageSize, String sortField, String sortOrder) {

                    // 获取where条件Map集合
                    Map<String, Object> conditionMap = ListGenerator.getSearchMap(getRequestContext().getComponents(),
                            sortField, sortOrder);

                    if (StringUtil.isNotBlank(nodeInfo)) {

                        String nodeId = nodeInfo.split(";")[0];
                        String nodeType = nodeInfo.split(";")[1];

                        // 点击子节点 sNode
                        if ("sNode".equals(nodeType)) {
                            SqlConditionUtil util = new SqlConditionUtil();
                            // select * from assetinfo where 1=1 and budgetguid
                            // =
                            // nodeId
                            util.eq("budgetguid", nodeId);
                            conditionMap.putAll(util.getMap());
                        }
                        if ("pNode".equals(nodeType)) {
                            // select * from assetinfo where 1=1 and budgetguid
                            // in
                            // (select rowguid from projectbudgetinfo where 1=1
                            // and
                            // projectguid = 'xx');
                            // in ('xx','yy','zz')
                            SqlConditionUtil util = new SqlConditionUtil();
                            util.eq("projectguid", nodeId);
                            List<Projectbudgetinfo> projectBudgetinfoList = projectbudgetinfoService
                                    .findList(util.getMap());
                            List<String> budgetGuidList = new ArrayList<>();
                            for (Projectbudgetinfo projectbudgetinfo : projectBudgetinfoList) {
                                budgetGuidList.add(projectbudgetinfo.getRowguid());
                            }
                            String budgetGuidStr = StringUtil.join(budgetGuidList, "','");

                            SqlConditionUtil util2 = new SqlConditionUtil();
                            util2.in("budgetguid", "'" + budgetGuidStr + "'");
                            conditionMap.putAll(util2.getMap());
                        }
                    }

                    PageData<Assetinfo> pageData = service.paginatorList(conditionMap, first, pageSize);
                    this.setRowCount(pageData.getRowCount());

                    List<Assetinfo> list = pageData.getList();

                    /////////////
                    String title = "采购详情", urlString = "";

                    for (Assetinfo assetinfo : list) {
                        assetinfo.put("applyUserName", userService.getUserNameByUserGuid(assetinfo.getApplyuserguid()));
                        assetinfo.put("budgetName",
                                projectbudgetinfoService.find(assetinfo.getBudgetguid()).getBudgetname());

                        // 数据重置
                        title = "采购详情";
                        urlString = "";
                        if (null != assetinfo.getPviguid()) {

                            ProcessVersionInstance pvi = wfInstanceAPI9
                                    .getProcessVersionInstance(assetinfo.getPviguid());
                            List<WorkflowWorkItem> wItems = wfInstanceAPI9.getWorkItemListByUserGuid(pvi,
                                    userSession.getUserGuid());
                            if (EpointCollectionUtils.isNotEmpty(wItems)) {
                                title = wItems.get(0).getWorkItemName();
                                urlString = wItems.get(0).getHandleUrl();
                            }
                        }
                        assetinfo.set("title", title);
                        assetinfo.put("url", urlString);
                    }
                    return list;
                }
            };
        }
        return model;
    }

    private TreeModel treeModel;

    /**
     * 构建对象树
     * 
     * @return
     */
    public TreeModel getTreeModel() {
        if (treeModel == null) {
            treeModel = new TreeModel()
            {

                @Override
                public List<TreeNode> fetch(TreeNode node) {
                    List<TreeNode> list = new ArrayList<>();

                    if (node == null) {
                        // 初次加载 构建根节点和父节点两个层级

                        // 构建根节点
                        TreeNode rNode = new TreeNode();
                        rNode.setId("");
                        // parent节点
                        rNode.setPid("-1");
                        rNode.setText("所有项目预算");
                        // 可展开
                        rNode.setExpanded(true);
                        // 自定义字段
                        rNode.getColumns().put("nodeType", "rNode");

                        list.add(rNode);

                        // 构建父节点
                        List<Projectinfo> projectinfoList = projectinfoService.findList(new HashMap<>());
                        TreeNode pNode = null;
                        for (Projectinfo projectinfo : projectinfoList) {
                            pNode = new TreeNode();
                            pNode.setId(projectinfo.getRowguid());
                            pNode.setPid(rNode.getId());
                            pNode.setText(projectinfo.getProjectname());

                            // 判断当前节点是否为叶子节点 判断有没有预算
                            SqlConditionUtil util = new SqlConditionUtil();
                            util.eq("projectguid", projectinfo.getRowguid());
                            List<Projectbudgetinfo> projectbudgetinfolist = projectbudgetinfoService
                                    .findList(util.getMap());
                            pNode.setLeaf(EpointCollectionUtils.isEmpty(projectbudgetinfolist));
                            pNode.getColumns().put("nodeType", "pNode");

                            list.add(pNode);
                        }
                    }
                    else {
                        // 再次加载
                        SqlConditionUtil util = new SqlConditionUtil();
                        util.eq("projectguid", node.getId());
                        List<Projectbudgetinfo> projectbudgetinfoList = projectbudgetinfoService
                                .findList(util.getMap());
                        TreeNode sNode = null;
                        for (Projectbudgetinfo projectbudgetinfo : projectbudgetinfoList) {
                            sNode = new TreeNode();
                            sNode.setId(projectbudgetinfo.getRowguid());
                            sNode.setPid(node.getId());
                            sNode.setLeaf(true);
                            sNode.setText(projectbudgetinfo.getBudgetname());

                            sNode.getColumns().put("nodeType", "sNode");
                            list.add(sNode);
                        }
                    }
                    return list;
                }
            };
        }
        return treeModel;
    }

    public Assetinfo getDataBean() {
        if (dataBean == null) {
            dataBean = new Assetinfo();
        }
        return dataBean;
    }

    public void setDataBean(Assetinfo dataBean) {
        this.dataBean = dataBean;
    }

    public ExportModel getExportModel() {
        if (exportModel == null) {
            exportModel = new ExportModel("", "");
        }
        return exportModel;
    }

    private String nodeInfo;

    public String getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(String nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

}
