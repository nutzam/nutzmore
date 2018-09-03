nutz-plugins-xmlbind
==================================

简介(可用性:试用,维护者:wendal)
==================================

POJO与XML互转

## 需要用到的注解

```
@XmlEle // 标注在类和java属性上,代表一个XML的Node
@XmlAttr // 标注在java属性上,代表XML Node的attr
```

## 映射示例

XML文档

```
<?xml version="1.0" encoding="UTF-8"?>
<gpx>
  <trk>
    <name>coolrunner.xihu2018.100KM:xiaobai</name>
    <trkseg>
      <trkpt lat="47.644548" lon="-122.326897">
        <ele>4.46</ele>
        <time>2009-10-17T18:37:26Z</time>
      </trkpt>
      <trkpt lat="47.644548" lon="-122.326897">
        <ele>4.94</ele>
        <time>2009-10-17T18:37:31Z</time>
      </trkpt>
    </trkseg>
  </trk>
</gpx>
```

对应的POJO类,均省略了getter/setter

```
// 顶层的gpx
@XmlEle("gax")
public class GpxFile {
    @XmlEle
    public GpxTrk trk; // trk节点
}

// 第二层的trk
@XmlEle("trk")
public class GpxTrk {

    // 对应 <name>coolrunner.xihu2018.100KM:xiaobai</name>,因为它只有一层,内容肯定是文本
    // 所以,单独为其建个类非常浪费,xmlbind支持以"simpleNode"的形式,直接映射为一个java属性
    @XmlEle(simpleNode=true)
    public String name;
    @XmlEle
    public GpxTrkseg trkseg;
}

// 第三层的trkseg
@XmlEle("trkseg") // 类上的@XmlEle不是必须的,取决于你打算从哪一层开始解析.
public class GpxTrkseg {

    @XmlEle("trkpt") // java属性名与xml节点名不一致时,需要声明名字
    public List<GpxTrkpt> trkpts;
}

// 第四层的trkpt
@XmlEle("trkpt")
public class GpxTrkpt {

    // 这一层总算用到@XmlAttr
    @XmlAttr
    public String lat;
    @XmlAttr
    public String lon;
    @XmlEle(simpleNode = true)
    public String ele;  // 对应<ele>4.46</ele>
    @XmlEle(simpleNode = true)
    public String time; // 对应 <time>2009-10-17T18:37:31Z</time>
}
```

## 调用示例

### XML转POJO

简便写法
```
GpxFile gpx = XmlBind.fromXml(gpx, getClass().getClassLoader().getResourceAsStream("gpxfiles/trk.gpx"));
```

完整写法
```
// 首先,将需要映射的类变成抽象的XmlEntity
XmlEntity<GpxFile> en = new XmlEntityAnnotationMaker().makeEntity(null, GpxFile.class);
// 通过Xmls帮助类,从输入流/文本对象/文件,读取XML实体
Element top = Xmls.xml(getClass().getClassLoader().getResourceAsStream("gpxfiles/trk.gpx")).getDocumentElement();
// 调用XmlEntity对象的read方法,转换为POJO对象
GpxFile gpx = en.read(top);
// 打印一下,看看效果吧
System.out.println(Json.toJson(gpx));
```

### POJO转XML

```
// 然后,将GpxFile对象传入write方法,生成XML即可
String xml = XmlBind.toXml(gpx, "gpx");
```
