package com.example.quantsapptest.fragments;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.quantsapptest.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link URLFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class URLFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    Context context;
    private String mParam1;
    private String mParam2;
    AsyncHttpClient client;
    private ProgressDialog pDialog;
    Workbook workbook;
    WebView webView;
    Button downloadBtn,webViewBtn;
    private View fragMentView;
    private static final int STORAGE_REQUEST_CODE = 400;
    String storagePermission[];
    DownloadManager downloadManager;
    String url = "https://qapptemporary.s3.ap-south-1.amazonaws.com/ritesh/zip_files/44418/Annexure123456&7_FO.xls";

    public URLFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment URLFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static URLFragment newInstance(String param1, String param2) {
        URLFragment fragment = new URLFragment();
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
        //return inflater.inflate(R.layout.fragment_url, container, false);
        if (fragMentView == null) {
            fragMentView = inflater.inflate(R.layout.fragment_url, container, false);
            webViewBtn = fragMentView.findViewById(R.id.webViewBtn);
            downloadBtn = fragMentView.findViewById(R.id.downloadBtn);
            webView=fragMentView.findViewById(R.id.wenView);
            client = new AsyncHttpClient();
            storagePermission = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
            context=getContext();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);;
            webViewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showpDialog();
                    client.get(url, new FileAsyncHttpResponseHandler(getContext()) {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                            hidepDialog();
                            Toast.makeText(context, "WebView Failed,,Try Again ...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File file) {

                            Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show();
                            //Now Read the File
                            WorkbookSettings ws = new WorkbookSettings();
                            ws.setGCDisabled(true);
                            if(file !=null)
                            {
                                try {
                                    workbook=Workbook.getWorkbook(file);
                                    int sheetsLength= workbook.getSheets().length;
                                    ArrayList<String> mainarrayList=new ArrayList<String>();
                                    for(int i=0;i<sheetsLength;i++)
                                    {
                                        Sheet sheet=workbook.getSheet(i);

                                        ArrayList<String> arrayList=new ArrayList<String>();
                                        for(int j=0;j<sheet.getRows();j++) {
                                            Cell[] row = sheet.getRow(j);
                                            String s = "";
                                            for (int k = 0; k < row.length; k++) {
                                                s = s + row[k].getContents()+"&nbsp&nbsp&nbsp&nbsp";
                                            }
                                            arrayList.add(s);
                                        }

                                        for(int l=0;l<arrayList.size();l++)
                                        {
                                            mainarrayList.add(arrayList.get(l));
                                        }

                                    }

                                    String webString="";
                                    for(int m=0;m<mainarrayList.size();m++)
                                    {
                                        webString=webString+mainarrayList.get(m)+"<br><br><br>";

                                    }

                                    String unCodedHtmlString="<html><body>"+webString+"</body></html>";

                                    String enCodedHtmlString= Base64.encodeToString(unCodedHtmlString.getBytes(),Base64.NO_PADDING);
                                    webView.loadData(enCodedHtmlString,"text/html","base64");
                                    hidepDialog();;

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (BiffException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            });
            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                        if (!checkStoragePermission()) {
                            requestStoragePermission();
                        } else {

                            if(isInternetAvailable(getContext()))
                            {
                                downloadFile();
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    else {
                        if(isInternetAvailable(getContext()))
                        {
                            downloadFile();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
        return fragMentView;
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
    private boolean checkStoragePermission() {

        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if ( writeAccepted) {
                        if(isInternetAvailable(getContext()))
                        {
                            downloadFile();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        }
    }

    public  void downloadFile()
    {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse("https://qapptemporary.s3.ap-south-1.amazonaws.com/ritesh/zip_files/44418/Annexure123456&7_FO.xls");
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //request.setTitle("Excel File");
        request.setDescription("Donloading Excel File");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //request.setAllowedOverMetered(true);
        request.setDestinationInExternalPublicDir("/Quantsapp", "ExcelFile.xls");
        Long reference = downloadManager.enqueue(request);

    }
    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
