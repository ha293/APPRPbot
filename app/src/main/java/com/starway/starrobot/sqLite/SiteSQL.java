package com.starway.starrobot.sqLite;

/**
 * @Author:Edgar.Li
 * @Date:2018-04-09 15:58
 * @version:
 * @purpose:
 * @Description:
 */
public class SiteSQL {
    public String addSite(SiteBean siteBean) {
        /**
         * @Author:Edgar.Li
         * @param siteBean
         * @Date:2018-04-09 16:22
         * @Description: 插入数据的sql语句
         * @return:java.lang.String
         *
         */
        String addSite = "insert into sc(name,place,describe,flag,weight) values(" +
                String.format("'%s'", siteBean.getName()) + "," +
                String.format("'%s'", siteBean.getPlace()) + "," +
                String.format("'%s'", siteBean.getDescribe()) + "," +
                siteBean.getFlag() + "," + siteBean.getWeight() + ");";
        return addSite;
    }

    public String updateSite(SiteBean siteBean) {
        /**
         * @Author:Edgar.Li
         * @param siteBean
         * @Date:2018-04-09 16:23
         * @Description:更新数据的sql语句
         * @return:java.lang.String
         *
         */
        String updateSite = "update sc set name=" +
                String.format("'%s'",siteBean.getName()) + ",place=" +
                String.format("'%s'",siteBean.getPlace()) + ",describe=" +
                String.format("'%s'", siteBean.getDescribe()) + ",flag=" +
                siteBean.getFlag() + ",weight=" + siteBean.getWeight() + " where id=" + siteBean.getId();
        return updateSite;
    }

    public String queryAllSite() {
        /**
         * @Author:Edgar.Li
         * @Date:2018-04-09 16:33
         * @Description:查询所有的数据
         * @return:java.lang.String
         *
         */
        return "select * from sc ";

    }

    public String queryStartSite() {
        /**
         * @Author:Edgar.Li
         * @Date:2018-04-09 16:33
         * @Description:查询起点
         * @return:java.lang.String
         *
         */
        return "select * from sc where flag=1;";

    }

}
