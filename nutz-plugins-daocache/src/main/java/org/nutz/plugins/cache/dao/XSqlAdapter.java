package org.nutz.plugins.cache.dao;

import java.util.List;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface XSqlAdapter extends SQLASTVisitor {

    List<String> getTableNames();
}
