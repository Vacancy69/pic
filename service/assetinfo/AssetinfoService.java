package com.epoint.xmcg.assetinfo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.epoint.core.dao.CommonDao;
import com.epoint.core.dao.ICommonDao;
import com.epoint.core.grammar.Record;
import com.epoint.core.utils.SearchParamUtil;
import com.epoint.core.utils.container.ContainerFactory;
import com.epoint.core.utils.sql.SqlConditionUtil;
import com.epoint.core.utils.sql.SqlHelper;
import com.epoint.core.utils.string.StringUtil;
import com.epoint.database.jdbc.Parameter;
import com.epoint.database.peisistence.crud.impl.model.PageData;
import com.epoint.frame.service.metadata.systemparameters.api.IConfigService;
import com.epoint.xmcg.assetinfo.api.entity.Assetinfo;
import com.epoint.xmcg.projectbudgetinfo.api.entity.Projectbudgetinfo;
import com.epoint.xmcg.projectinfo.api.entity.Projectinfo;

/**
 * 项目采购数据表对应的后台service
 * 
 * @author Epoint
 * @version [版本号, 2024-06-07 14:18:09]
 */
public class AssetinfoService
{
    /**
     * 数据增删改查组件
     */
    protected ICommonDao baseDao;

    public AssetinfoService() {
        baseDao = CommonDao.getInstance();
    }

    /**
     * 插入数据
     * 
     * @param record
     *            BaseEntity或Record对象 <必须继承Record>
     * @return int
     */
    public int insert(Assetinfo record) {
        return baseDao.insert(record);
    }

    /**
     * 删除数据
     * 
     * @param guid
     *            主键guid
     * @return int
     */
    public <T extends Record> int deleteByGuid(String guid) {
        T t = baseDao.find(Assetinfo.class, guid);
        return baseDao.delete(t);
    }

    /**
     * 更新数据
     * 
     * @param record
     *            BaseEntity或Record对象 <必须继承Record>
     * @return int
     */
    public int update(Assetinfo record) {
        return baseDao.update(record);
    }

    /**
     * 查询数量
     * 
     * @param conditionMap
     *            查询条件集合
     * @return Integer
     */
    public Integer countAssetinfo(Map<String, Object> conditionMap) {
        SqlConditionUtil conditionUtil = new SqlConditionUtil(conditionMap);
        conditionUtil.setSelectFields("count(*)");
        Parameter parameter = getSql(conditionUtil.getMap());
        return baseDao.queryInt(parameter.getSql(), parameter.getValue());
    }

    /**
     * 根据ID查找单个实体
     * 
     * @param primaryKey
     *            主键
     * @return T extends BaseEntity
     */
    public Assetinfo find(Object primaryKey) {
        return baseDao.find(Assetinfo.class, primaryKey);
    }

    /**
     * 查找单条记录
     * 
     * @param conditionMap
     *            查询条件集合
     * @return T {String、Integer、Long、Record、FrameOu、Object[]等}
     */
    public Assetinfo find(Map<String, Object> conditionMap) {
        Parameter parameter = getSql(conditionMap);
        return baseDao.find(parameter.getSql(), Assetinfo.class, parameter.getValue());
    }

    /**
     * 查找一个list
     * 
     * @param conditionMap
     *            查询条件集合
     * @return T extends BaseEntity
     */
    public List<Assetinfo> findList(Map<String, Object> conditionMap) {
        Parameter parameter = getSql(conditionMap);
        return baseDao.findList(parameter.getSql(), Assetinfo.class, parameter.getValue());
    }

    /**
     * 分页查找一个list
     * 
     * @param conditionMap
     *            查询条件集合
     * @param pageNumber
     *            记录行的偏移量
     * @param pageSize
     *            记录行的最大数目
     * @return T extends BaseEntity
     */
    public PageData<Assetinfo> paginatorList(Map<String, Object> conditionMap, int pageNumber, int pageSize) {
        Parameter parameter = getSql(conditionMap);
        List<Assetinfo> list = baseDao.findList(parameter.getSql(), pageNumber, pageSize, Assetinfo.class,
                parameter.getValue());
        int count = countAssetinfo(conditionMap);
        return new PageData<Assetinfo>(list, count);
    }

    /**
     * 拼接sql
     * 
     * @param conditionMap
     *            查询条件集合
     * @return Parameter
     */
    private Parameter getSql(Map<String, Object> conditionMap) {
        List<Object> params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : conditionMap.entrySet()) {
            if (entry.getKey().contains("leftlike")) {
                conditionMap.put(entry.getKey(), SearchParamUtil.leftLikeDeal((String) entry.getValue()));
            }
            else if (entry.getKey().contains("rightlike")) {
                conditionMap.put(entry.getKey(), SearchParamUtil.rightLikeDeal((String) entry.getValue()));
            }
            else if (entry.getKey().contains("like")) {
                conditionMap.put(entry.getKey(), SearchParamUtil.bothSidesLikeDeal((String) entry.getValue()));
            }
        }
        String sql = new SqlHelper().getSqlComplete(Assetinfo.class, conditionMap, params);

        Parameter parameter = new Parameter();
        parameter.setSql(sql);
        parameter.setParamValue(params);
        return parameter;
    }

    /////////////////////////////////////////////////////////////////////

    /**
     * 更新预算已使用额
     * 
     * @param assetrowguid
     */
    public void updateUsedMoney(String assetrowguid) {
        Assetinfo assetinfo = find(assetrowguid);
        if (StringUtil.isNotBlank(assetinfo.getBudgetguid())) {
            Projectbudgetinfo projectbudgetinfo = baseDao.find(Projectbudgetinfo.class, assetinfo.getBudgetguid());
            projectbudgetinfo.setUsedbudgetmoney(projectbudgetinfo.getUsedbudgetmoney() + assetinfo.getTotalmoney());
            baseDao.update(projectbudgetinfo);
        }
    }

    /**
     * 更新业务表流程状态
     * 
     * @param assetrowguid
     * @param status
     */
    public void updateStatus(String assetrowguid, String status) {
        Assetinfo assetinfo = find(assetrowguid);
        assetinfo.setStatus(status);
        baseDao.update(assetinfo);
    }

    /**
     * 查询项目经理
     * 
     * @param assetrowguid
     * @return
     */
    public String findProjectManager(String assetrowguid) {
        Assetinfo assetinfo = find(assetrowguid);
        if (null != assetinfo) {
            Projectbudgetinfo projectbudgetinfo = baseDao.find(Projectbudgetinfo.class, assetinfo.getBudgetguid());
            Projectinfo projectinfo = baseDao.find(Projectinfo.class, projectbudgetinfo.getProjectguid());
            return projectinfo.getProjectmanager();
        }
        return "";
    }

    /**
     * 超限额
     * 
     * @param assetrowguid
     * @return
     */
    public boolean overMoney(String assetrowguid) {
        Assetinfo assetinfo = find(assetrowguid);
        IConfigService configService = ContainerFactory.getContainInfo().getComponent(IConfigService.class);
        String limitString = configService.getFrameConfigValue("LimitMoney");
        return assetinfo.getTotalmoney() > Integer.parseInt(limitString);
    }

    public boolean lowMoney(String assetrowguid) {
        return !overMoney(assetrowguid);
    }

    public void removeUsedMoney(String assetrowguid) {
        Assetinfo assetinfo = find(assetrowguid);
        if (StringUtil.isNotBlank(assetinfo.getBudgetguid())) {
            Projectbudgetinfo projectbudgetinfo = baseDao.find(Projectbudgetinfo.class, assetinfo.getBudgetguid());
            projectbudgetinfo.setUsedbudgetmoney(projectbudgetinfo.getUsedbudgetmoney() - assetinfo.getTotalmoney());
            baseDao.update(projectbudgetinfo);

        }
    }

}
