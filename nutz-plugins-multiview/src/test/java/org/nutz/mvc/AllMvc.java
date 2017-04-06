package org.nutz.mvc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.mvc.testapp.AllApp;
import org.nutz.mvc.view.AllView;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AllView.class,
                        AllApp.class})
public class AllMvc {}
