package net.synchthia.systera.chat;

import biscotte.kana.Kana;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Japanize {
    private static final Pattern pattern = Pattern.compile("[^\u0020-\u007E]|\u00a7|u00a74u00a75u00a73u00a74v");

    public String convert(String convertString) {
        Kana kana = new Kana();
        Matcher matcher = pattern.matcher(convertString);
        //URLっぽい文章は省く
        if (convertString.startsWith("http:") || convertString.startsWith("https:")) {
            return "";
        }
        //母音が無かったら省く
        if (!convertString.contains("a") && !convertString.contains("i") && !convertString.contains("u") && !convertString.contains("e") && !convertString.contains("o") && !convertString.contains("nn")) {
            return "";
        }
        //matchで指定されている、英語以外の文字列が含まれている場合, 文字の頭に#が付いている場合は無視
        if (matcher.find(0) || convertString.startsWith("#")) {
            return "";
        }
        kana.setLine(convertString);
        kana.convert();
        String converted = kana.getLine()
                .replace("-", "ー")
                .replace("~", "～")
                .replace(",", "、")
                .replace(".", "。");

        try {
            String encodeString = URLEncoder.encode(converted, "utf-8");
            String apiURLString = "https://www.google.com/transliterate?langpair=ja-Hira|ja&text=" + encodeString;
            URL apiURL = new URL(apiURLString);
            HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
            connection.setConnectTimeout(1000);
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder builder = new StringBuilder();
            JsonElement rootElement = new JsonParser().parse(br.readLine());
            Iterator<JsonElement> iterator = rootElement.getAsJsonArray().iterator();
            while (iterator.hasNext()) {
                JsonArray rootArray = iterator.next().getAsJsonArray();
                JsonArray jsonArray = rootArray.get(1).getAsJsonArray();
                builder.append(jsonArray.get(0).getAsString());
            }
            //長過ぎたりして何も返さなかったら、Google日本語で変換する前の文字をそのまま帰す
            if (!converted.isEmpty()) {
                converted = builder.toString();
            }
            return converted;
        } catch (Exception ex) {
            ex.printStackTrace();
            return converted;
        }
    }
}
