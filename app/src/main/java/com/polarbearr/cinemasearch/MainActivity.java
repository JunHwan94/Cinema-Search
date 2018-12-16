package com.polarbearr.cinemasearch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.polarbearr.cinemasearch.data.MovieInfo;
import com.polarbearr.cinemasearch.data.MovieInfoList;
import com.polarbearr.cinemasearch.data.ResponseInfo;
import com.polarbearr.cinemasearch.databinding.ActivityMainBinding;
import com.polarbearr.cinemasearch.network.HttpHelper;
import com.polarbearr.cinemasearch.utils.GreenToast;
import com.polarbearr.cinemasearch.utils.NetworkStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.polarbearr.cinemasearch.utils.NetworkStatus.TYPE_CONNECTED;

public class MainActivity extends AppCompatActivity {

    static final String X_NAVER_ID = "X-Naver-Client-Id";
    static final String X_NAVER_SECRET= "X-Naver-Client-Secret";
    static final String CLIENT_ID = "aCCmpxUDpSTGU1paDToI";
    static final String CLIENT_SECRET = "JeXmd4tO4T";
    ActivityMainBinding binding;

    MovieInfoAdapter adapter;
    ProgressDialog dialog;
    int netStat;

    boolean isFirstSearchRequest = true;
    int loop;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);

        settingAdapter();

        settingButtonListener(binding.button);
    }

    public void settingAdapter(){
        adapter = new MovieInfoAdapter(getApplicationContext());
        adapter.setOnItemClickListener(new MovieInfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieInfoAdapter.ViewHolder holder, View view, int position) {
                MovieInfo item = adapter.getItem(position);
                // 네트워크 연결 검사
                netStat = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(netStat == TYPE_CONNECTED) {
                    String url = item.getLink();
                    startWebView(url);
                } else GreenToast.setCustomToast(getApplicationContext(), R.string.network_not_connected, null);
            }
        });

        binding.recyclerView.setAdapter(adapter);
    }

    public void settingButtonListener(Button button){
        final Activity activity = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstSearchRequest = true;
                loop = 0;
                count = 0;

                // 네트워크 연결 검사
                netStat = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(netStat == TYPE_CONNECTED){
                    String searchWord = binding.etSearchWord.getText().toString();

                    if (searchWord.equals(""))
                        GreenToast.setCustomToast(getApplicationContext(), R.string.no_searchWord, null);
                    else {
                        showProgressDialog();
                        requestSearchWord(searchWord, 1);
                    }
                    hideKeyboard(activity);
                } else GreenToast.setCustomToast(getApplicationContext(), R.string.network_not_connected, null);
            }
        });
    }

    // 데이터 요청
    public void requestSearchWord(String searchWord, int sno){
        final Map<String, String> headers = new HashMap<>();
        headers.put(X_NAVER_ID, CLIENT_ID);
        headers.put(X_NAVER_SECRET, CLIENT_SECRET);

        String targetUrl = "https://openapi.naver.com/v1/search/movie.json?query=" + searchWord + "&display=100";
        if(sno != 0) targetUrl += "&start=" + sno;

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

                        switch(error.networkResponse.statusCode){
                            case 400:
                                GreenToast.setCustomToast(getApplicationContext(), R.string.wrong_request, null);
                                break;
                            case 404:
                                GreenToast.setCustomToast(getApplicationContext(), R.string.no_api, null);
                                break;
                            case 500:
                                GreenToast.setCustomToast(getApplicationContext(), R.string.server_error, null);
                                break;
                        }
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
        // 응답받은 json을 gson으로 파싱
        Gson gson = new Gson();
        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);

        // 새로운 검색 요청을 할 때 비워줌
        if(isFirstSearchRequest) adapter.items.clear();

        // 응답에 영화 정보가 하나라도 있으면 파싱, 어댑터에 추가
        if(0 < info.total){
            isFirstSearchRequest = false;

            MovieInfoList movieList = gson.fromJson(response, MovieInfoList.class);
            List<MovieInfo> items = movieList.items;
            adapter.addItems(items);

            // 요청 반복 횟수 설정
            if(loop == 0) loop = info.total / 100;
            // 설정된 loop보다 요청한 횟수 count가 작아야 또 요청
            if(0 < loop && count < loop){
                count++;
                // 10번 수행시 1000번을 초과하므로 제한
                if(count < 10)
                    requestSearchWord(binding.etSearchWord.getText().toString(), count * 100 + 1);
            }
        } else{
            GreenToast.setCustomToast(getApplicationContext(), R.string.no_result, binding.etSearchWord.getText().toString());
        }
        adapter.notifyDataSetChanged();
        dialog.cancel();
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

    // progressDialog 보여주기
    public void showProgressDialog(){
        dialog = new ProgressDialog(this);

        SpannableString message = new SpannableString(getString(R.string.please_wait));
        message.setSpan(new RelativeSizeSpan(1.5f), 0, message.length(), 0);
        dialog.setMessage(message);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 900;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
    }
}