package comkimwonjun.httpsgithub.indianpoker.Retrofit;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by KimWonJun on 10/14/2017.
 */

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static HttpInterface communication = null;

    public static HttpInterface getInterface() {
        if (retrofit == null) {
            Log.d("CHECK", "클라이언트 만들어짐");
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            communication = retrofit.create(HttpInterface.class);
        }

        return communication;
    }
}