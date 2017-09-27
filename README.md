# quick

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fcibook.quick/quick-http/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fcibook.quick/quick-http)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

this is a java quick request http util
 * Ability to quickly create HTTP requests
 * It's very simple, but very powerful
 --------------------
To use Quick - Http in a library
 ```java
 String res = new QuickHttp()
                .url("http://www.csdn.net/")
                .get()
                .addParame("c", "iphone")
                .addHeader("uid","100")
                .addCookie("sid","100")
                .text();
 ```
If use post and more return type
 ```java
byte[] res = new QuickHttp()
                .url("http://www.csdn.net/")
                .post()
                .bytes();
```
or return http code and cookie and data of ResponseBody
 ```java
ResponseBody body = new QuickHttp()
                .url("http://www.csdn.net/")
                .post()
                .body();
int stateCode = body.getStateCode();
CookieStore cookie = body.getCookie();
String text = body.text();
String bytes = body.bytes();
```
## Download
- [maven][1]
- [the latest JAR][2]  

[1]: https://repo1.maven.org/maven2/com/fcibook/quick/quick-http/
[2]: https://search.maven.org/remotecontent?filepath=com/fcibook/quick/quick-http/1.3/quick-http-1.3.jar

## Maven

```xml
<dependency>
    <groupId>com.fcibook.quick</groupId>
    <artifactId>quick-http</artifactId>
    <version>1.3</version>
</dependency>
```
