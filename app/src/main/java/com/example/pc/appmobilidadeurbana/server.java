package com.example.pc.appmobilidadeurbana;


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

    public static Utilizador postLoginHttp (String user, String pass) {
        Utilizador utilizador = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://projetos.est.ipcb.pt/.../getLogin.php");

        try {
            ArrayList<NameValuePair> val = new ArrayList <NameValuePair>();

            val.add(new BasicNameValuePair("username", user));
            val.add(new BasicNameValuePair("password", pass));


            httpPost.setEntity(new UrlEncodedFormEntity(val));
            HttpResponse resposta = httpClient.execute(httpPost);


            try {
                final String resp = EntityUtils.toString(resposta.getEntity());
                JSONObject reader = new JSONObject(resp);
                JSONArray userJSon  = reader.getJSONArray("user");


                if (userJSon.length() > 0)
                    if (!userJSon.getJSONObject(0).getString("username").isEmpty()) {
                        utilizador = new Utilizador();
                        utilizador.setUsername(userJSon.getJSONObject(0).getString("username"));
                        utilizador.setNome(userJSon.getJSONObject(0).getString("name"));
                        utilizador.setPassword(userJSon.getJSONObject(0).getString("password"));
                        utilizador.setEmail(userJSon.getJSONObject(0).getString("email"));
                    }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        catch (ClientProtocolException e) {}
        catch (IOException e) {}

        return utilizador;
    }
}
