import com.fcibook.quick.http.OnHttpErrorListener;
import com.fcibook.quick.http.QuickHttp;
import com.fcibook.quick.http.ResponseBody;

/**
 * Created by cc_want on 2017/9/12.
 */
public class Test {


    public static void main(String args[]){

       // String url = "http://blog.csdn.net/xiaoliuliu2050/article/category/1504435";
        String url = "http://www.baidu.com";
        ResponseBody res = new QuickHttp()
                .url(url)
                .get()
                .debug()
                .setOnHttpErrorListener(new OnHttpErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        System.out.println("异常");
                        t.printStackTrace();
                    }
                })
                .body();

//        System.out.println(res.getStateCode());
//        System.out.println(res.getCookie());
//        System.out.println(res.text());


//        QuickURL quickURL = new QuickURL("http://doc.zsmy.cn/pages/viewpage.action?pageId=1803896");
//        System.out.println(quickURL.fullUrl());
//
//        QuickURL quickURL2 = new QuickURL(quickURL.getUrl(),quickURL.getParames());
//        System.out.println(quickURL2.fullUrl());

    }
}
