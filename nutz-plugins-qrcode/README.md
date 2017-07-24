### QRCode on Nutz

简介(可用性:生产,维护者:冬日温泉)
==================================

生成 QRCode,基于 [zxing](http://code.google.com/p/zxing/)

### How to ues

#### 生成 QRCode

```java
// 直接获得 QRCode 的图像对象
BufferedImage image = QRCode.toQRCode("this's simple text.");

// 或者通过构造器模式
QRCode qrcode = QRCode.NEW("this's simple text by used build method.");

// 获得 QRCode 的图像对象
BufferedImage image = qrcode.getQrcodeImage();

// 在指定路径的文件中生成一个 QRCode 的图片，图片格式为 png
// 如果指定的文件没有扩展名，将生成一个扩展名为 png 的图片
String filePath = "/PATH/TO/FILE.jpg";
qrcode.toFile(filePath);

// 或者直接指定一个 File 对象
File file = new File(filePath);
qrcode.toFile(file);
```

默认使用如下参数生成 QRCode，

* 图片大小: 256 x 256 像素
* 内容编码格式: UTF-8
* 错误修正等级: Level M (有15% 的内容可被修正)
* 前景色: 黑色
* 背景色: 白色
* 输出图片的文件格式: png
* 图片空白区域大小: 0个单位

如需修改生成参数，请使用`QRCodeFormat`类来实现，比如

```java
// 生成一个带有默认值的生成器格式
QRCodeFormat format = QRCodeFormat.NEW();

// 改变生成器格式的值
format.setSize(400) // 设置图片大小
    .setEncode("GB18030") // 设置文字编码
    .setErrorCorrectionLevel('H') // 设置错误修正等级
    .setForeGroundColor("#2F4F4F") // 设置前景色
    .setBackGroundColor("#808080") // 设置背景色
    .setImageFormat("jpg") // 设置生成的图片格式
    .setMargin(0) // 设置图片空白区域, 单位 - 格（外填充）
    .setIcon(new File("/PATH/TO/ICON_FILE")); // 设置 icon

// 然后
// 使用指定的生成器格式生成一个 QRCode 的图像对象
BufferedImage image = QRCode.toQRCode("this's simple text by used format.", format);

// 或者
// 使用指定的生成器格式生成一个 QRCode 构造器
QRCode qrcode = QRCode.NEW("this's simple test text by used format.", format);
```

另外能给生成的 QRCode 的中添加上指定的图片

```java
QRCode qrcode = QRCode.NEW("this's simple test text. and add icon file.");

// 在指定路径的文件中生成 QRCode 的图片，以及需要添加的 icon 的图片路径。（添加的图片不支持 ico 格式）
// 指定的 icon 的图片不存在时将不会在 QRCode 中添加图片
String filePath = "/PATH/TO/FILE";
String iconFilePath = "/PATH/TO/ICON_FILE";
qrcode.toFile(filePath, iconFilePath);

// 或者直接使用 File 对象
File file = new File(filePath);
File iconFile = new File(iconFilePath);
qrcode.toFile(file, iconFile);

// 或者使用生成器格式来设置
QRCodeFormat format = QRCodeFormat.NEW().setIcon(new File(iconFilePath));
QRCode qrcode = QRCode.NEW("this's simple test text. and add icon file form format.", format);
File file = new File(filePath);
qrcode.toFile(file, iconFile);
```

如果直接指定了添加的 icon 文件的时候，将忽略生成器格式中的 icon 文件。

为了保证生成的 QRCode 的图片能被正确识别，请按需设置 QRCode 的图片大小，以及 icon 图片的大小。

#### 解析 QRCode

```java
// 图片路径
String filePath = "/PATH/TO/FILE";
String content = QRCode.from(filePath);

// 或者直接传入文件对象
File file = new File(filePath);
content = QRCode.from(file);

// 或者 QRCode 图像对象
BufferedImage image = ImageIO.read(file);
content = QRCode.from(image);

// 支持直接解析 QRCode 图片的 URL 地址（以「http」或者「https」开头）
String url = "https://chart.googleapis.com/chart?chs=72x72&cht=qr&choe=UTF-8&chl=http%3A%2F%2Fwww.nutz.cn%2F";
content = QRCode.from(url);

// 或者该地址的对象
URL imageUrl = new URL(url);
content = QRCode.from(imageUrl);
```

### 其他

请使用`mvn package -Dmaven.test.skip=true`生成 jar 包使用。

如果想把代码当成一个工程导入到 eclipse 中，请运行`mvn eclipse:eclipse`。

QRCode 的详细介绍请参照 [维基百科](http://zh.wikipedia.org/zh-cn/QR%E7%A0%81)。