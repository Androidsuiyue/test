package com.test;

import com.utils.HttpReqestUtils;
import com.utils.HzptUtils;
import com.utils.MovieUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;



/**
 * Created by hdy on 17-8-21.
 */
public class Test {

    @org.junit.Test
    public void test() {
        try {
            String back = HzptUtils.send("http://10.50.50.2:801/eportal/controller/Action.php", "utf-8");
            //back里包含的就是成功还是错误的信息
            //成功返回{"ret": "true"}
            //失败返回{"result":"fail","msg":"\u83b7\u53d6\u9a8c\u8bc1\u7801\u5931\u8d25\uff0c\u8bf7\u91cd\u65b0\u83b7\u53d6\uff01"}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
@org.junit.Test
public void testRequest(){
   String result = HttpReqestUtils.sendPost("http://10.50.50.2/a70.htm", "DDDDD=13506580214" +
            "upass=570528&" +
            "R1=0&" +
            "R2=&" +
            "R6=0&"+"para:00&"+
           "MKKey:123456");
    System.out.println(result);

}



    /**
     * 单个分类查询
     */
    @org.junit.Test
    public void testOne() {
        Object[] detail = MovieUtils.getTypeDetail("旧版综艺", "http://dytt8.net/html/2009zongyi/index.html");
        com.model.MovieSearch o = (com.model.MovieSearch) detail[2];
        System.out.println(o);
        com.model.MovieSearch movieSearch = o.nextPage();
        System.out.println(movieSearch);
    }



    /**
     * 获取分类测试
     */
    @org.junit.Test
    public void test5() {
        Map<String, String> map = com.utils.ParseUtils.getType();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            System.out.println(next.getKey() + " " + next.getValue());
        }
    }

    /**
     * 所有分类查询测试
     * 尽量不要使用遍历....
     * 它服务器不行.....
     */
    @org.junit.Test
    public void testTyoe() {
        Map<String, String> map = com.utils.ParseUtils.getType();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            System.out.println("当前查询:" + next.getKey() + " " + next.getValue());
            Object[] detail = com.utils.MovieUtils.getTypeDetail(next.getKey(), next.getValue());
            if (detail == null) {
                System.out.println("当前分类不能查询");
            } else {
                if (detail[0] != null) {
                    System.out.println(detail[0]);
                } else if (detail[1] != null) {
                    System.out.println(detail[1]);
                }
                System.out.println(detail[2]);
            }
        }
    }

    /**
     * 详细数据获取
     */
    @org.junit.Test
    public void testDetail() {
        com.model.MovieDetail detail = com.utils.MovieUtils.detail("/html/gndy/dyzz/20170802/54643.html");
        System.out.println(detail);

    }


    /**
     * 主页数据获取
     */
    @org.junit.Test
    public void test1() {
        com.model.Movie info = com.utils.MovieUtils.getIndexTypeAndInfo();
        System.out.println(info);
        Map<com.model.MovieType, ArrayList<com.model.MovieTop>> map = info.getMap();
        Iterator<Map.Entry<com.model.MovieType, ArrayList<com.model.MovieTop>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<com.model.MovieType, ArrayList<com.model.MovieTop>> next = iterator.next();
            System.out.println("[" + next.getKey().getTitle() + "]");
            ArrayList<com.model.MovieTop> list = next.getValue();
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i).getTitle());
            }
        }
    }

    /**
     * 最新发布170部影片
     */
    @org.junit.Test
    public void test2() {
        int i = 2;
        if (i == 0) {
            com.model.Movie movie = com.utils.MovieUtils.getIndexTypeAndInfo();
            Iterator<Map.Entry<com.model.MovieType, ArrayList<com.model.MovieTop>>> iterator = movie.getMap().entrySet().iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next().getKey().getTitle());
            }
        } else if (i == 1) {
            com.model.Movie movie = com.utils.MovieUtils.getIndexOthers();
            Iterator<Map.Entry<com.model.MovieType, ArrayList<com.model.MovieTop>>> iterator = movie.getMap().entrySet().iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next().getKey().getTitle());
            }
        } else {
            com.model.Movie movie = com.utils.MovieUtils.getIndexLasted();
            Iterator<Map.Entry<com.model.MovieType, ArrayList<com.model.MovieTop>>> iterator = movie.getMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<com.model.MovieType, ArrayList<com.model.MovieTop>> next = iterator.next();
                System.out.println(next.getKey().getTitle());
                ArrayList<com.model.MovieTop> list = next.getValue();
                for (int j = 0; j < list.size(); j++) {
                    System.out.println(list.get(j).getTitle());
                }
            }
        }
    }


    /**
     * 主页大分类获取
     */
    @org.junit.Test
    public void jsoupTest() {
        String content = com.utils.NetworkUtils.get("http://www.dytt8.net/");
        Document document = Jsoup.parse(content);
        Elements elements = document.select("table > tbody > tr");
        System.out.println(elements.size());
        //临时存放视频类型
        LinkedList<com.model.MovieType> movieTypes = new LinkedList<com.model.MovieType>();
        ArrayList<com.model.MovieTop> movieTops = new ArrayList<com.model.MovieTop>();
        com.model.Movie movie = new com.model.Movie();
        int times = 0;
        for (Element link : elements) {
            times++;
            Elements select = link.select("td.inddline");
            if (select.size() == 2) {
                //电影相关信息
                Elements a = select.get(0).getElementsByTag("a");
                //类型指向地址
                String type_href = a.get(0).attr("href");
                //类型内容
                String type_content = a.get(0).text();
                //资源指向地址
                String movie_href = a.get(1).attr("href");
                //资源标题
                String movie_title = a.get(1).text();
                //时间
                String time = select.get(1).getElementsByTag("font").get(0).text();
                if (movieTypes.size() == 0) {
                    com.model.MovieType movieType = new com.model.MovieType(type_href, type_content);
                    movieTypes.add(movieType);
                } else {
                    com.model.MovieType last = movieTypes.getLast();
                    if (type_content.equals(last.getTitle())) {
                        //说明是一样的
                        movieTops.add(new com.model.MovieTop(movie_href, movie_title, time));
                        if (times == elements.size() - 1) {
                            //如果一样,说明是最后了
                            movie.getMap().put(movieTypes.getLast(), movieTops);
                        }
                    } else {
                        //说明是不一样的
                        movie.getMap().put(movieTypes.getLast(), movieTops);
                        movieTops = new ArrayList<com.model.MovieTop>();
                        com.model.MovieType movieType = new com.model.MovieType(type_href, type_content);
                        movieTypes.add(movieType);
                    }
                }
                System.out.println(movieTypes);
            }
        }
    }
}
