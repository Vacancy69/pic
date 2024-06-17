package com.epoint.xmcg.assetinfo.action;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.epoint.basic.faces.fileupload.FileUploadModel9;
import com.epoint.basic.faces.tree.DefaultFileUploadHandlerImpl9;
import com.epoint.basic.faces.util.DataUtil;
import com.epoint.core.dto.model.SelectItem;
import com.epoint.core.utils.convert.ConvertUtil;
import com.epoint.core.utils.date.EpointDateUtil;
import com.epoint.core.utils.string.StringUtil;
import com.epoint.frame.service.message.api.IMessagesCenterService;
import com.epoint.frame.service.metadata.mis.util.CodeModalFactory;
import com.epoint.frame.service.organ.user.api.IUserService;
import com.epoint.workflow.controller.execute.WorkflowBaseController;
import com.epoint.workflow.service.common.custom.ProcessVersionInstance;
import com.epoint.workflow.service.common.entity.config.WorkflowActivityOperation;
import com.epoint.workflow.service.common.entity.execute.WorkflowWorkItem;
import com.epoint.workflow.service.common.runtime.WorkflowResult9;
import com.epoint.workflow.service.core.api.IWFDefineAPI9;
import com.epoint.workflow.service.core.api.IWFEngineAPI9;
import com.epoint.workflow.service.core.api.IWFInitPageAPI9;
import com.epoint.workflow.service.core.api.IWFInstanceAPI9;
import com.epoint.xmcg.assetinfo.api.IAssetinfoService;
import com.epoint.xmcg.assetinfo.api.entity.Assetinfo;
import com.epoint.xmcg.projectbudgetinfo.api.IProjectbudgetinfoService;
import com.epoint.xmcg.projectbudgetinfo.api.entity.Projectbudgetinfo;

/**
 * 项目采购数据表工作流页面对应的后台
 * 
 * @author Epoint
 * @version [版本号, 2024-06-07 14:18:09]
 */
@RestController("assetinfoworkflowaction")
@Scope("request")
public class AssetinfoWorkFlowAction extends WorkflowBaseController
{
    /**
     * 获取流程版本实例操作API
     */
    @Autowired
    private IWFInstanceAPI9 wfInstanceAPI9;

    /**
     * 获取流程流转页面信息API
     */
    @Autowired
    private IWFInitPageAPI9 wfInitPageAPI9;

    /**
     * 获取工作流流程定义信息API
     */
    @Autowired
    private IWFDefineAPI9 wfDefineAPI9;

    @Autowired
    IWFEngineAPI9 wfEngineAPI9;

    private ProcessVersionInstance pvi = null;

    private Assetinfo dataBean = null;
    // 数据表名
    private String SQLTableName = "assetinfo";

    private String rowguid;

    private WorkflowWorkItem item;

    @Autowired
    private IAssetinfoService service;

    @Autowired
    private IProjectbudgetinfoService projectbudgetinfoService;

    @Autowired
    private IUserService userService;

    private String processVersionInstanceGuid;

    @Autowired
    private IMessagesCenterService messagesCenterService;

    /**
     * 采购类型单选按钮组model
     */
    private List<SelectItem> assettypeModel = null;

    @Override
    public void pageLoad() {

        // 获取工作项guid
        String workitemGuid = getRequestParameter("WorkItemGuid");
        // 获取流程版本实例guid
        processVersionInstanceGuid = getRequestParameter("ProcessVersionInstanceGuid");
        // 流程版本实例信息
        pvi = wfInstanceAPI9.getProcessVersionInstance(processVersionInstanceGuid);
        if (StringUtil.isNotBlank(workitemGuid)) {
            item = wfInstanceAPI9.getWorkItem(pvi, workitemGuid);
        }

        // 采购信息主键
        rowguid = getRequestParameter("guid");
        // 初始化mis表
        if (StringUtil.isBlank(rowguid)) {
            rowguid = wfInstanceAPI9.getContextItemValue(pvi, SQLTableName);
        }
        if (StringUtil.isNotBlank(rowguid)) {
            dataBean = service.find(rowguid);
            if (null != dataBean.getBudgetguid()) {
                Projectbudgetinfo info = projectbudgetinfoService.find(dataBean.getBudgetguid());
                if (null != info) {
                    addCallbackParam("budgetname", info.getBudgetname());
                }
            }
            // addCallbackParam("applyusername", applyusername);
        }
        else {
            dataBean = new Assetinfo();
            // rowguid = UUID.randomUUID().toString();

            // 公用隐藏域
            rowguid = getViewData("rowguid");
            if (StringUtil.isBlank(rowguid)) {
                rowguid = UUID.randomUUID().toString();
                addViewData("rowguid", rowguid);
            }

            dataBean.setApplyuserguid(userSession.getUserGuid());
            // 大小写无影响
            addCallbackParam("applyusername", userSession.getDisplayName());
            dataBean.setApplytime(new Date());
            // addCallbackParam("applytimeoutput",
            // EpointDateUtil.convertDate2String(new Date(),
            // EpointDateUtil.DATE_TIME_FORMAT));

        }
        addCallbackParam("applyusername", userSession.getDisplayName());
        addCallbackParam("applytimeoutput",
                EpointDateUtil.convertDate2String(new Date(), EpointDateUtil.DATE_TIME_FORMAT));

        // 需要设置字段权限时设置
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("activityGuid", item.getActivityGuid());
            jsonobject.put("issingleform", true);
            jsonobject.put("processversioninstanceguid", processVersionInstanceGuid);
            addCallbackParam("accessRight", wfInitPageAPI9.init_getTablePropertyControl(jsonobject.toJSONString()));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        ///////
    }

    /**
     * 保存并关闭
     * 
     */
    public void submit() {
        if (save()) {
            wfInstanceAPI9.singlematerialSubmit(pvi, SQLTableName, rowguid, userSession.getUserGuid(), true);
        }
    }

    /**
     * 保存并新建
     * 
     */
    public void saveForm() {
        save();
    }

    private boolean save() {
        // 如果不满足业务逻辑返回false
        // if (不符合条件) {
        // addCallbackParam("msg", "提示错误");
        // return false;
        // }
        if ("申请".equals(item.getActivityName())
                && EpointDateUtil.compareDateOnDay(dataBean.getArrivaltime(), dataBean.getApplytime()) <= 0) {
            addCallbackParam("msg", "到货时间不能早于申请时间");
            return false;
        }

        if (StringUtil.isBlank(dataBean.getRowguid())) {
            // rowguid = UUID.randomUUID().toString();
            dataBean.setRowguid(rowguid);
            dataBean.setOperatedate(new Date());
            dataBean.setOperateusername(userSession.getDisplayName());
            dataBean.setStatus("10");

            dataBean.setPviguid(processVersionInstanceGuid);

            service.insert(dataBean);
        }
        else {
            rowguid = dataBean.getRowguid();
            dataBean.setOperatedate(new Date());
            service.update(dataBean);
        }
        wfInstanceAPI9.singlematerialSubmit(pvi, SQLTableName, rowguid, userSession.getUserGuid(), false);

        /////////////////////////////////////

        if ("申请".equals(item.getActivityName())) {
            // 物品总价不得超过预算的剩余金额
            Projectbudgetinfo budgetinfo = projectbudgetinfoService.find(dataBean.getBudgetguid());
            if (null != budgetinfo) {
                if (dataBean.getTotalmoney() > (budgetinfo.getBudgetmoney() - budgetinfo.getUsedbudgetmoney())) {
                    addCallbackParam("msg", "物品总价不得超预算的剩余金额");
                    return false;
                }
            }

            item.setWorkItemName("待提交的采购申请");
            wfInstanceAPI9.updateWorkFlowWorkItem(Arrays.asList(item));
            // 代办guid
            messagesCenterService.updateMessageTitleAndShow(item.getWaitHandleGuid(), "待提交的采购申请",
                    userSession.getUserGuid(), true);
        }

        // 如果满足业务逻辑返回true,msg为空
        addCallbackParam("msg", "");
        return true;
    }

    public void setDataBean(Assetinfo dataBean) {
        this.dataBean = dataBean;
    }

    public Assetinfo getDataBean() {
        return dataBean;
    }

    /**
     * 系统方法，勿删 获取按钮打开页面或者默认处理
     * 
     * @param operationGuid
     *            操作按钮guid
     * @param transitionGuid
     *            变迁guid
     * @return
     */
    public String getPageUrlOfOperate(String data) {
        // String returndata = EncodeUtil.decode(data);
        JSONObject jsonobject = JSONObject.parseObject(data);
        jsonobject.put("userguid", userSession.getUserGuid());
        jsonobject.put("ishandleendactivity", true);
        if (StringUtil.isNotBlank(getRequestParameter("batchHandleGuid"))) {
            String otherurlparam = "batchHandleGuid=" + getRequestParameter("batchHandleGuid");
            jsonobject.put("otherurlparam", otherurlparam);
        }
        String operationGuid = jsonobject.get("operationguid").toString();

        // 获取操作处理页面或者默认处理
        JSONObject returnobject = JSONObject
                .parseObject(wfInitPageAPI9.init_getPageUrlOfOperate(jsonobject.toString()));
        WorkflowActivityOperation operation = (StringUtil.isNotBlank(operationGuid))
                ? wfDefineAPI9.getActOperation(pvi, operationGuid)
                : new WorkflowActivityOperation();
        if (returnobject.containsKey("isdefoperation")) {
            boolean isdefault = ConvertUtil.convertStringToBoolean(returnobject.get("isdefoperation").toString());
            if (isdefault) {
                // 需要默认操作前确认提示返回returnobject.toString()即可
                jsonobject.put("operationname", operation.getOperationName());
                jsonobject.put("isdefoperation", isdefault);
                return jsonobject.toString();
                // 如果不需要提示直接处理
                // return getoperate(data);
            }
        }
        returnobject.put("operationtype", operation.getOperationType());
        return returnobject.toString();
    }

    /**
     * 系统方法，勿删 操作处理页面返回json后回调
     * 
     * @param data
     *            json数据
     * @return
     */
    public String getoperate(String data) {
        // data = EncodeUtil.decode(data);
        JSONObject jsonobject = JSONObject.parseObject(data);
        jsonobject.put("userguid", userSession.getUserGuid());
        jsonobject.put("displayname", userSession.getDisplayName());
        String json = jsonobject.toString();
        JSONObject returnjsonobject = new JSONObject();
        returnjsonobject.put("message", "");
        try {
            // 调用IWFRMSAPI.operateTCC包裹分布式事务，如果存在操作前后事件请个性化主业务服务IWFRMSAPI
            // String resultString = wfRMSAPI.operateTCC(json);
            // WorkflowResult9 result = JsonUtil.jsonToObject(resultString,
            // WorkflowResult9.class);
            WorkflowResult9 result = wfEngineAPI9.operate(json);
            // 非服务化部署，本地事务情况可以编写操作后事件
            // 服务化架构部署的此处不允许编写操作后事件
            if (StringUtil.isNotBlank(result.getCurrentActIstance().getActivity().getNote())) {
                // getActivityDispName
                switch (result.getCurrentActIstance().getActivity().getNote()) {
                    case "审核":
                        // getOperationName()
                        switch (result.getOperation().getNote()) {
                            case "送下一步":
                                // 操作后事件
                                break;
                            case "退回":
                                // 操作后事件
                                break;
                            default:
                                break;
                        }
                    case "申请":
                        // 事件同上
                        break;
                    default:
                        break;
                }
            }
        }
        catch (RuntimeException e) {
            String message = e.getMessage();
            if (StringUtil.isNotBlank(e.getMessage()) && e.getMessage().indexOf("：") > -1) {
                message = e.getMessage().split("：")[1];
            }
            returnjsonobject.put("message", message);
        }
        return returnjsonobject.toString();
    }

    public List<SelectItem> getAssettypeModel() {
        if (assettypeModel == null) {
            assettypeModel = DataUtil.convertMap2ComboBox(
                    (List<Map<String, String>>) CodeModalFactory.factory("单选按钮组", "采购类型", null, false));
        }
        return this.assettypeModel;
    }

    //////////////////////////////////////////////

    private FileUploadModel9 fileUploadModel;

    public FileUploadModel9 getFileUploadModel() {
        if (null == fileUploadModel) {
            // 采购信息主键 rowguid-clientguid
            fileUploadModel = new FileUploadModel9(new DefaultFileUploadHandlerImpl9(rowguid, userSession.getUserGuid(),
                    userSession.getDisplayName()));
        }
        fileUploadModel.setReadOnly("true");
        return fileUploadModel;
    }

}
