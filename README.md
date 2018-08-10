<p align="center">
    <h3 align="center">DouYuAndroid-SDK</h3>
    <p align="center">
        DouYuAndroid-SDK,一个基于斗鱼弹幕API封装的SDK
        <br>
        (斗鱼弹幕服务器第三方接入协议v1.4.1)
        <br>
        <a href="https://jitpack.io/#xbw12138/Douyu_Android_lib">
            <img src="https://jitpack.io/v/xbw12138/Douyu_Android_lib.svg" >
        </a>
         <a href="http://www.gnu.org/licenses/gpl-3.0.html">
             <img src="https://img.shields.io/badge/license-GPLv3-blue.svg" >
         </a>
    </p>    
</p>

## Introductions

DouYuAndroid-SDK,一个基于斗鱼弹幕API封装的SDK

本SDK基于 斗鱼弹幕服务器第三方接入协议v1.4.1 进行封装


## How to
### Step 1. Add the JitPack repository to your build file
####  Add it in your root build.gradle at the end of repositories:
```
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### Step 2. Add the dependency

```
dependencies {
	        implementation 'com.github.xbw12138:Douyu_Android_lib:1.2'
	}
```
## Usage

In Activity

```
   private DouYuClient client;
   
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable(){
            @Override
            public void run() {
                getDouyuDanmu("750240");//斗鱼房间号
            }
        }).start();
    }
    public void getDouyuDanmu(String roomid) {
        client = new DouYuClient(DouYuConstants.SOCKET_HOST, DouYuConstants.SOCKET_PORT, roomid);
        client.registerMessageListener(new MessageListener<ChatMsg>() {
            @Override
            public void read(ChatMsg message) {
                Log.d("xbw12138", message.toChatStr());
            }
        });
        client.login();
        client.sync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable(){
            @Override
            public void run() {
                client.exit();
            }
        }).start();
    }
```

## 目前支持监听的消息类型
```
//斗鱼推送的原消息
String

//通用消息实体(用于处理所有接收到的消息)
BaseMsg

//错误消息/系统消息
ErrorMsg

//弹幕消息
ChatMsg

//赠送礼物消息
DgbMsg

//房间内用户抢红包消息
GgbbMsg

//礼物广播消息
SpbcMsg

//超级弹幕消息
SsdMsg

//用户进房通知消息
UenterMsg
```

## 注意事项

1.当接收到消息后会线生成一个BaseMsg实体再根据消息类型转换为其他各类消息实体

2.每一个实体中都会有UUID字段，该字段用于关联BaseMsg记录，是SDK自己生成的，非斗鱼API返回，如需要实现用id区分消息唯一暂无方案


## 友情链接
[https://github.com/yyc-dev/douyu-sdk](https://github.com/yyc-dev/douyu-sdk)

