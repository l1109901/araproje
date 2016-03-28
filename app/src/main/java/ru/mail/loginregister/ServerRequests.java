package ru.mail.loginregister;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServerRequests {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT=1000*15;
    public static final String SERVER_ADDRESS="http://ec2-54-210-140-139.compute-1.amazonaws.com/project1/";

    public ServerRequests(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("İşlem sürüyor");
        progressDialog.setMessage("Bekleyiniz...");
    }

    public void storeUserDataInBackground(User user,GetUserCallback userCallback){
        progressDialog.show();
        new StoreUserDataAsyncTask(user,userCallback).execute();
    }

    public void fetchUserDataInBackground(User user,GetUserCallback callback){
        progressDialog.show();
        new FetchUserDataAsyncTask(user,callback).execute();
    }

    public void storeEducationDataInBackground(Education education,long tcno){
        progressDialog.show();
        new StoreEducationDataAsyncTask(education,tcno).execute();
    }

    public void storeFirmaDataInBackground(Firma firma){
        progressDialog.show();
        new StoreFirmaDataAsyncTask(firma).execute();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void,Void,Void>{

        User user;
        GetUserCallback userCallback;

        public StoreUserDataAsyncTask(User user,GetUserCallback userCallback){
            this.user=user;
            this.userCallback=userCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("ad",user.getAd()));
            dataToSend.add(new BasicNameValuePair("soyad",user.getSoyad()));
            dataToSend.add(new BasicNameValuePair("tc_no",user.getTc_no()+""));
            dataToSend.add(new BasicNameValuePair("email",user.getEmail()));
            dataToSend.add(new BasicNameValuePair("tel",user.getTel()));
            dataToSend.add(new BasicNameValuePair("kim",user.getId()+""));
            dataToSend.add(new BasicNameValuePair("kullanici_adi",user.getKullaniciAdi()));
            dataToSend.add(new BasicNameValuePair("sifre", user.getParola1()));

            HttpParams httpRequestParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client=new DefaultHttpClient(httpRequestParams);
            HttpPost post=new HttpPost(SERVER_ADDRESS+"Register.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }


    public class FetchUserDataAsyncTask extends AsyncTask<Void,Void,User>{

        User user;
        GetUserCallback userCallback;

        public FetchUserDataAsyncTask(User user,GetUserCallback userCallback){
            this.user=user;
            this.userCallback=userCallback;
        }

        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("kullanici_adi",user.getKullaniciAdi()));
            dataToSend.add(new BasicNameValuePair("sifre", user.getParola1()));

            System.out.println(user.getKullaniciAdi());
            System.out.println(user.getParola1());

            HttpParams httpRequestParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client=new DefaultHttpClient(httpRequestParams);
            HttpPost post=new HttpPost(SERVER_ADDRESS+"FetchUserData.php");

            User returnedUser=null;
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse=client.execute(post);

                HttpEntity entity=httpResponse.getEntity();
                String result= EntityUtils.toString(entity);
                System.out.println(result);
                JSONObject jsonObject=new JSONObject(result);

                if(jsonObject.length()==0){
                    returnedUser=null;
                }else{
                    String ad=jsonObject.getString("ad");
                    String soyad=jsonObject.getString("soyad");
                    int tc_no=jsonObject.getInt("tc_no");
                    String email=jsonObject.getString("email");
                    String tel=jsonObject.getString("tel");
                    int id=jsonObject.getInt("kim");

                    returnedUser=new User(ad,soyad,tc_no,email,tel,id,user.getKullaniciAdi(),user.getParola1());
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }

    public class StoreEducationDataAsyncTask extends AsyncTask<Void,Void,Void> {

        long tcno;
        Education education;

        public StoreEducationDataAsyncTask(Education education,long tcno){
            this.education=education;
            this.tcno=tcno;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("okul_adi",education.getOkul_adi()));
            dataToSend.add(new BasicNameValuePair("bolum", education.getBolum()));
            dataToSend.add(new BasicNameValuePair("mezuniyet_yili", (education.getMezuniyet_yili() + "")));
            dataToSend.add(new BasicNameValuePair("tc_no", (tcno + "")));

            HttpParams httpRequestParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client=new DefaultHttpClient(httpRequestParams);
            HttpPost post=new HttpPost(SERVER_ADDRESS+"Education.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    public class StoreFirmaDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Firma firma;

        public StoreFirmaDataAsyncTask(Firma firma){
            this.firma=firma;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("firma_adi",firma.getFirmaAdi()));
            dataToSend.add(new BasicNameValuePair("latitude", firma.getLatitude()+""));
            dataToSend.add(new BasicNameValuePair("longtitude", firma.getLongtitude()+""));
            dataToSend.add(new BasicNameValuePair("tc_no", firma.getTcno()+""));

            HttpParams httpRequestParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client=new DefaultHttpClient(httpRequestParams);
            HttpPost post=new HttpPost(SERVER_ADDRESS+"Firma.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}