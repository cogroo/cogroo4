/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogroo.addon.util;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cogroo.addon.CogrooException;
import org.cogroo.addon.CogrooExceptionMessages;
import org.cogroo.addon.LoggerImpl;
import org.cogroo.addon.i18n.I18nLabelsLoader;

public class RestUtil {

    protected static Logger LOG = LoggerImpl.getLogger(RestUtil.class.getCanonicalName());

    public String get(String urlRoot, String path) throws CogrooException {
        return execute("GET", urlRoot, path, null);
    }

    public Map<String, String> post(String urlRoot, String path, Map<String, String> data) throws CogrooException {
        return extractResponse(execute("POST", urlRoot, path, convert(data)));
    }

    private String __userAgent = null;

    private String getUserAgent() {

        synchronized(this) {
            if(__userAgent == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("CoGrOO/" + I18nLabelsLoader.ADDON_VERSION + " ");
                sb.append("(" + System.getProperty("os.name") + " " + System.getProperty("os.version") + "; " + System.getProperty("os.arch") + ") ");

               __userAgent = sb.toString();
            }
        }
        return __userAgent;
    }

    public String execute(String method, String urlRoot, String path,
            String urlParameters) throws CogrooException {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            // Create connection
            url = toUrl(urlRoot, path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Language", "en-US");
            connection.setRequestProperty ( "User-agent", getUserAgent());
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if (urlParameters != null) {
                connection.setRequestProperty("Content-Length",
                        "" + Integer.toString(urlParameters.getBytes().length));

                // Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
            }


            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (ConnectException e) {
            LOG.log(Level.SEVERE, "Couldn't connect to host. Is it online? URL: " + url + " method: " + method, e);
            throw new RestConnectionException(CogrooExceptionMessages.COMMUNITY_CONNECT_EXCEPTION, null);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Communication error. URL: " + url + " method: " + method, e);
            throw new CogrooException(CogrooExceptionMessages.COMMUNITY_COMMUNICATION_EXCEPTION, new String[]{url.toString(), method});
        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    private static final Pattern imgSrc = Pattern.compile("<img\\s+id=\"gaMobileTrackingCode\"\\s+src=\"([^\"]+)\".*");

    public void getGAImg(String urlRoot, String path) throws CogrooException {
        try {
            String resp = get(urlRoot, path);
            if (resp != null) {
                Matcher m = imgSrc.matcher(resp);
                if (m.find()) {
                    String respImg = get(urlRoot, m.group(1));
                    if (respImg == null) {
                        LOG.log(Level.WARNING, "Couldn't get GA img! Get image returned null");
                    }
                }
            } else {
                LOG.log(Level.WARNING, "Couldn't get GA img!");
            }
        } catch (Exception e) {
            // just log, we don't care if it fail.
            LOG.log(Level.WARNING, "Couldn't get GA img!", e);
        }
    }

    private static URL toUrl(String urlRoot, String path)
            throws MalformedURLException {
        return new URL(urlRoot + "/" + path);
    }

    private static String convert(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        for (String key : data.keySet()) {
            sb.append(encode(key) + "=" + encode(data.get(key)));
            sb.append("&");
        }
        return sb.subSequence(0, sb.length() - 1).toString();
    }

    private static String encode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    private static final Pattern responsePattern = Pattern.compile("\\$\\{(.*?)\\|(.*?)\\}&");

    // response have the format:
    // ${key|value}&
    // one response per line
    public static String prepareResponse(String key, String data) {
        return "${" + key + "|" + data + "}&";
    }

    public static Map<String, String> extractResponse(String response) {
        Map<String, String> data = new HashMap<String, String>();
        Matcher theMatcher = responsePattern.matcher(response);
        while (theMatcher.find()) {
            data.put(theMatcher.group(1), theMatcher.group(2));
        }
        return data;
    }
}
