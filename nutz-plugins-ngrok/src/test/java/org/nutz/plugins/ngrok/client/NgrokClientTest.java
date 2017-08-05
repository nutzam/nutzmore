package org.nutz.plugins.ngrok.client;

import org.junit.Test;
import org.nutz.lang.Lang;

public class NgrokClientTest {

	@Test
	public void testStart() {
		while (true) {
			final NgrokClient client = new NgrokClient();
			client.auth_token = "s67r4ilqh6jh3r4v17lggrb93";
			new Thread() {
				@Override
				public void run() {
					client.start();
					Lang.quiteSleep(1000);
					client.stop();
				};
			}.start();
			Lang.quiteSleep(10);
		}
	}

}
