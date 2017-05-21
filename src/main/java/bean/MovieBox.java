package bean;

/**
 * Created by Administrator on 2017/5/20.
 */
public class MovieBox {
    String linkUrl;
    String thumb ;
    String movieName;
    String code;
    String date;
    String keyWord;
    String currentPageUrl;

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCurrentPageUrl() {
        return currentPageUrl;
    }

    public void setCurrentPageUrl(String currentPageUrl) {
        this.currentPageUrl = currentPageUrl;
    }
}
