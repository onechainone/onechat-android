
# onechat-sdk-android

## WalletCore  

### 结构   

- 账号管理
- 聊天管理
- 群组管理
- 联系人管理
- 红包管理
- 社区管理   

## 初始化SDK   

### 初始化环境

APP启动需要调用`initOneChat`来初始化SDK环境，
`appId`为您的app唯一id标识。

```objc
OneAccountHelper.initOneChat(Context context, String appId);
OneChatUiHelper.initOneChatUi(Context context);
```

### 判断本地是否存在账号

如果本地没有账号则需要去注册或者恢复,建议在进行注册或者恢复之前先进行节点检测以选择性能最优的节点。如果本地有账号信息则验证密码即可。

```
boolean isHasAccount = OneAccountHelper.isHasAccount();
```   

## 注册恢复   

### 助记词   

助记词由15个单词组成   

- 生成助记词   

	```
	String seed = OneAccountHelper.generateNewMnemonic();
	```
- 获取本地存储的助记词   

	```
	String seed = OneAccountHelper.getBrainKey();
	```
	
- 验证助记词是否合法
	```
	boolean isHasAccount = OneAccountHelper.checkPassword(seed)
	```
	
- 获取加密助记词   

	加密助记词是通过助记词和用户密码通过一系列加密生成的加密字符串

	```
	String encryptSeed = Util.bytesToHex(Util.encryptAES(BtsHelper.getDefaultAccount().brain_key.getBytes(Charsets.UTF_8), BtsHelper.getMePasswordBackend().getBytes(Charsets.UTF_8)));
	```   

## 发送消息相关
### MessageSender
- 发送单聊消息
	```
	public static MessageSender getSingleMessageSender(Activity mActivity, final String userId)
	```
- 发送群聊消息
	```
	public static MessageSender getGroupMessageSender(Activity mActivity, final String groupId)
	```
## ONE账号相关
### OneAccountHelper

## ONE聊天相关
### OneChatHelper

## ONE社区相关
### OneCommunityHelper

## ONE群聊相关
### OneGroupHelper

## ONE其他相关
### OneOpenHelper

## ONE红包相关
### OneRedpacketHelper


