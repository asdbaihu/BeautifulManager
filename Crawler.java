import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.*;

import java.sql.*;
import java.util.*;

public class Crawler implements PageProcessor {

    public Crawler() {
    }

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site
            .me()
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setTimeOut(10000)
            .setUserAgent("AppleWebKit/537.31 (KHTML, like Gecko)")
            .addCookie("www.tttt8.club",
                    "wordpress_5759e5e9b5b452268b627c04afdc3908",
                    "tangchaolizi234%7C1515223725%7ClQ2JqAgPdHZsUSVmsc3mgZKT0QQMvv6OLuTafxOxDlY%7C1107de19b2a971f567c97aba5ccd3c2314bbaac6dbdeaf61f974c698d11fffb4")
            .addCookie("www.tttt8.club",
                    "wordpress_logged_in_5759e5e9b5b452268b627c04afdc3908",
                    "tangchaolizi234%7C1515223725%7ClQ2JqAgPdHZsUSVmsc3mgZKT0QQMvv6OLuTafxOxDlY%7C0ccd1634bf375b074a05ef895320a52f9714a4cbd74b442f5b3b33648a905730");

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        List<String> imgSrc = page.getHtml()
                .xpath("//div[@class=\"context\"]//div[@id=\"post_content\"")
                .regex("img src=\"(.+?)\"")
                .all();

        //System.out.println(imgSrc);

        int n = imgSrc.size();
        try {
            for (int i = 0; i < n; i++) {

                String url = imgSrc.get(i);
                String fileName = url.substring(url.lastIndexOf('/')).substring(1);
                Runnable runnable = new DownloadImage(url, fileName, "D:\\imgSave\\" + title);
                Thread t = new Thread(runnable);
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> nextPages = page.getHtml()
                .xpath("//div[@class=\"pagelist\"]")
                .links()
                .all();
        //System.out.println(nextPages);
        page.addTargetRequests(nextPages);
    }

    private String title = "";

    public Crawler setTitle(String title) {

        this.title = title;
        return this;

    }

    @Override
    public Site getSite() {
        return this.site;
    }

}
