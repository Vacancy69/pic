package com.epoint.xmcg.projectbudgetinfo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.epoint.core.dao.CommonDao;
import com.epoint.core.dao.ICommonDao;
import com.epoint.core.grammar.Record;
import com.epoint.core.utils.SearchParamUtil;
import com.epoint.core.utils.sql.SqlConditionUtil;
import com.epoint.core.utils.sql.SqlHelper;
import com.epoint.database.jdbc.Parameter;
import com.epoint.database.peisistence.crud.impl.model.PageData;
import com.epoint.xmcg.projectbudgetinfo.api.entity.Projectbudgetinfo;

/**
 * 项目预算信息表对应的后台service
 * 
 * @author Epoint
 * @version [版本号, 2024-06-05 16:03:53]
 */
public class ProjectbudgetinfoService
{
    /**
     * 数据增删改查组件
     */
    protected ICommonDao baseDao;

    public ProjectbudgetinfoService() {
        baseDao = CommonDao.getInstance();
    }

    /**
     * 插入数据
     * 
     * @param record
     *            BaseEntity或Record对象 <必须继承Record>
     * @return int
     */
    public int insert(Projectbudgetinfo record) {
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
        T t = baseDao.find(Projectbudgetinfo.class, guid);
        return baseDao.delete(t);
    }

    /**
     * 更新数据
     * 
     * @param record
     *            BaseEntity或Record对象 <必须继承Record>
     * @return int
     */
    public int update(Projectbudgetinfo record) {
        return baseDao.update(record);
    }

    /**
     * 查询数量
     * 
     * @param conditionMap
     *            查询条件集合
     * @return Integer
     */
    public Integer countProjectbudgetinfo(Map<String, Object> conditionMap) {
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
    public Projectbudgetinfo find(Object primaryKey) {
        return baseDao.find(Projectbudgetinfo.class, primaryKey);
    }

    /**
     * 查找单条记录
     * 
     * @param conditionMap
     *            查询条件集合
     * @return T {String、Integer、Long、Record、FrameOu、Object[]等}
     */
    public Projectbudgetinfo find(Map<String, Object> conditionMap) {
        Parameter parameter = getSql(conditionMap);
        return baseDao.find(parameter.getSql(), Projectbudgetinfo.class, parameter.getValue());
    }

    /**
     * 查找一个list
     * 
     * @param conditionMap
     *            查询条件集合
     * @return T extends BaseEntity
     */
    public List<Projectbudgetinfo> findList(Map<String, Object> conditionMap) {
        Parameter parameter = getSql(conditionMap);
        return baseDao.findList(parameter.getSql(), Projectbudgetinfo.class, parameter.getValue());
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
    public PageData<Projectbudgetinfo> paginatorList(Map<String, Object> conditionMap, int pageNumber, int pageSize) {
        Parameter parameter = getSql(conditionMap);
        List<Projectbudgetinfo> list = baseDao.findList(parameter.getSql(), pageNumber, pageSize,
                Projectbudgetinfo.class, parameter.getValue());
        int count = countProjectbudgetinfo(conditionMap);
        return new PageData<Projectbudgetinfo>(list, count);
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
        String sql = new SqlHelper().getSqlComplete(Projectbudgetinfo.class, conditionMap, params);

        Parameter parameter = new Parameter();
        parameter.setSql(sql);
        parameter.setParamValue(params);
        return parameter;
    }

    /**
     * 根据项目guid获取当前项目已有的预算总额
     * 
     * @param projectGuid
     * @return
     */
    public int getMoneyBefore(String projectGuid) {
        String sql = "select ifnull(sum(budgetmoney),0) from projectbudgetinfo where projectguid = ?";
        return baseDao.queryInt(sql, projectGuid);
    }

    public List<Projectbudgetinfo> getInfosByProjectName(String projectName) {
        String sql = "select budgetname,budgettype,budgetmoney,usedbudgetmoney"
                + " from projectbudgetinfo p left outer join projectinfo p2 on"
                + " p.projectguid = p2.rowguid where p2.projectname = ?";
        return baseDao.findList(sql, Projectbudgetinfo.class, projectName);
    }

}
