package com.polarbearr.cinemasearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText searchText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchWord = searchText.getText().toString();
                requestSearchWord(searchWord);
            }
        });
    }

    public void requestSearchWord(String searchWord){
        String targetUrl = "" + searchWord;
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
                    }
                }
        );

        request.setShouldCache(false);
        HttpHelper.getInstance(getBaseContext()).addToRequestQueue(request);
    }

    public void processResponse(String response){
        // TODO : 응답받은 json을 gson으로

        // TODO : 리사이클러뷰에 어댑터 설정
    }
}
