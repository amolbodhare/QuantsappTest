package com.example.quantsapptest.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.quantsapptest.AppController;
import com.example.quantsapptest.R;
import com.example.quantsapptest.activities.MainActivity;
import com.example.quantsapptest.adapters.ApiListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ApiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApiFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View fragMentView;
    private static RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    ApiListAdapter apiListAdapter;
    Context context;
    JSONArray jsonArray;

    // json object response url
    private String urlJsonObj = "https://api.androidhive.info/feed/feed.json";

    private static String TAG = MainActivity.class.getSimpleName();
   // private Button btnMakeObjectRequest, btnMakeArrayRequest;

    // Progress dialog
    private ProgressDialog pDialog;


    public ApiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ApiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ApiFragment newInstance(String param1, String param2) {
        ApiFragment fragment = new ApiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_api, container, false);
        if (fragMentView == null) {
            fragMentView = inflater.inflate(R.layout.fragment_api, container, false);
            context = getActivity();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);;
            jsonArray=new JSONArray();

            recyclerView = fragMentView.findViewById(R.id.eventListRecyclerView);

            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setItemAnimator(new DefaultItemAnimator());

            if (isInternetAvailable(context))
                makeJsonObjectRequest();
            else
                Toast.makeText(context, "Internet is not available", Toast.LENGTH_SHORT).show();


            final SwipeRefreshLayout swipeRefreshLayout = fragMentView.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                    if (isInternetAvailable(context))
                       makeJsonObjectRequest();
                    else
                        Toast.makeText(context, "Internet is not available", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return fragMentView;
    }
    private void makeJsonObjectRequest() {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    jsonArray=response.getJSONArray("feed");

                    apiListAdapter=null;
                    apiListAdapter = new ApiListAdapter(context,jsonArray);
                    recyclerView.setAdapter(apiListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    public static boolean isInternetAvailable(Context context) {
        boolean connected = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    // connected to wifi
                    connected = true;
                    break;

                case ConnectivityManager.TYPE_MOBILE:
                    // connected to mobile data
                    connected = true;
                    break;

                default:
                    connected = false;
            }
        } else {
            // not connected to the internet
            connected = false;
        }

        return connected;
    }
}
