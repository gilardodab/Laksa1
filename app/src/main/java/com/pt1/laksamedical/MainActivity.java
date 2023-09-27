package com.pt1.laksamedical;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.service.controls.templates.ToggleRangeTemplate;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    public static int CLEAR_CART=0;
    RecyclerView mRecyclerView;
    MyAdapter myAdapter;
    ArrayList<Model> models ;
    public RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        models = new ArrayList<>();
        mQueue = VolleySingleton.getInstance(this).getmRequestqueue();
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        models= new ArrayList<>();
        myAdapter = new MyAdapter(this,models);
        mRecyclerView.setAdapter(myAdapter);
        getData();



    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        String url = "paste your link here";

        JsonObjectRequest ObjRequest = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray result = response.getJSONArray("result");

                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jo = result.getJSONObject(i);
                                String title = jo.getString("title");
                                String cost = jo.getString("cost");
                                int id = Integer.parseInt(jo.getString("item_id"));
                                String imgstr = jo.getString("img");

                                Model m = new Model();
                                m.setTitle(title.substring(0, 1).toUpperCase() + title.substring(1));
                                m.setCost(cost);
                                m.setId(id);
                                m.setQuant(0);
                                m.setImg(imgstr);

                                models.add(m);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        myAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        myAdapter.list=new ArrayList<>(models);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }) {

        };
        //Toast.makeText(this,"Done",Toast.LENGTH_LONG).show();
        mQueue.add(ObjRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutop,menu);
        MenuItem search = menu.findItem(R.id.search);

        MenuItem profile = menu.findItem(R.id.profile);
        SearchView searchView = (SearchView) search.getActionView();
        //myAdapter.list=new ArrayList<>(models);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                myAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.cart){

            Intent intent = new Intent(this,cart_activity.class) ;
            //intent.putExtra("cart",models);
            Bundle extra = new Bundle();
            extra.putSerializable("objects", myAdapter.cart);
            intent.putExtra("cart",extra);
            startActivityForResult(intent,0);

        }
        if(id==R.id.profile){
            Intent intent = new Intent(this,Profile_activity.class)    ;
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if(CLEAR_CART==1){

            myAdapter = new MyAdapter(this,models);
            mRecyclerView.setAdapter(myAdapter);

        }
        super.onResume();
    }

    int backButtonCount=0;
    @Override
    public void onBackPressed()
    {

        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }



}