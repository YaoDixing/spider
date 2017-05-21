/**
 * Created by Administrator on 2017/5/20.
 */
public class Run {
    public static void main(String[] args){
//        Search search = new Search();
//        search.searchAvByKeyWord("lxvs");

        SaveMovieDetail saveMovieDetail = new SaveMovieDetail();
        saveMovieDetail.getMovieBoxFromDb("lxvs");
    }
}
