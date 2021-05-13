package com.surjo.oauth.notification;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sanjoy on 5/17/17.
 */
@Service
public class SmsNotificationServiceImpl implements SmsNotificationService {
    @Override
    public boolean sendSms(String mobileNo, String msg) throws Exception {
            String url ="http://202.51.191.68/bulkpush/";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String str="user=sibl&pass=sibl321&msg_in_id=7879834&mobileno="+mobileNo+"&msgbody="+msg;
            String urlParameters=str;

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode==200){
                return true;
            }
            else
            {
                return false;
            }

        }
    }

