import bean.MovieBox;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/5/20.
 */
public class Search {

    String key;
    long time;
    MongoCollection mongoCollection;
    public void searchAvByKeyWord(String keyWord){
        key = keyWord;
        time = System.currentTimeMillis();
        String url = Constants.SEARCH_HOST +Constants.SEARCH_API + keyWord+"";
        MongoDatabase database =  MongoDBUtil.instance.getDB(Constants.DB_NAME);
        mongoCollection =  database.getCollection(Constants.TABLE_MOVIE_BOX);
        if(mongoCollection==null)
            database.createCollection("");
        /* 保存每一页的内容 */
        saveEveryPage(url,"");
    }

    void saveEveryPage(final String url, final String refer){
        System.out.println(System.currentTimeMillis()+"");

        final Connection connection = Jsoup.connect(url);

        if(refer.equals("")){

        }else {
            long t = System.currentTimeMillis();
            connection.header("Cookie", "AD_enterTime="+time+"; __test; __test; AD_clic_j_POPUNDER=2; AD_adst_j_POPUNDER=2; AD_exoc_j_POPUNDER=2; splash_i=false; splashWeb-1008094-42=1; AD_javu_j_P_728x90=2; AD_exoc_j_M_728x90=0; AD_juic_j_M_728x90=2; AD_wav_j_P_728x90=7; AD_enterTime="+time+"; AD_juic_j_L_728x90=1; _gat=1; AD_exoc_j_L_728x90=6; _ga=GA1.2.628838833."+(t-18230)+"; _gid=GA1.2.1614885301."+(t+1637)+"");
        }
//        connection.header("Connection","keep-alive");
        connection.header("Upgrade-Insecure-Requests", "1");
        connection.validateTLSCertificates(false);
        connection.timeout(20000);
        try {
            Document document = null;
            document = connection.get();

            /* 保存第一页数据 */
            /* 获取每个movie box */
            Elements movieBoxs = document.select("a.movie-box");

            System.out.println("当前页面url:"+url);
            System.out.println("当前页面共有movie:"+movieBoxs.size()+"个");

            for (Element movieBox : movieBoxs){
                MovieBox box = new MovieBox();
                box.setCurrentPageUrl(url);
                int index = movieBoxs.indexOf(movieBox)+1;
                String linkUrl = movieBox.attr("href");
                System.out.println("这是第"+index+"个movie");
                System.out.println("link url："+linkUrl);
                box.setLinkUrl(linkUrl);
                /* 获取图片url  和 名字 */
                Elements imgElements = movieBox.getElementsByTag("img");
                if(imgElements!=null && imgElements.size()>0) {
                    String imgurl = imgElements.get(0).attr("src");
                    String title = imgElements.get(0).attr("title");
                    box.setThumb(imgurl);
                    box.setMovieName(title);
                    System.out.println("picUrl:"+imgurl);
                    System.out.println("movie name:"+title);
                }
                /* 获取编号 与 发行日期 */
                Elements codeDates = movieBox.getElementsByTag("date");
                if(codeDates!=null && codeDates.size()>1){
                    String code = codeDates.get(0).text();
                    String date = codeDates.get(1).text();
                    box.setCode(code);
                    box.setDate(date);
                    System.out.println("code:"+code);
                    System.out.println("date:"+date);
                }
                insertMovieBox(box);
            }
            /* 获取下一页 url */
            Elements elements = document.select("a[name = nextpage]");
            if(elements!=null &&!elements.isEmpty()) {
                for (Element element : elements) {
                    String nextPageUrl = element.attr("href");
                    String ss[] = nextPageUrl.split("/");
                    final int page = (Integer.valueOf(ss[ss.length - 1]));
                    final String nextPage = Constants.SEARCH_HOST + Constants.SEARCH_API+key+"/page/"+page;
                    System.out.println("next page:" + nextPage);
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                int random =  new Random().nextInt(11);
                                random = random*1000+1000;
                                System.out.println("random :"+random);
                                Thread.sleep(random);
                                a++;
                                String refer ;
                                if(a==1){
                                    refer = Constants.SEARCH_HOST+Constants.SEARCH_API+key;
                                }else {
                                    refer =Constants.SEARCH_HOST+Constants.SEARCH_API+key+"/page/"+(page-1);
                                }

                                saveEveryPage(nextPage,refer);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();

                }
            }else {
                System.out.println("保存完毕！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof SocketTimeoutException){

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(30000);
                            System.out.println("reload");
                            String ss[] = url.split("/");
                            try {
                                final int page = (Integer.valueOf(ss[ss.length - 1]))-1;
                                final String previousPage = Constants.SEARCH_HOST + Constants.SEARCH_API+key+"/page/"+page;
                                saveEveryPage(url,previousPage);
                            }catch (NumberFormatException e1){
                                saveEveryPage(url,"");
                            }
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }).start();



            }
        }
    }

     List<String> brokenUrlList = new ArrayList<String>();

    int a = 0;
    String pre;

    public void insertMovieBox(MovieBox box){
        org.bson.Document doc = new org.bson.Document();
        doc.put("link_url",box.getLinkUrl());
        doc.put("keyWord",key);
        doc.put("thumb",box.getThumb());
        doc.put("movie_name",box.getMovieName());
        doc.put("code",box.getCode());
        doc.put("date",box.getDate());
        doc.put("referer",box.getCurrentPageUrl());
        mongoCollection.insertOne(doc);
    }
}
