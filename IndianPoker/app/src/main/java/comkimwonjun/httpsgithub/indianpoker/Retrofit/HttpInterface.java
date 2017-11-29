package comkimwonjun.httpsgithub.indianpoker.Retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by KimWonJun on 11/10/2017.
 */

public interface HttpInterface {
    @POST("/api/auth/login")
    @FormUrlEncoded
    Call<ResponseBody> reqLogin(@Field("username") String id, @Field("password") String pw);


    @POST("/api/auth/register")
    @FormUrlEncoded
    Call<ResponseBody> register(@Field("username") String id, @Field("password") String pw);
}
