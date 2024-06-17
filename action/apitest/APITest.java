package com.epoint.xmcg.apitest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.epoint.basic.controller.BaseController;
import com.epoint.core.utils.classpath.ClassPathUtil;
import com.epoint.core.utils.code.EncodeUtil;
import com.epoint.core.utils.date.EpointDateUtil;
import com.epoint.core.utils.file.FileManagerUtil;
import com.epoint.core.utils.json.JsonUtil;
import com.epoint.core.utils.string.StringUtil;
import com.epoint.frame.service.attach.api.IAttachService;
import com.epoint.frame.service.metadata.code.api.ICodeItemsService;
import com.epoint.frame.service.metadata.code.entity.CodeItems;
import com.epoint.frame.service.organ.ou.api.IOuService;
import com.epoint.frame.service.organ.ou.entity.FrameOu;
import com.epoint.frame.service.organ.ou.entity.FrameOuExtendInfo;
import com.epoint.frame.service.organ.role.api.IRoleService;
import com.epoint.frame.service.organ.user.api.IUserService;
import com.epoint.frame.service.organ.userrole.api.IUserRoleRelationService;
import com.epoint.frame.service.organ.userrole.entity.FrameUserRoleRelation;
import com.epoint.xmcg.projectbudgetinfo.api.entity.Projectbudgetinfo;

@RestController("apitest")
@Scope("request")
public class APITest extends BaseController
{

    @Resource
    private IAttachService attachService;

    @Resource
    private IUserService userService;

    @Resource
    private IRoleService roleService;

    @Autowired
    private IUserRoleRelationService relationService;

    @Resource
    private IOuService ouService;

    @Resource
    private ICodeItemsService codeItemService;

    @Override
    public void pageLoad() {
        // TODO Auto-generated method stub
        // System.out.println("进入测试");
        //
        // // 通过附件标识获取附件信息
        // FrameAttachInfo attachInfo =
        // attachService.getAttachInfoDetail("4eb311e6-8224-4c77-9d9a-ff2521bcec85");
        // System.out.println("attachInfo.getAttachFileName()" +
        // attachInfo.getAttachFileName());
        //
        // System.out.println("////////////////////////////////////////////////////////////////////////////////////");
        //
        // // 通过附件信息获取附件预览链接（该链接携带了token信息 以通过登录信息校验 供其他服务器使用）
        // String attachPreviewDwonPath =
        // attachService.getAttachPreviewDownPath(attachInfo);
        // System.out.println("attachPreviewDwonPath" + attachPreviewDwonPath);
        //
        // System.out.println("////////////////////////////////////////////////////////////////////////////////////");
        //
        // // 根据业务标识查询附件列表，当我们需要查看某个业务都关联了哪些附件时，可用此方法。
        // // clientGuid即业务标识，例如我要查看我的用户头像附件，clientGuid就是我的用户标识
        // List<FrameAttachInfo> attachInfoList = attachService
        // .getAttachInfoListByGuid("c2509d7a-5386-4175-8e49-219142b7345a");
        // attachInfoList.forEach(System.out::println);
        //
        // System.out.println("////////////////////////////////////////////////////////////////////////////////////");
        //
        // // 判断附件信息对应的物理附件是否存在
        // Boolean isExist = attachService.checkStorageExist(attachInfo);
        // System.out.println("isExist" + isExist);
        //
        // /////////////////////////////////////////////////////////////////////
        //
        // // 根据自定义条件查询用户信息，左值是用户表的字段名称，右值是字段对应的值，当传参不确定时，可以用该方法。
        // // 要求查询的字段值必须是唯一的，如果不唯一那么返回第一条记录
        // FrameUser userByUser = userService.getUserByUserField("loginid",
        // "admin");
        // System.out.println("userByUser" + userByUser);
        //
        // System.out.println("////////////////////////////////////////////////////////////////////////////////////");
        //
        // // 有时候我们不需要那么多的信息，只需要获得用户名即可，那么api也提供了专门的方法提高查询效率。
        // String userName =
        // userService.getUserNameByUserGuid("45f0c5f9-cad2-49e6-887d-b38dfcbc23de");
        // System.out.println("userName" + userName);
        //
        // System.out.println("////////////////////////////////////////////////////////////////////////////////////");
        //
        // // 获取用户详细信息，如手机号、生日、邮箱等
        // FrameUserExtendInfo userExtendInfo =
        // userService.getUserExtendInfoByUserGuid("userGuid");
        // // System.out.println(userExtendInfo.getQqnumber());
        //
        // System.out.println("////////////////////////////////////////////////////////////////////////////////////");
        //
        // // 根据用户标识获取该用户的所有兼职部门信息
        // List<FrameUserSecondOU> secondOuList = userService
        // .listFrameUserSecondOU("10c628e9-4472-42d8-a721-c331d29096de");
        // secondOuList.forEach(System.out::println);
        //
        // System.out.println("////////////////////////////////////////////////////////////////////////////////////");
        //
        // // 根据角色字段查询出角色(要求查询的字段值必须是唯一的,如果不唯一那么返回第一条记录)
        // FrameRole role = roleService.getRoleByRoleField("roleGuid",
        // "22cbe1b4-a55a-484c-b57d-71525682b622");
        // System.out.println(role);
        //
        // System.out.println("////////////////////////////////////////////////////////////////////////////////////");
        //
        // // 判断用户是否是系统管理员
        // Boolean isAdmin =
        // roleService.isAdmin("45f0c5f9-cad2-49e6-887d-b38dfcbc23de");
        // System.out.println(isAdmin);
        //
        // System.out.println("////////////////////////////////////////////////////////////////////////////////////");
        //
        // // 根据角色类别取得角色列表 baseOuGuid(不是独立部门不需要填，写空字符串即可);
        // List<FrameRole> roles =
        // roleService.listRoleByRoleTypeGuid("d64fb3a8-62d5-4de3-8638-7ac671fa37db",
        // "");
        // for (FrameRole frameRole : roles) {
        // System.out.println(frameRole.getRoleName());
        // }
        //
        // System.out.println("////////////////////////////////////////////////////////////////////////////////////");

        // 根据用户标识获取用户角色关系
        List<FrameUserRoleRelation> relation = relationService
                .getUserRoleRelationByUserGuid("45f0c5f9-cad2-49e6-887d-b38dfcbc23de");
        for (FrameUserRoleRelation userRoleRelation : relation) {
            System.out
                    .println(roleService.getRoleByRoleField("roleGuid", userRoleRelation.getRoleGuid()).getRoleName());
        }

        // 通过部门标识获取部门详细信息
        FrameOuExtendInfo ouExtendInfo = ouService.getFrameOuExtendInfo("9579bbf9-31d0-4548-b78f-ea4392bf68f9");
        System.out.println(ouExtendInfo);

        // 通过部门标识获取部门基本信息
        FrameOu frameOu1 = ouService.getOuByOuGuid("9579bbf9-31d0-4548-b78f-ea4392bf68f9");
        System.out.println(frameOu1);

        // 通过用户标识获取该用户所在的部门信息
        FrameOu frameOu = ouService.getOuByUserGuid("45f0c5f9-cad2-49e6-887d-b38dfcbc23de");
        System.out.println(frameOu);

        // 通过部门标识获取该用户所在部门名称，有很多场景我们不需要那么多信息，那么可以使用该方法提高效率
        String ouName = ouService.getOuNameByUserGuid("45f0c5f9-cad2-49e6-887d-b38dfcbc23de");
        System.out.println(ouName);

        // 通过部门标识获取当前部门的最上级部门
        FrameOu parentOu = ouService.getTopParentOu("9579bbf9-31d0-4548-b78f-ea4392bf68f9");
        System.out.println(parentOu);

        // 根据代码id和代码项值查询代码项
        CodeItems item = codeItemService.getCodeItemByCodeID("20", "1010");
        System.out.println(item);

        // 根据代码名称和代码项值查询代码项
        CodeItems item1 = codeItemService.getCodeItemByCodeName("采购类型", "40");
        System.out.println(item1);

        // 根据代码id获取该代码下所有代码项。
        List<CodeItems> codeItems = codeItemService.listCodeItemsByCodeName("所属地区");
        codeItems.forEach(System.out::println);

        // 根据代码ID以及代码项值获取代码项文本
        String itemtext = codeItemService.getItemTextByCodeID("19", "10101010");
        System.out.println(itemtext);
        String value = codeItemService.getItemValueByCodeName("申请状态", "审核通过");
        System.out.println(value);

        // 判断字符串是否为空
        System.out.println(StringUtil.isNotBlank("str"));

        // 字符串转小写
        System.out.println(StringUtil.toLowerCase("str"));
        // 字符串转大写
        System.out.println(StringUtil.toUpperCase("str"));
        // 首字母大写
        System.out.println(StringUtil.firstCharToUpperCase("str"));
        // 首字母小写
        StringUtil.firstCharToLowerCase("str");

        // 使用utf-8编码
        System.out.println(EncodeUtil.encode("str"));
        // 解码页面回传参数
        System.out.println(EncodeUtil.decode("str"));

        // List转JSON
        List<String> list = Arrays.asList("a", "b", "c");
        String json = JsonUtil.listToJson(list);
        System.out.println(json);

        // JSON字符串转集合
        String jsonString = "[{\"budgettype\":\"项目采购\",\"usedbudgetmoney\":31,\"budgetmoney\":100,\"budgetname\":\"项目一预算一\"},{\"budgettype\":\"差旅费\",\"usedbudgetmoney\":3,\"budgetmoney\":10,\"budgetname\":\"项目一预算二\"}]";
        List<Projectbudgetinfo> projectbudgetinfos = JsonUtil.jsonToList(jsonString, Projectbudgetinfo.class);
        System.out.println(projectbudgetinfos);

        // 判断字符串是否是JSON格式
        String str = "[1,2,3]";
        boolean isJson = JsonUtil.isJson(str);
        System.out.println(isJson); // 输出: true

        // JSON字符串转换成Map
        String jsonMapString = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        Map<String, Object> map = JsonUtil.jsonToMap(jsonMapString);
        System.out.println(map);

        // 获取系统文件内容，填写全路径
        byte[] contentFromSystem = FileManagerUtil.getContentFromSystem("path");

        // 把字符串转换为日期 (默认格式yyyy-MM-dd HH:mm:ss)
        System.out.println(EpointDateUtil.convertString2Date("2024-01-01 00:00:00"));

        // 获取编译路径
        System.out.println(ClassPathUtil.getClassesPath());
        // 获取应用部署路径
        System.out.println(ClassPathUtil.getDeployWarPath());
        // 获取web应用名称
        System.out.println(ClassPathUtil.getWebContext());
        // 获取jar包路径
        System.out.println(ClassPathUtil.getLibPath());

        // 获取系统参数;
        String configValue = configService.getFrameConfigValue("limitMoney");
        System.out.println(configValue);
        // 获取系统参数值，先从配置文件获取，再从系统参数获取
        String configValue2 = configService.getFrameConfigPropertiesPrior("limitMoney");
        System.out.println(configValue2);
        // 获取系统参数值，如果没有，则返回默认值“defaultValue";
        String configValue3 = configService.getFrameConfigValueByNameWithDefault("limitMoney", "50000");
        System.out.println(configValue3);
    }

}
