package org.nutz.integration.jsr303;

/**
 * jsr303与nutz mvc的集成. 分别为ValidationActionFilter和ValidationProcessor,两种均可,但不应该同时使用
 * <p/>ValidationActionFilter每次校验前都需要查询是否带ValidationResult参数,计算量偏大
 * <p/>ValidationProcessor使用一个ConcurrentHashMap保存预处理的结果,会降低并发性
 * <p/>也许,你能设计出更好的方案,欢迎报issue提交代码
 * <p/>
 */