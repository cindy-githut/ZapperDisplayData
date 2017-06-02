package habbitatvalley.com.zapperdisplaydata;

/**
 * Created by cindymbonani on 2017/05/26.
 */

public class Networks {

    public static final String personBaseEndpoint = "http://demo4012764.mockable.io/person";

    public static final String getUserDetailedInfor(int userId){
        return  personBaseEndpoint+"/"+userId;
    }

}
