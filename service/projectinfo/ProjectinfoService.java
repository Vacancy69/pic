package com.epoint.xmcg.projectinfo.impl;

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
import com.epoint.frame.service.organ.user.api.IUserService;
import com.epoint.xmcg.projectinfo.api.entity.Projectinfo;

/**
 * 项目信息表对应的后台service
 * 
 * @author Epoint
 * @version [版本号, 2024-06-05 16:03:42]
 */
public class ProjectinfoService
{
    /**
     * 数据增删改查组件
     */
    protected ICommonDao baseDao;

    public ProjectinfoService() {
        baseDao = CommonDao.getInstance();
    }

    /**
     * 插入数据
     * 
     * @param record
     *            BaseEntity或Record对象 <必须继承Record>
     * @return int
     */
    public int insert(Projectinfo record) {
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
        T t = baseDao.find(Projectinfo.class, guid);
        return baseDao.delete(t);
    }

    /**
     * 更新数据
     * 
     * @param record
     *            BaseEntity或Record对象 <必须继承Record>
     * @return int
     */
    public int update(Projectinfo record) {
        return baseDao.update(record);
    }

    /**
     * 查询数量
     * 
     * @param conditionMap
     *            查询条件集合
     * @return Integer
     */
    public Integer countProjectinfo(Map<String, Object> conditionMap) {
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
    public Projectinfo find(Object primaryKey) {
        return baseDao.find(Projectinfo.class, primaryKey);
    }

    /**
     * 查找单条记录
     * 
     * @param conditionMap
     *            查询条件集合
     * @return T {String、Integer、Long、Record、FrameOu、Object[]等}
     */
    public Projectinfo find(Map<String, Object> conditionMap) {
        Parameter parameter = getSql(conditionMap);
        return baseDao.find(parameter.getSql(), Projectinfo.class, parameter.getValue());
    }

    /**
     * 查找一个list
     * 
     * @param conditionMap
     *            查询条件集合
     * @return T extends BaseEntity
     */
    public List<Projectinfo> findList(Map<String, Object> conditionMap) {
        Parameter parameter = getSql(conditionMap);
        return baseDao.findList(parameter.getSql(), Projectinfo.class, parameter.getValue());
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
    public PageData<Projectinfo> paginatorList(Map<String, Object> conditionMap, int pageNumber, int pageSize) {
        Parameter parameter = getSql(conditionMap);
        List<Projectinfo> list = baseDao.findList(parameter.getSql(), pageNumber, pageSize, Projectinfo.class,
                parameter.getValue());
        int count = countProjectinfo(conditionMap);
        return new PageData<Projectinfo>(list, count);
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
        String sql = new SqlHelper().getSqlComplete(Projectinfo.class, conditionMap, params);

        Parameter parameter = new Parameter();
        parameter.setSql(sql);
        parameter.setParamValue(params);
        return parameter;
    }

    /**
     * 判断当前项目名称是否已存在
     * 
     * @param projectname
     * @return
     */
    public Projectinfo findProjectinfoByProjectname(String projectname) {
        String sql = "select * from projectinfo where projectname = ?";
        return baseDao.find(sql, Projectinfo.class, projectname);
    }

    /**
     * 修改页重名认证
     * 
     * @param projectNameOld
     * @param projectNameNew
     * @return
     */
    public Projectinfo findProjectinfoByProjectname(String projectNameOld, String projectNameNew) {
        String sql = "select * from projectinfo where projectname != ? and projectname = ?";
        return baseDao.find(sql, Projectinfo.class, projectNameOld, projectNameNew);
    }

    /**
     * 根据userGuid获取所有的UserName
     * 
     * @param allUserGuid
     * @return
     */
    public String getUserAllNamesByUserGuid(String allUserGuid) {
        // 获取项目人员姓名
        String[] projectUserGuidSplit = allUserGuid.split(";");
        List<String> userList = new ArrayList<>();

        IUserService userService = ContainerFactory.getContainInfo().getComponent(IUserService.class);

        for (String str : projectUserGuidSplit) {
            userList.add(userService.getUserNameByUserGuid(str));
        }
        return StringUtil.join(userList, ";");
    }

}
