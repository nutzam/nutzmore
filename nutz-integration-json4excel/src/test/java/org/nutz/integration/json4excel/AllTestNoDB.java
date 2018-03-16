package org.nutz.integration.json4excel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.integration.json4excel.issue.HuromRun;
import org.nutz.integration.json4excel.issue.IssueRun;

@RunWith(Suite.class)
@Suite.SuiteClasses({J4EConfTest.class,
                     J4ETest.class,
                     J4ETest2007.class,
                     IssueRun.class,
                     HuromRun.class})
public class AllTestNoDB {}
