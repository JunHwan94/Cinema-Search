package com.polarbearr.cinemasearch;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsService;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final String X_NAVER_ID = "X-Naver-Client-Id";
    static final String X_NAVER_SECRET= "X-Naver-Client-Secret";
    static final String CLIENT_ID = "aCCmpxUDpSTGU1paDToI";
    static final String CLIENT_SECRET = "JeXmd4tO4T";

    RecyclerView recyclerView;
    MovieInfoAdapter adapter;
    EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity activity = this;

        searchText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieInfoAdapter(getApplicationContext());
        adapter.setOnItemClickListener(new MovieInfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieInfoAdapter.ViewHolder holder, View view, int position) {
                MovieInfo item = adapter.getItem(position);
                String url = item.getLink();
                System.out.println(url);
                startWebView(url);
            }
        });
        recyclerView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchWord = searchText.getText().toString();

                if(searchWord.equals(""))
                    GreenToast.setCustomToast(getApplicationContext(), R.string.no_searchWord, null);
                else requestSearchWord(searchWord);
                hideKeyboard(activity);
            }
        });
    }

    // 데이터 요청
    public void requestSearchWord(String searchWord){
        final Map<String, String> headers = new HashMap<>();
        headers.put(X_NAVER_ID, CLIENT_ID);
        headers.put(X_NAVER_SECRET, CLIENT_SECRET);

        String targetUrl = "https://openapi.naver.com/v1/search/movie.json?query=" + searchWord + "&display=100";
//        if(sno != 0) targetUrl += "&start=" + sno;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                targetUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "응답 실패", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                return headers;
            }
        };

        request.setShouldCache(false);
        HttpHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    // 응답 처리
    public void processResponse(String response){
//        System.out.println(response);
        // 응답받은 json을 gson으로 파싱
        Gson gson = new Gson();
        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);

        adapter.items.clear();

        // 응답에 영화 정보가 하나라도 있으면 파싱, 어댑터에 추가
        if(0 < info.total){
            System.out.println(info.total);

            MovieInfoList movieList = gson.fromJson(response, MovieInfoList.class);
            List<MovieInfo> items = movieList.items;
            adapter.addItems(items);
        } else{
            adapter.items.clear();
            GreenToast.setCustomToast(getApplicationContext(), R.string.no_result, searchText.getText().toString());
        }
        adapter.notifyDataSetChanged();
    }

    // 웹뷰 실행
    public void startWebView(String url){
        Bitmap arrowIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_back);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getColor(R.color.colorPrimary));
        builder.setCloseButtonIcon(arrowIcon);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    // 키보드 숨기기
    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
