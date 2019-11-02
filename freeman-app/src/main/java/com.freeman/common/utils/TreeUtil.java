package com.freeman.common.utils;



import com.freeman.common.base.domain.Tree;
import com.freeman.common.router.RouterMeta;
import com.freeman.common.router.VueRouter;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TreeUtil {


    private final static Long TOP_NODE_ID = 0L;

    /** 构建菜单或部门树 */
    public static Tree build(List<Tree> nodes) {
        if (ObjectUtils.isEmpty(nodes)) return null;

        Tree root = new Tree();
        root.setLabel("root");
        root.setId(TOP_NODE_ID);
        root.setParentId(null);
        root.setHasParent(false);
        root.setHasChildren(true);
        root.setIds(nodes.stream().map(Tree::getId).collect(Collectors.toList()));
        root.setChildren(build(nodes,TOP_NODE_ID));
        return root;
    }

    /**
     * 构建树结构(可以是不完整的数据)
     * @author 刘志新
     * @email  lzxorz@163.com
     * @date   19-7-13 下午10:03
     * @Param
     * @return
     **/
   private static List<Tree> build(List<Tree> nodes,Long pid){
       if (ObjectUtils.isEmpty(nodes) || pid == null) return null;

       List<Tree> resultList = new ArrayList();
       // 构建一个Map,把所有树节点的ID作为Key,树节点对象作为value
       Map<Long, Tree> map = new LinkedHashMap();
       for (Tree node : nodes) {
           node.setKey(node.getId());
           node.setValue(node.getId());
           node.setTitle(node.getLabel());
           map.put(node.getId(), node);
       }

       for (Map.Entry<Long, Tree> entry : map.entrySet()) {
           Tree node = entry.getValue();
           if (pid.equals(node.getParentId())) {
               // 是顶层节点,直接添加到结果集合中
               resultList.add(node);
               continue;
           }

           Tree parent = map.get(node.getParentId());
           // 能找到找父节点,添加到父节点的子节点集合中
           if (parent != null) {
               parent.addChild(node);
               continue;
           }

           // 不是顶层节点,没找到父节点,也直接添加得到集合中
           resultList.add(node);
       }

       return resultList;
   }



    /** 构造前端路由 */
    public static List<VueRouter> buildVueRouter(List<VueRouter> routes) {
        if (ObjectUtils.isEmpty(routes)) return null;

        List<VueRouter> resultList = new ArrayList();

        VueRouter root = VueRouter.builder().name("主页").path("/").component("MenuView").redirect("/home").icon("none").build();
        resultList.add(root);
        resultList.add(VueRouter.builder().name("404").path("*").component("error/404").build());

        List<VueRouter> topRoutes = new ArrayList();

        topRoutes.add(VueRouter.builder().name("个人中心").path("/profile").component("personal/Profile").icon("none")
                .meta(new RouterMeta(true, false)).build());

        topRoutes.add(VueRouter.builder().name("系统主页").path("/home").component("HomePageView").icon("home")
                .children(null).meta(new RouterMeta(false, true)).build());

        topRoutes.addAll(buildVueRouter(routes, TOP_NODE_ID));

        root.setChildren(topRoutes);

        return resultList;
    }


    /**
     * 解析为嵌套结构 前端路由
     * @author 刘志新
     * @email  lzxorz@163.com
     * @date   19-7-12 上午11:02
     * @Param  [nodes 无嵌套List集合, pid  可以指定父id(默认0)]
     * @return
     **/
    private static List<VueRouter> buildVueRouter(List<VueRouter> nodes, Long pid){
        if (ObjectUtils.isEmpty(nodes) || pid == null) return null;

        //定义本次递归结果集
        List<VueRouter> resultList = new ArrayList();
        // 构建一个Map,把所有树节点的ID作为Key,树节点对象作为value
        Map<Long, VueRouter> map = new LinkedHashMap();
        for (VueRouter node : nodes) {
            map.put(node.getId(), node);
        }

        for (Map.Entry<Long, VueRouter> entry : map.entrySet()) {
            VueRouter node = entry.getValue();
            // 是顶层节点,直接添加到结果集合中
            if (pid.equals(node.getParentId())) {
                node.setHasParent(true);
                resultList.add(node);
                continue;
            }

            VueRouter parent = map.get(node.getParentId());
            // 能找到找父节点,添加到父节点的子节点集合中
            if (parent != null) {
                parent.setHasChildren(true);
                parent.addChild(node);
                continue;
            }

            // 不是顶层节点,没找到父节点,也直接添加得到集合中
            resultList.add(node);
        }

        return resultList;
    }
}