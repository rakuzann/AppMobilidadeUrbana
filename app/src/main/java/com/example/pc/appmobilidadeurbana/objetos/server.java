package com.example.pc.appmobilidadeurbana.objetos;


import android.util.Log;

import com.example.pc.appmobilidadeurbana.objetos.Utilizador;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class server {

    public static Utilizador postLoginHttp(String user, String pass) {
        Utilizador utilizador = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://projectos.est.ipcb.pt/MyBestTransfer/getLogin.php");

        try {
            ArrayList<NameValuePair> val = new ArrayList<NameValuePair>();

            val.add(new BasicNameValuePair("username", user));
            val.add(new BasicNameValuePair("password", pass));


            httpPost.setEntity(new UrlEncodedFormEntity(val));
            HttpResponse resposta = httpClient.execute(httpPost);


            try {
                final String resp = EntityUtils.toString(resposta.getEntity());
                JSONObject reader = new JSONObject(resp);
                JSONArray userJSon = reader.getJSONArray("user");


                if (userJSon.length() > 0)
                    if (!userJSon.getJSONObject(0).getString("username").isEmpty()) {
                        utilizador = new Utilizador();
                        utilizador.setId(userJSon.getJSONObject(0).getInt("id"));
                        utilizador.setUsername(userJSon.getJSONObject(0).getString("username"));
                        utilizador.setNome(userJSon.getJSONObject(0).getString("nome"));
                        utilizador.setPassword(userJSon.getJSONObject(0).getString("password"));
                        utilizador.setEmail(userJSon.getJSONObject(0).getString("email"));
                    }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        return utilizador;
    }

    public static boolean existeUser(String user) {
        Utilizador utilizador = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://projectos.est.ipcb.pt/MyBestTransfer/getUser.php");

        try {
            ArrayList<NameValuePair> val = new ArrayList<NameValuePair>();

            val.add(new BasicNameValuePair("username", user));


            httpPost.setEntity(new UrlEncodedFormEntity(val));
            HttpResponse resposta = httpClient.execute(httpPost);


            try {
                final String resp = EntityUtils.toString(resposta.getEntity());
                JSONObject reader = new JSONObject(resp);
                JSONArray userJSon = reader.getJSONArray("user");


                if (userJSon.length() > 0)
                    if (!userJSon.getJSONObject(0).getString("username").isEmpty()) {
                        return true;
                    }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        return false;
    }

    public static void postRegisterHttp(String user, String pass, String nome, String email) {

        Utilizador utilizador = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://projectos.est.ipcb.pt/MyBestTransfer/register.php");

        try {

            ArrayList<NameValuePair> val = new ArrayList<NameValuePair>();

            val.add(new BasicNameValuePair("username", user));
            val.add(new BasicNameValuePair("password", pass));
            val.add(new BasicNameValuePair("nome", nome));
            val.add(new BasicNameValuePair("email", email));


            httpPost.setEntity(new UrlEncodedFormEntity(val));
            httpClient.execute(httpPost);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void postFavorito(String latitude, String longitude, String id_user) {

        Utilizador utilizador = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://projectos.est.ipcb.pt/MyBestTransfer/addFavorito.php");

        try {

            ArrayList<NameValuePair> val = new ArrayList<NameValuePair>();

            val.add(new BasicNameValuePair("latitude", latitude));
            val.add(new BasicNameValuePair("longitude", longitude));
            val.add(new BasicNameValuePair("iduser", id_user));


            httpPost.setEntity(new UrlEncodedFormEntity(val));
            httpClient.execute(httpPost);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Favorito> postHttpGetFavoritos(String id_user) {
        Favorito favorito = null;
        ArrayList<Favorito> arrayFav = new ArrayList<>();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://projectos.est.ipcb.pt/MyBestTransfer/getFavoritos.php");

        try {

            ArrayList<NameValuePair> val = new ArrayList<NameValuePair>();

            val.add(new BasicNameValuePair("user", id_user));

            httpPost.setEntity(new UrlEncodedFormEntity(val));

            HttpResponse resposta = httpClient.execute(httpPost);

            try {
                final String resp = EntityUtils.toString(resposta.getEntity());
                JSONObject reader = new JSONObject(resp);
                JSONArray favJSon  = reader.getJSONArray("FAV");

                for (int i = 0; i < favJSon.getJSONArray(0).length(); i++) {
                    favorito = new Favorito();

                    JSONObject idk = (JSONObject)favJSon.getJSONArray(0).getJSONObject(i);

                    favorito.setId(idk.getInt("id"));
                    favorito.setLatitude(idk.getDouble("latitude"));
                    favorito.setLongitude(idk.getDouble("longitude"));
                    favorito.setId_user(idk.getInt("id_utilizador"));

                    //favorito.setId(favJSon.getJSONObject(0).getInt("id"));
                    //favorito.setLatitude(favJSon.getJSONObject(0).getDouble("latitude"));
                    //favorito.setLongitude(favJSon.getJSONObject(0).getDouble("longitude"));
                    //favorito.setId_user(favJSon.getJSONObject(0).getInt("id_utilizador"));

                    arrayFav.add(favorito);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        catch (ClientProtocolException e) {}
        catch (IOException e) {}

        return arrayFav;
    }

}
