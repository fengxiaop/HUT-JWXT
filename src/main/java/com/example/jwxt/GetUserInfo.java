package com.example.jwxt;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

/**
 * 登录后获取用户信息
 * 相关解析HTMl的操作不是固定的
 * 需要结合自己的需求来操作
 * 此处仅作为演示。
 */
public class GetUserInfo {
    static int idx = 2;
    public static void cj() throws Exception {
        for (int i = 0; i < 1000000000; ) {
            LoginPz loginPz = new LoginPz();
            String cookies = loginPz.hello();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            try {
                HttpGet httpGet = new HttpGet("http://218.75.197.123:83/jsxsd/kscj/cjcx_list?kksj=2021-2022-1");
                //增加头信息
                //注意此处需要修改为正确的JSESSIONID 和 SERVERID
                httpGet.addHeader("Cookie", "JSESSIONID" + "=" + cookies + "; SERVERID=121; JSESSIONID=8FFFAEA49DC840CE5A3135330C06CED3");
                httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");
                httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                httpGet.addHeader("Connection", "keep-alive");

                //执行
                CloseableHttpResponse response = httpClient.execute(httpGet);
                String html = EntityUtils.toString(response.getEntity(), "utf8");
//                System.out.println("结果：" + html);
                //解析html

                Document parse = Jsoup.parse(html);
                parseHtml(parse);
                Thread thread = new Thread();
                thread.sleep(600000);
            } catch (Exception e) {
            }

        }
    }

    private static void parseHtml(Document parse) throws Exception {
//        int idx = 2;//目前已出成绩科目
        //选择table
        Element table = parse.getElementById("dataList");
        //选择tr
        Elements cells = table.select("tr");

        StringBuilder stringBuilder = new StringBuilder(); //用来存储成绩信息
        //自己存储的每一行数据
        List<List<String>> tables = new ArrayList<>();

        for (int index = 1; index < cells.size(); index++) {
            //第一行是表头 index = 0 跳过

            //第二行开始table数据

            Element row = cells.get(index);

            //搜索tr下的所有的td
            Elements rows = row.select("td");

            //每一行的数据
            List<String> dataList = new ArrayList<>();

            for (Element element : rows) {
                dataList.add(element.text());
            }
            tables.add(dataList);
        }

        //获取表头
        Elements headers = cells.get(0).select("th");
        List<String> tableHeader = headers.stream()
                .map(Element::text)
                .collect(Collectors.toList());

        //打印数据
        for (String str : tableHeader) {
            System.out.printf(str + "    ");
            stringBuilder.append(str + "      ");
        }
        stringBuilder.append("\r\n");
        System.out.println("");
        for (List<String> strs : tables) {
            for (String str : strs) {
                System.out.printf(str + "        ");
                stringBuilder.append(str + "         ");
            }
            stringBuilder.append("\r\n");
            System.out.println("");
        }
        System.out.println();
//        System.out.println(tables.size());
        if (tables.size() > 1)//idx 为原先的成绩数量123
        {
            //发送邮件给自己
            System.out.println("这是一个标志");
            SendMailUtil.sendEmail("1493020035@qq.com", "成绩更新", stringBuilder.toString());
            idx = tables.size();
        }
    }
}
