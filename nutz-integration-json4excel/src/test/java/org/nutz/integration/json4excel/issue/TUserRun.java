package org.nutz.integration.json4excel.issue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.integration.json4excel.J4E;
import org.nutz.integration.json4excel.J4EColumn;
import org.nutz.integration.json4excel.J4EConf;
import org.nutz.integration.json4excel.bean.TUser;
import org.nutz.lang.Files;
import org.nutz.lang.random.R;
import org.nutz.lang.util.Disks;

public class TUserRun {

    private TUser getUser() {
        TUser user = new TUser();
        user.name = R.captchaChar(3);
        user.alias = R.captchaChar(5);
        user.remark = R.captchaChar(10, false);
        user.age = R.random(2, 30);
        return user;
    }

    @Test
    public void noData() throws Exception {
        List<TUser> uList = new ArrayList<TUser>();
        uList.add(getUser());
        uList.add(getUser());
        uList.add(getUser());
        J4E.toExcel(Files.createFileIfNoExists2(Disks.normalize("~/tuserNoData.xls")), uList, null);
    }

    @Test
    public void withNameAndAge() throws Exception {
        List<TUser> uList = new ArrayList<TUser>();
        uList.add(getUser());
        uList.add(getUser());
        uList.add(getUser());

        J4EConf j4c = J4EConf.from(TUser.class);
        for (J4EColumn column : j4c.getColumns()) {
            if (column.getFieldName().equals("name")) {
                column.setIgnore(false);
            }
            if (column.getFieldName().equals("age")) {
                column.setIgnore(false);
            }
        }
        J4E.toExcel(Files.createFileIfNoExists2(Disks.normalize("~/tuserWithNameAge.xls")),
                    uList,
                    j4c);
    }

    @Test
    public void withAll() throws Exception {
        List<TUser> uList = new ArrayList<TUser>();
        uList.add(getUser());
        uList.add(getUser());
        uList.add(getUser());

        J4EConf j4c = J4EConf.from(TUser.class);
        for (J4EColumn column : j4c.getColumns()) {
            column.setIgnore(false);
        }
        J4E.toExcel(Files.createFileIfNoExists2(Disks.normalize("~/tuserAll.xls")), uList, j4c);
    }

}
