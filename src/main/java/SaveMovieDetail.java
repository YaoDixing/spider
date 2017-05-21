import bean.MovieDetail;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/5/21.
 */
public class SaveMovieDetail {

    private MongoDatabase database;
    private MongoCollection collection;
    private void initDB(){

    }

    List<Document > documentList ;
    long time ;
    public void getMovieBoxFromDb(String key){
        time = System.currentTimeMillis();
        database = MongoDBUtil.instance.getDB(Constants.DB_NAME);
        collection = database.getCollection(Constants.TABLE_MOVIE_BOX);
        BasicDBObject obj = new BasicDBObject();
        obj.put("keyWord",key);
        MongoCursor<Document> cursor =  MongoDBUtil.instance.find(collection,obj);
        documentList = new ArrayList<Document>();
        if(cursor!=null){
            while (cursor.hasNext()){
                Document doc = cursor.next();
               documentList.add(doc);

            }
        }
        for(Document doc:documentList){
            final String link = doc.getString("link_url");
            final String refer = doc.getString("referer");

            int random = new Random().nextInt(6);
            try {
                Thread.sleep(random*1000);
                connectUrl(link,refer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    synchronized void connectUrl(final String url,final String refer){
        MovieDetail movieDetail = new MovieDetail();
        try{
            Connection connection = Jsoup.connect(url);
            connection.header("Referer",refer);
            long t = System.currentTimeMillis();
            connection.header("Connection","keep-alive");
            connection.header("Host","avio.pw");
            connection.header("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:53.0) Gecko/20100101 Firefox/53.0");
            connection.header("Cookie", "AD_enterTime="+time+"; __test; __test; AD_clic_j_POPUNDER=2; AD_adst_j_POPUNDER=2; AD_exoc_j_POPUNDER=2; splash_i=false; splashWeb-1008094-42=1; AD_javu_j_P_728x90=2; AD_exoc_j_M_728x90=0; AD_juic_j_M_728x90=2; AD_wav_j_P_728x90=7; AD_enterTime="+time+"; AD_juic_j_L_728x90=1; _gat=1; AD_exoc_j_L_728x90=6; _ga=GA1.2.628838833."+(t-18230)+"; _gid=GA1.2.1614885301."+(t+1637)+"");
            connection.header("Upgrade-Insecure-Requests", "1");
            connection.validateTLSCertificates(false);
            connection.timeout(20000);
            org.jsoup.nodes.Document document = connection.get();
            /* 获取 movie title */
            Elements titleElements = document.select(".container h3");
            if(titleElements!=null && !titleElements.isEmpty()){
                String title = titleElements.get(0).text();
//                System.out.println("title:"+title);
                movieDetail.setMovieName(title);
            }
            /* 获取movie cover */
            Elements coverElements = document.select("a.bigImage");
            if(coverElements != null && !coverElements.isEmpty()){
                String coverUrl  = coverElements.get(0).attr("href");
                System.out.println("cover:"+coverUrl);
                movieDetail.setCoverUrl(coverUrl);
            }
            /* 获取影片信息 */
            Elements infoElements = document.select(".info p");

            if(infoElements!=null && !infoElements.isEmpty()) {
                Elements tags = infoElements.select("span.genre");
                List<String> tagList = new ArrayList<String>();
                for (Element tag : tags) {
                    String movieTag = tag.text();
                    tagList.add(movieTag);
                    System.out.println("tag:" + movieTag);
                }
                movieDetail.setTags(tagList);
                boolean nextIsManu = false;
                boolean nextIsPub = false;
                boolean isSeries = false;
                for (Element element : infoElements) {
                    String str = element.text();
                    if (nextIsManu && !nextIsPub && !isSeries) {
                        nextIsManu = false;
                        movieDetail.setManufacturer(str);
                        System.out.println("制作商:" + str);
                    } else if (!nextIsManu && nextIsPub && !isSeries) {
                        nextIsPub = false;
                        movieDetail.setDistributor(str);
                        System.out.println("发行商:" + str);
                    } else if (!nextIsManu && !nextIsPub && isSeries) {
                        isSeries = false;
                        movieDetail.setSeries(str);
                        System.out.println("系列:" + str);
                    } else {
                        if (str.contains("识别码")) {
                            movieDetail.setMovieCode(str.split(":")[1]);
                            System.out.println("识别码:" + movieDetail.getMovieCode());
                        } else if (str.contains("发行时间"))
                            movieDetail.setPublishTime(str.split(":")[1]);
                        else if (str.contains("长度"))
                            movieDetail.setMovieLength(str.split(":")[1]);
                        else if (str.contains("导演"))
                            movieDetail.setDirector(str.split(":")[1]);
                        else if (str.contains("制作商") && !nextIsManu) {
                            nextIsManu = true;
                        } else if (str.contains("发行商")) {
                            nextIsPub = true;
                        } else if (str.contains("系列")) {
                            isSeries = true;
                        }
                    }

//                    System.out.println(element.text());
                }
            }
                /* performer avatar and name */
            Elements avatarElements = document.select(".avatar-box img");
            if(avatarElements!=null && !avatarElements.isEmpty()){
                String avatar = avatarElements.get(0).attr("src");
                movieDetail.setPerformerAvatar(avatar);
            }
            Elements performerNameElements = document.select(".avatar-box span");
            if(performerNameElements!=null && !performerNameElements.isEmpty()){
                String performerName = performerNameElements.get(0).text();
                movieDetail.setPerformerName(performerName);
            }
                /* 样品图片 */
            Elements sampleImgElements = document.select(".sample-box");
            if(sampleImgElements!=null && !sampleImgElements.isEmpty()){
                List<String> imgList = new ArrayList<String>();
                for (Element sampleElement : sampleImgElements) {
                    String sampleImg = sampleElement.attr("href");
                    imgList.add(sampleImg);
                }
                movieDetail.setSampleImgs(imgList);
            }
            saveInfoToDB(movieDetail);
        }catch (Exception e){

            e.printStackTrace();
            if(e instanceof SocketTimeoutException) {
                try {
                    Thread.sleep(30000);
                    connectUrl(url, refer);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            }else if(e instanceof HttpStatusException){
                try {
                    Thread.sleep(30000);
                    connectUrl(url, refer);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    void saveInfoToDB(MovieDetail movieDetail){
        collection = database.getCollection(Constants.TABLE_MOVIE_DETAIL);
        if(collection == null){
            database.createCollection(Constants.TABLE_MOVIE_DETAIL);
        }
        Document document = new Document();
        document.put("movie_name",movieDetail.getMovieName());
        document.put("movie_cover",movieDetail.getCoverUrl());
        document.put("movie_code",movieDetail.getMovieCode());
        document.put("movie_publish_time",movieDetail.getPublishTime());
        document.put("movie_length",movieDetail.getMovieLength());
        document.put("movie_manufacturer",movieDetail.getManufacturer());
        document.put("movie_distributor",movieDetail.getDistributor());
        document.put("movie_director",movieDetail.getDirector());
        document.put("movie_series",movieDetail.getSeries());
        document.put("movie_performer_name",movieDetail.getPerformerName());
        document.put("movie_performer_avatar",movieDetail.getPerformerAvatar());
        document.put("movie_tags",movieDetail.getTags());
        document.put("movie_sample_img",movieDetail.getSampleImgs());
        collection.insertOne(document);
    }
}
