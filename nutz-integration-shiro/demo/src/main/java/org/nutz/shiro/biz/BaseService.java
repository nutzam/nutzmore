package org.nutz.shiro.biz;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.DaoException;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Logs;
import org.nutz.service.IdNameEntityService;
import org.nutz.shiro.bean.Entity;

/**
 * @author admin
 *
 * @email kerbores@gmail.com
 *
 */
@IocBean(fields = { "dao" })
public class BaseService<T extends Entity> extends IdNameEntityService<T> {

	@Inject("config")
	protected PropertiesProxy config;

	protected int PAGESIZE = config() == null ? 15 : config().getInt("pageSize", 15);

	public PropertiesProxy config() {
		return config;
	}

	// C
	/**
	 * 保存实体
	 * 
	 * @param t
	 *            待保存实体
	 * @return 保存后的实体,根据配置可能将产生 id 等其他属性
	 */
	public T save(T t) {

		return dao().insert(t);
	}

	/**
	 * 保存对象指定字段
	 * 
	 * @param obj
	 *            待保存对象
	 * @param filter
	 *            字段过滤器
	 * @return
	 */
	public T save(final T obj, FieldFilter filter) {
		return dao().insert(obj, filter);
	}

	/**
	 * 保存
	 * 
	 * @param tableName
	 *            表名
	 * @param chain
	 *            数据链
	 */
	public void save(String tableName, Chain chain) {
		dao().insert(tableName, chain);
	}

	/**
	 * 保存
	 * 
	 * @param classOfT
	 *            类
	 * @param chain
	 *            数据链
	 */
	public void save(Class<?> classOfT, Chain chain) {
		dao().insert(classOfT, chain);
	}

	/**
	 * 保存
	 * 
	 * @param t
	 *            数据对象
	 * @param ignoreNull
	 *            是否忽略空值
	 * @param ignoreZero
	 *            是否忽略零值
	 * @param ignoreBlankStr
	 *            是否忽略空字符串
	 * @return
	 */
	public T insert(final T t, boolean ignoreNull, boolean ignoreZero, boolean ignoreBlankStr) {
		return dao().insert(t, ignoreNull, ignoreZero, ignoreBlankStr);
	}

	/**
	 * 保存指定字段
	 * 
	 * @param obj
	 *            待保存对象
	 * @param regex
	 *            字段正则
	 * @return
	 */
	public T insertWith(T obj, String regex) {
		return dao().insertWith(obj, regex);
	}

	/**
	 * 保存管理数据
	 * 
	 * @param obj
	 *            对象
	 * @param regex
	 *            管理字段正则
	 * @return
	 */
	public T insertLinks(T obj, String regex) {
		return dao().insertLinks(obj, regex);
	}

	/**
	 * 插入中间表
	 * 
	 * @param obj
	 * @param regex
	 * @return
	 */
	public T insertRelation(T obj, String regex) {
		return dao().insertRelation(obj, regex);
	}

	// R

	// count

	// entity

	/**
	 * 条件查询
	 * 
	 * @param cnd
	 * @return
	 */
	@Override
	public List<T> query(Condition cnd) {
		return dao().query(getEntityClass(), cnd);
	}

	/**
	 * 查询全部
	 * 
	 * @return
	 */
	public List<T> queryAll() {
		return query(null);
	}

	/**
	 * 分页查询
	 * 
	 * @param condition
	 *            条件
	 * @param currentPage
	 *            当前页码
	 * @param pageSize
	 *            页面大小
	 * @return
	 */
	public List<T> query(Condition condition, int currentPage, int pageSize) {
		if (condition == null) {// 不传入条件那么我就设置一个排序条件
			condition = Cnd.orderBy().desc("id");
		}
		org.nutz.dao.pager.Pager pager = dao().createPager(currentPage, pageSize);
		return dao().query(getEntityClass(), condition, pager);
	}

	/**
	 * 分页查询
	 * 
	 * @param condition
	 *            条件
	 * @param currentPage
	 *            页码
	 * @return
	 */
	public List<T> query(Condition condition, int currentPage) {
		return query(condition, currentPage, PAGESIZE);
	}

	/**
	 * 根据指定字段查询(仅限唯一属性,非唯一属性查询第一个满足条件的数据)
	 * 
	 * @param field
	 *            字段
	 * @param value
	 *            值
	 * @return 单个对象
	 */
	public T findByField(String field, Object value) {
		return dao().fetch(getEntityClass(), Cnd.where(field, "=", value));
	}

	/**
	 * 多字段查询
	 * 
	 * @param fieldvaluePairs
	 *            奇数参数为字段,偶数参数为值
	 * @return 单个实体,如果满足条件的有多个,将返回第一个
	 */
	public T findByField(String... fieldvaluePairs) {
		if (fieldvaluePairs.length % 2 != 0) {
			throw new DaoException("The count of parameters must be an even number!");
		}
		Cnd condition = Cnd.NEW();
		for (int i = 0; i < fieldvaluePairs.length; i = i + 2) {
			condition = condition.and(fieldvaluePairs[i], "=", fieldvaluePairs[i + 1]);
		}
		return dao().fetch(getEntityClass(), condition);
	}

	protected Record run(Sql sql, Dao dao) {
		sql.setCallback(Sqls.callback.record());
		dao.execute(sql);
		return sql.getObject(Record.class);
	}

	protected List<Record> runReport(Sql sql, Dao dao) {
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		return sql.getList(Record.class);
	}

	protected T run(Sql sql, Dao dao, Class<T> classOfEntity) {
		sql.setCallback(Sqls.callback.entity());
		dao.execute(sql);
		return sql.getObject(classOfEntity);
	}

	protected List<T> runsql(Sql sql, Dao dao, Class<T> classOfEntity) {
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(classOfEntity));
		dao.execute(sql);
		return sql.getList(classOfEntity);
	}

	/**
	 * 执行更新
	 *
	 * @param sql
	 *            sql接口对象
	 * @param dao
	 *            dao接口实例
	 * @return 影响的记录条数
	 */
	protected int runUpdate(Sql sql, Dao dao) {
		sql.setCallback(Sqls.callback.integer());
		dao.execute(sql);
		return sql.getUpdateCount();
	}

	/**
	 * 执行update语句
	 *
	 * @param query
	 *            sql语句
	 * @param dao
	 *            nutzdao对象
	 * @return 影响的记录条数
	 */
	protected int runUpdate(String query, Dao dao) {
		Sql sql = Sqls.create(query);
		return sql.getUpdateCount();
	}
	// sql

	public Record fetch(Sql sql) {
		return run(sql, dao());
	}

	public List<Record> search(Sql sql) {
		return runReport(sql, dao());
	}

	public List<NutMap> searchAsMap(Sql sql) {
		sql.setCallback(Sqls.callback.maps());
		dao().execute(sql);
		return sql.getList(NutMap.class);
	}

	public T fetchObj(Sql sql) {
		return run(sql, dao(), getEntityClass());
	}

	public List<T> searchObj(Sql sql) {
		return runsql(sql, dao(), getEntityClass());
	}

	public int deleteOrUpdate(Sql sql) {
		return runUpdate(sql, dao());
	}

	/**
	 * 创建sql对象
	 * 
	 * @param key
	 * @return
	 */
	public Sql create(String key) {
		return dao().sqls().create(key);
	}

	// biz

	/**
	 * 分页查询
	 * 
	 * @param page
	 * @return
	 */
	public Pager<T> searchByPage(int page) {
		return searchByPage(page, null);
	}

	/**
	 * 分页查询
	 * 
	 * @param page
	 *            页码
	 * @param condition
	 *            条件
	 * @return 分页对象
	 */
	public Pager<T> searchByPage(int page, Condition condition) {
		return searchByPage(page, PAGESIZE, condition);
	}

	public Pager<T> searchByPage(int page, int pageSize, Condition condition) {
		Pager<T> pager = new Pager<T>(pageSize, page);
		pager.setEntities(query(condition, page, pageSize));
		pager.setCount(count(condition));
		return pager;
	}

	public Pager<T> searchByKeyAndPage(String key, int page, Cnd cnd, String... fields) {
		String searchKey = String.format("%%%s%%", key);
		if (cnd == null) {
			cnd = Cnd.NEW();
		}
		int index = 0;
		for (String field : fields) {
			if (index == 0) {
				cnd = cnd.and(field, "like", searchKey);
			} else {
				cnd = cnd.or(field, "like", searchKey);
			}
			index++;
		}
		Pager<T> pager = new Pager<T>(PAGESIZE, page);
		pager.setEntities(query(cnd, page));
		pager.setCount(count(cnd));
		return pager;
	}

	/**
	 * 关键词搜索
	 * 
	 * @param key
	 *            关键词
	 * @param page
	 *            页码
	 * @param fields
	 *            检索字段列表
	 * @return 分页对象
	 */
	public Pager<T> searchByKeyAndPage(String key, int page, String... fields) {
		return searchByKeyAndPage(key, page, null, fields);
	}

	// U

	public int update(T obj) {
		return dao().update(obj);
	}

	public int update(final T obj, String regex) {
		return dao().update(obj, regex);
	}

	public int updateIgnoreNull(final T obj) {
		return dao().updateIgnoreNull(obj);
	}

	@Override
	public int update(Chain chain, Condition cnd) {
		return dao().update(getEntityClass(), chain, cnd);
	}

	public T updateWith(T obj, final String regex) {
		return dao().updateWith(obj, regex);
	}

	public T updateLinks(T obj, final String regex) {
		return dao().updateLinks(obj, regex);
	}

	@Override
	public int updateRelation(String regex, Chain chain, Condition cnd) {
		return dao().updateRelation(getEntityClass(), regex, chain, cnd);
	}

	public boolean update(T t, String... fields) {
		return dao().update(t.getClass(), makeChain(t, fields), getCnd(t)) == 1;
	}

	private Chain makeChain(T t, String[] fields) {
		NutMap map = NutMap.NEW();
		// 获取数据库字段名称和对象值键值对
		Mirror<T> clazzMirror = Mirror.me(t);// 获取类型的镜像
		for (String field : fields) {
			Field f = null;
			try {
				f = clazzMirror.getField(field);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			if (f != null) {
				map.addv(f.getAnnotation(Column.class).value(), clazzMirror.getValue(t, f));
			}
		}
		return Chain.from(map);
	}

	private Condition getCnd(T t) {
		Mirror<T> clazzMirror = Mirror.me(t);// 获取类型的镜像
		Field idField = null;
		try {
			idField = clazzMirror.getField(Id.class);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		String fieldName = idField.getName();
		Object value = clazzMirror.getValue(t, idField);
		return Cnd.where(fieldName, "=", value);
	}

	public int update(T t, Condition cnd, String... fields) {
		Arrays.sort(fields);
		NutMap map = Lang.map(Json.toJson(t));
		NutMap data = NutMap.NEW();
		for (String key : map.keySet()) {
			if (Arrays.binarySearch(fields, key) >= 0) {
				data.put(key, map.get(key));
			}
		}
		try {
			return dao().update(t.getClass(), Chain.from(data), cnd);
		} catch (Exception e) {
			Logs.get().error(e);
			return 0;
		}
	}

	public int update(T t, Condition cnd) {
		try {
			return dao().update(t.getClass(), Chain.from(t), cnd);
		} catch (Exception e) {
			Logs.get().error(e);
			return 0;
		}
	}

	// D
	/**
	 * 按条件删除数据
	 * 
	 * @return 记录条数
	 */
	@Override
	public int clear(Condition cnd) {
		return dao().clear(getEntityClass(), cnd);
	}

	/**
	 * 清除全部数据
	 * 
	 * @return 记录条数
	 */
	@Override
	public int clear() {
		return clear(null);
	}

	/**
	 * 删除关联数据
	 * 
	 * @param obj
	 * @param regex
	 * @return
	 */
	public T clearLinks(T obj, final String regex) {
		return dao().clearLinks(obj, regex);
	}

	public int delete(T obj) {
		return dao().delete(obj);
	}

	public int deleteWith(T obj, final String regex) {
		return dao().deleteWith(obj, regex);
	}

	public int deleteLinks(T obj, final String regex) {
		return dao().deleteLinks(obj, regex);
	}
}
