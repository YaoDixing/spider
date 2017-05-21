package bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/21.
 */
public class MovieDetail {
    private String movieName;
    private String coverUrl;
    private String movieCode;
    private String publishTime;
    private String movieLength;
    private String director;
    private String manufacturer;
    private String distributor;
    private String series;
    private List<String> tags;
    private String performerName;
    private String performerAvatar;
    private List<String> sampleImgs;

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getMovieCode() {
        return movieCode;
    }

    public void setMovieCode(String movieCode) {
        this.movieCode = movieCode;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getMovieLength() {
        return movieLength;
    }

    public void setMovieLength(String movieLength) {
        this.movieLength = movieLength;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getPerformerName() {
        return performerName;
    }

    public void setPerformerName(String performerName) {
        this.performerName = performerName;
    }

    public String getPerformerAvatar() {
        return performerAvatar;
    }

    public void setPerformerAvatar(String performerAvatar) {
        this.performerAvatar = performerAvatar;
    }

    public List<String> getSampleImgs() {
        return sampleImgs;
    }

    public void setSampleImgs(List<String> sampleImgs) {
        this.sampleImgs = sampleImgs;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }
}
