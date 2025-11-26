package cn.smallyoung.springbootdemo.user.service;


import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.smallyoung.springbootdemo.base.BaseService;
import cn.smallyoung.springbootdemo.user.entity.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author smallyoung
 */

@Slf4j
@Service
@Transactional(readOnly = true)
public class PermissionService  extends BaseService<Permission, String> {

    /**
     * 生成权限树
     *
     * @param map 查询条件
     * @return 权限树
     */
    public List<Tree<String>> toTree(Map<String, Object> map) {
        List<Permission> permissions = super.findAll(map);
        if (CollectionUtils.isEmpty(permissions)) {
            return new ArrayList<>();
        }
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setWeightKey("orderNum");
        return TreeUtil.build(permissions, "0", treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId());
                    tree.setParentId(treeNode.getParentId());
                    tree.setName(treeNode.getName());
                    tree.putExtra("val", treeNode.getVal());
                    tree.putExtra("jumpPath", treeNode.getJumpPath());
                    tree.putExtra("identification", treeNode.getIdentification());
                    tree.putExtra("type", treeNode.getType());
                    tree.putExtra("icon", treeNode.getIcon());
                    tree.putExtra("remark", treeNode.getRemark());
                    tree.setWeight(treeNode.getOrderNum());
                });
    }

}
