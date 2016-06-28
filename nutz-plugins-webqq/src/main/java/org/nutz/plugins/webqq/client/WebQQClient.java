package org.nutz.plugins.webqq.client;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dongliu.requests.Client;
import net.dongliu.requests.Response;
import net.dongliu.requests.Session;

import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.webqq.callback.MessageCallback;
import org.nutz.plugins.webqq.constant.ApiURL;
import org.nutz.plugins.webqq.model.Category;
import org.nutz.plugins.webqq.model.Discuss;
import org.nutz.plugins.webqq.model.DiscussInfo;
import org.nutz.plugins.webqq.model.DiscussMessage;
import org.nutz.plugins.webqq.model.DiscussUser;
import org.nutz.plugins.webqq.model.Font;
import org.nutz.plugins.webqq.model.Friend;
import org.nutz.plugins.webqq.model.FriendStatus;
import org.nutz.plugins.webqq.model.Group;
import org.nutz.plugins.webqq.model.GroupInfo;
import org.nutz.plugins.webqq.model.GroupMessage;
import org.nutz.plugins.webqq.model.GroupUser;
import org.nutz.plugins.webqq.model.Message;
import org.nutz.plugins.webqq.model.Recent;
import org.nutz.plugins.webqq.model.UserInfo;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-webqq
 *
 * @file WebQQClient.java
 *
 * @description webqq
 *
 *
 * @DateTime 2016年6月27日 下午5:16:31
 *
 */
public class WebQQClient implements Closeable {

	// 日志
	private static final Log LOGGER = Logs.getLog(WebQQClient.class);

	// 消息id，这个好像可以随便设置，所以设成全局的
	private static long MESSAGE_ID = 43690001;

	// 客户端id，固定的
	private static final long Client_ID = 53999199;

	// 鉴权参数
	private String ptwebqq;

	private String vfwebqq;

	private long uin;

	private String psessionid;

	// 客户端
	private Client client;

	// 会话
	private Session session;

	// 线程开关
	private volatile boolean pollStarted;

	public WebQQClient(final MessageCallback callback) {
		this.client = Client.pooled().maxPerRoute(5).maxTotal(10).build();
		this.session = client.session();
		login();
		if (callback != null) {
			this.pollStarted = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						if (!pollStarted) {
							return;
						}
						pollMessage(callback);
					}
				}
			}).start();
		}
	}

	// 线程暂停
	private static void sleep(long seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException ignored) {
		}
	}

	/**
	 * 拉取消息
	 * 
	 * @param callback
	 *            获取消息后的回调
	 */
	private void pollMessage(MessageCallback callback) {
		LOGGER.info("开始接收消息");

		NutMap r = new NutMap();
		r.put("ptwebqq", ptwebqq);
		r.put("clientid", Client_ID);
		r.put("psessionid", psessionid);
		r.put("key", "");

		Response<String> response = post(ApiURL.POLL_MESSAGE, r);
		List<NutMap> array = getJsonArrayResult(response);
		for (int i = 0; array != null && i < array.size(); i++) {
			NutMap message = array.get(i);
			String type = message.getString("poll_type");
			if ("message".equals(type)) {
				callback.onMessage(new Message(message.get("value")));
			} else if ("group_message".equals(type)) {
				callback.onGroupMessage(new GroupMessage(message.get("value")));
			} else if ("discu_message".equals(type)) {
				callback.onDiscussMessage(new DiscussMessage(message.get("value")));
			}
		}
	}

	/**
	 * 登录
	 */
	private void login() {
		getQRCode();
		String url = verifyQRCode();
		getPtwebqq(url);
		fetchVfwebqq();
		getUinAndPsessionid();
	}

	// 登录流程1：获取二维码
	private void getQRCode() {
		LOGGER.info("开始获取二维码");
		// 本地存储二维码图片
		String filePath;
		try {
			filePath = new File("qrcode.png").getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException("二维码保存失败");
		}
		session.get(ApiURL.GET_QR_CODE.getUrl()).addHeader("User-Agent", ApiURL.USER_AGENT).file(filePath);
		LOGGER.info("二维码已保存在 " + filePath + " 文件中，请打开手机QQ并扫描二维码");
	}

	// 登录流程2：校验二维码
	private String verifyQRCode() {
		LOGGER.info("等待扫描二维码");
		// 阻塞直到确认二维码认证成功
		while (true) {
			sleep(1);
			String result = get(ApiURL.VERIFY_QR_CODE).getBody();
			System.err.println(result);
			if (result.contains("成功")) {
				for (String content : result.split("','")) {
					if (content.startsWith("http")) {
						return content;
					}
				}
			} else if (result.contains("已失效")) {
				LOGGER.info("二维码已失效，尝试重新获取二维码");
				getQRCode();
			}
		}

	}

	// 登录流程3：获取ptwebqq
	private void getPtwebqq(String url) {
		LOGGER.info("开始获取ptwebqq");
		Response response = get(ApiURL.GET_PTWEBQQ, url);
		this.ptwebqq = response.getCookies().get("ptwebqq").iterator().next().getValue();
	}

	// 登录流程4：获取vfwebqq
	private void fetchVfwebqq() {
		LOGGER.info("开始获取vfwebqq");

		Response response = get(ApiURL.GET_VFWEBQQ, ptwebqq);
		this.vfwebqq = getJsonObjectResult(response).getString("vfwebqq");
	}

	// 登录流程5：获取uin和psessionid
	private void getUinAndPsessionid() {
		LOGGER.info("开始获取uin和psessionid");

		NutMap r = new NutMap();
		r.put("ptwebqq", ptwebqq);
		r.put("clientid", Client_ID);
		r.put("psessionid", "");
		r.put("status", "online");

		Response response = post(ApiURL.GET_UIN_AND_PSESSIONID, r);
		NutMap result = getJsonObjectResult(response);
		this.psessionid = result.getString("psessionid");
		this.uin = result.getLong("uin");
	}

	// 获取返回json的result字段（JSONObject类型）
	private static NutMap getJsonObjectResult(Response response) {
		return getResponseJson(response).getAs("result", NutMap.class);
	}

	// 获取返回json的result字段（JSONArray类型）
	private static List<NutMap> getJsonArrayResult(Response response) {
		return getResponseJson(response).getList("result", NutMap.class);
	}

	private static NutMap getResponseJson(Response<String> response) {

		if (response.getStatusCode() != 200) {
			throw new RuntimeException(String.format("请求失败，Http返回码[%d]", response.getStatusCode()));
		}
		NutMap data = Lang.map(response.getBody());
		Integer retCode = data.getAs("retcode", Integer.class);
		if (retCode == null || retCode != 0) {
			if (retCode != null && retCode == 103) {
				LOGGER.error("请求失败，Api返回码[103]。你需要进入http://w.qq.com，检查是否能正常接收消息。如果可以的话点击[设置]->[退出登录]后查看是否恢复正常");
			} else {
				throw new RuntimeException(String.format("请求失败，Api返回码[%d]", retCode));
			}
		}
		return data;
	}

	// 发送get请求
	private net.dongliu.requests.Response<String> get(ApiURL url, Object... params) {
		return session.get(url.buildUrl(params)).addHeader("User-Agent", ApiURL.USER_AGENT).addHeader("Referer", url.getReferer()).text();
	}

	// 发送post请求
	private net.dongliu.requests.Response<String> post(ApiURL url, NutMap r) {
		return session.post(url.getUrl()).addHeader("User-Agent", ApiURL.USER_AGENT).addHeader("Referer", url.getReferer()).addHeader("Origin", url.getOrigin())
				.addForm("r", Json.toJson(r)).text();
	}

	public List<Category> getFriendListWithCategory() {
		LOGGER.info("开始获取好友列表");

		NutMap r = new NutMap();
		r.put("vfwebqq", vfwebqq);
		r.put("hash", hash());

		Response response = post(ApiURL.GET_FRIEND_LIST, r);
		NutMap result = getJsonObjectResult(response);
		// 获得好友信息
		Map<Long, Friend> friendMap = parseFriendMap(result);
		// 获得分组
		List<NutMap> categories = result.getList("categories", NutMap.class);
		Map<Integer, Category> categoryMap = new HashMap<Integer, Category>();
		categoryMap.put(0, Category.defaultCategory());
		for (int i = 0; categories != null && i < categories.size(); i++) {
			Category category = Lang.map2Object(categories.get(i), Category.class);
			categoryMap.put(category.getIndex(), category);
		}
		List<NutMap> friends = result.getList("friends", NutMap.class);
		for (int i = 0; friends != null && i < friends.size(); i++) {
			NutMap item = friends.get(i);
			Friend friend = friendMap.get(item.getLong("uin"));
			categoryMap.get(item.getInt("categories")).addFriend(friend);
		}
		return new ArrayList<Category>(categoryMap.values());
	}

	private static Map<Long, Friend> parseFriendMap(NutMap result) {
		Map<Long, Friend> friendMap = new HashMap<Long, Friend>();
		List<NutMap> info = result.getList("info", NutMap.class);
		for (int i = 0; info != null && i < info.size(); i++) {
			NutMap item = info.get(i);
			Friend friend = new Friend();
			friend.setUserId(item.getLong("uin"));
			friend.setNickname(item.getString("nick"));
			friendMap.put(friend.getUserId(), friend);
		}
		List<NutMap> marknames = result.getList("marknames", NutMap.class);
		for (int i = 0; marknames != null && i < marknames.size(); i++) {
			NutMap item = marknames.get(i);
			friendMap.get(item.getLong("uin")).setMarkname(item.getString("markname"));
		}
		List<NutMap> vipinfo = result.getList("vipinfo", NutMap.class);
		for (int i = 0; vipinfo != null && i < vipinfo.size(); i++) {
			NutMap item = vipinfo.get(i);
			Friend friend = friendMap.get(item.getLong("u"));
			friend.setVip(item.getInt("is_vip") == 1);
			friend.setVipLevel(item.getInt("vip_level"));
		}
		return friendMap;
	}

	// hash加密方法
	private String hash() {
		return hash(uin, ptwebqq);
	}

	// hash加密方法
	private static String hash(long x, String K) {
		int[] N = new int[4];
		for (int T = 0; T < K.length(); T++) {
			N[T % 4] ^= K.charAt(T);
		}
		String[] U = { "EC", "OK" };
		long[] V = new long[4];
		V[0] = x >> 24 & 255 ^ U[0].charAt(0);
		V[1] = x >> 16 & 255 ^ U[0].charAt(1);
		V[2] = x >> 8 & 255 ^ U[1].charAt(0);
		V[3] = x & 255 ^ U[1].charAt(1);

		long[] U1 = new long[8];

		for (int T = 0; T < 8; T++) {
			U1[T] = T % 2 == 0 ? N[T >> 1] : V[T >> 1];
		}

		String[] N1 = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
		String V1 = "";
		for (long aU1 : U1) {
			V1 += N1[(int) ((aU1 >> 4) & 15)];
			V1 += N1[(int) (aU1 & 15)];
		}
		return V1;
	}

	/**
	 * 发送群消息
	 * 
	 * @param groupId
	 *            群id
	 * @param msg
	 *            消息内容
	 */
	public void sendMessageToGroup(long groupId, String msg) {
		LOGGER.info("开始发送群消息");

		NutMap r = new NutMap();
		r.put("group_uin", groupId);
		r.put("content", Json.toJson(Arrays.asList(msg, Arrays.asList("font", Font.DEFAULT_FONT)))); // 注意这里虽然格式是Json，但是实际是String
		r.put("face", 573);
		r.put("clientid", Client_ID);
		r.put("msg_id", MESSAGE_ID++);
		r.put("psessionid", psessionid);

		Response<String> response = post(ApiURL.SEND_MESSAGE_TO_GROUP, r);
		checkSendMsgResult(response);
	}

	private static void checkSendMsgResult(Response<String> response) {
		if (response.getStatusCode() != 200) {
			LOGGER.error(String.format("发送失败，Http返回码[%d]", response.getStatusCode()));
		}
		NutMap result = Lang.map(response.getBody());
		Integer errCode = result.getAs("errCode", Integer.class);
		if (errCode != null && errCode == 0) {
			LOGGER.info("发送成功!");
		}
		LOGGER.error(String.format("发送失败，Api返回码[%d]", result.getAs("retcode", Integer.class)));
	}

	/**
	 * 发送讨论组消息
	 * 
	 * @param discussId
	 *            讨论组id
	 * @param msg
	 *            消息内容
	 */
	public void sendMessageToDiscuss(long discussId, String msg) {
		LOGGER.info("开始发送讨论组消息");

		NutMap r = new NutMap();
		r.put("did", discussId);
		r.put("content", Json.toJson(Arrays.asList(msg, Arrays.asList("font", Font.DEFAULT_FONT)))); // 注意这里虽然格式是Json，但是实际是String
		r.put("face", 573);
		r.put("clientid", Client_ID);
		r.put("msg_id", MESSAGE_ID++);
		r.put("psessionid", psessionid);

		Response response = post(ApiURL.SEND_MESSAGE_TO_DISCUSS, r);
		checkSendMsgResult(response);
	}

	/**
	 * 发送消息
	 * 
	 * @param friendId
	 *            好友id
	 * @param msg
	 *            消息内容
	 */
	public void sendMessageToFriend(long friendId, String msg) {
		LOGGER.info("开始发送消息");

		NutMap r = new NutMap();
		r.put("to", friendId);
		r.put("content", Json.toJson(Arrays.asList(msg, Arrays.asList("font", Font.DEFAULT_FONT)))); // 注意这里虽然格式是Json，但是实际是String
		r.put("face", 573);
		r.put("clientid", Client_ID);
		r.put("msg_id", MESSAGE_ID++);
		r.put("psessionid", psessionid);

		Response response = post(ApiURL.SEND_MESSAGE_TO_FRIEND, r);
		checkSendMsgResult(response);
	}

	/**
	 * 获得讨论组列表
	 * 
	 * @return
	 */
	public List<Discuss> getDiscussList() {
		LOGGER.info("开始获取讨论组列表");
		Response response = get(ApiURL.GET_DISCUSS_LIST, psessionid, vfwebqq);
		return getJsonObjectResult(response).getList("dnamelist", Discuss.class);
	}

	/**
	 * 获取群列表
	 * 
	 * @return
	 */
	public List<Group> getGroupList() {
		LOGGER.info("开始获取群列表");

		NutMap r = new NutMap();
		r.put("vfwebqq", vfwebqq);
		r.put("hash", hash());

		Response response = post(ApiURL.GET_GROUP_LIST, r);
		NutMap result = getJsonObjectResult(response);
		return result.getList("gnamelist", Group.class);
	}

	/**
	 * 获得当前登录用户的详细信息
	 * 
	 * @return
	 */
	public UserInfo getAccountInfo() {
		LOGGER.info("开始获取登录用户信息");

		Response response = get(ApiURL.GET_ACCOUNT_INFO);

		return Lang.map2Object(getJsonObjectResult(response), UserInfo.class);
	}

	/**
	 * 获得好友的详细信息
	 * 
	 * @return
	 */
	public UserInfo getFriendInfo(long friendId) {
		LOGGER.info("开始获取好友信息");

		Response response = get(ApiURL.GET_FRIEND_INFO, friendId, vfwebqq, psessionid);

		return Lang.map2Object(getJsonObjectResult(response), UserInfo.class);
	}

	/**
	 * 获得最近会话列表
	 * 
	 * @return
	 */
	public List<Recent> getRecentList() {
		LOGGER.info("开始获取最近会话列表");

		NutMap r = new NutMap();
		r.put("vfwebqq", vfwebqq);
		r.put("clientid", Client_ID);
		r.put("psessionid", "");

		Response response = post(ApiURL.GET_RECENT_LIST, r);

		final List<Recent> recents = new ArrayList<Recent>();
		Lang.each(getJsonArrayResult(response), new Each<NutMap>() {

			@Override
			public void invoke(int index, NutMap ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				recents.add(Lang.map2Object(ele, Recent.class));
			}
		});
		return recents;
	}

	/**
	 * 获得qq号
	 * 
	 * @param friendId
	 *            用户id
	 * @return
	 */
	public long getQQById(long friendId) {
		LOGGER.info("开始获取QQ号");

		Response response = get(ApiURL.GET_QQ_BY_ID, friendId, vfwebqq);
		return getJsonObjectResult(response).getLong("account");
	}

	/**
	 * 获得登录状态
	 * 
	 * @return
	 */
	public List<FriendStatus> getFriendStatus() {
		LOGGER.info("开始获取好友状态");

		Response response = get(ApiURL.GET_FRIEND_STATUS, vfwebqq, psessionid);

		final List<FriendStatus> friendStatus = new ArrayList<FriendStatus>();
		Lang.each(getJsonArrayResult(response), new Each<NutMap>() {

			@Override
			public void invoke(int index, NutMap ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				friendStatus.add(Lang.map2Object(ele, FriendStatus.class));
			}
		});
		return friendStatus;
	}

	/**
	 * 获得群的详细信息
	 * 
	 * @param groupCode
	 *            群编号
	 * @return
	 */
	public GroupInfo getGroupInfo(long groupCode) {
		LOGGER.info("开始获取群资料");

		Response response = get(ApiURL.GET_GROUP_INFO, groupCode, vfwebqq);
		NutMap result = getJsonObjectResult(response);
		GroupInfo groupInfo = result.getAs("ginfo", GroupInfo.class);
		// 获得群成员信息
		Map<Long, GroupUser> groupUserMap = new HashMap<Long, GroupUser>();
		List<NutMap> minfo = result.getList("minfo", NutMap.class);
		for (int i = 0; minfo != null && i < minfo.size(); i++) {
			GroupUser groupUser = Lang.map2Object(minfo.get(i), GroupUser.class);
			groupUserMap.put(groupUser.getUin(), groupUser);
			groupInfo.addUser(groupUser);
		}
		List<NutMap> stats = result.getList("stats", NutMap.class);
		for (int i = 0; stats != null && i < stats.size(); i++) {
			NutMap item = stats.get(i);
			GroupUser groupUser = groupUserMap.get(item.getLong("uin"));
			groupUser.setClientType(item.getInt("client_type"));
			groupUser.setStatus(item.getInt("stat"));
		}
		List<NutMap> cards = result.getList("cards", NutMap.class);
		for (int i = 0; cards != null && i < cards.size(); i++) {
			NutMap item = cards.get(i);
			groupUserMap.get(item.getLong("muin")).setCard(item.getString("card"));
		}
		List<NutMap> vipinfo = result.getList("vipinfo", NutMap.class);
		for (int i = 0; vipinfo != null && i < vipinfo.size(); i++) {
			NutMap item = vipinfo.get(i);
			GroupUser groupUser = groupUserMap.get(item.getLong("u"));
			groupUser.setVip(item.getInt("is_vip") == 1);
			groupUser.setVipLevel(item.getInt("vip_level"));
		}
		return groupInfo;
	}

	/**
	 * 获得讨论组的详细信息
	 * 
	 * @param discussId
	 *            讨论组id
	 * @return
	 */
	public DiscussInfo getDiscussInfo(long discussId) {
		LOGGER.info("开始获取讨论组资料");

		Response response = get(ApiURL.GET_DISCUSS_INFO, discussId, vfwebqq, psessionid);
		NutMap result = getJsonObjectResult(response);
		DiscussInfo discussInfo = result.getAs("info", DiscussInfo.class);
		// 获得讨论组成员信息
		Map<Long, DiscussUser> discussUserMap = new HashMap<Long, DiscussUser>();
		List<NutMap> minfo = result.getList("mem_info", NutMap.class);
		for (int i = 0; minfo != null && i < minfo.size(); i++) {
			DiscussUser discussUser = Lang.map2Object(minfo.get(i), DiscussUser.class);
			discussUserMap.put(discussUser.getUin(), discussUser);
			discussInfo.addUser(discussUser);
		}
		List<NutMap> stats = result.getList("mem_status", NutMap.class);
		for (int i = 0; stats != null && i < stats.size(); i++) {
			NutMap item = stats.get(i);
			DiscussUser discussUser = discussUserMap.get(item.getLong("uin"));
			discussUser.setClientType(item.getInt("client_type"));
			discussUser.setStatus(item.getString("status"));
		}
		return discussInfo;
	}

	@Override
	public void close() throws IOException {
		this.pollStarted = false;
	}
}
