package http;

import com.google.protobuf.ByteString;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class sendHttp {


    public static void main(String[] args) {
        String uri = "http://localhost:8021/api/tmms/v3";
        String author = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoyLCJzdWIiOiIwZGNmNmMyMS04ZGFlLTQ1MjgtYTRlMy00YzkzYjJlMjRiZDgiLCJDb21wYW55SWQiOiJkNDJhYmQxMC05NzE3LTQxY2YtYTI2Ny1jODQwNGQ1OTcyYzciLCJVc2VySWQiOiIwZGNmNmMyMS04ZGFlLTQ1MjgtYTRlMy00YzkzYjJlMjRiZDgiLCJleHAiOjE1ODUyMTg1NzksImlhdCI6MTU4NTIwNDE3OSwianRpIjoiYWMyM2E0MjgtMmQ2Yy00OTJmLTlkZWItN2E3OGM0YWUxNjYzIn0.xuK70ctOzjQCDaFxEsix-1Ik3DE75X54xCz0jTWcCTE";

        for(int i =100;i<200;i++){
            URL url = null;
            try {
                url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true); // 设置可输入
                connection.setDoOutput(true); // 设置该连接是可以输出的
                connection.setRequestMethod("POST"); // 设置请求方式
                connection.setRequestProperty("Content-Type", "application/octet-stream; charset=UTF-8");
                connection.setRequestProperty("Authorization", author);
                BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());

                //request
                MonitoringFileEntity.MonitoringFile.Builder builder = MonitoringFileEntity.MonitoringFile.newBuilder();
                builder.setTunnelId("92ce174a-b706-4375-9e6c-833b8cb90fb1");
                builder.setGalleryId("55a31906-6693-432a-aa77-2a68769367c3");
                builder.setStartLegend(String.valueOf(i));
                builder.setEndLegend(String.valueOf(i+1));
                builder.setNumOfSection(452);
                builder.setRectLegend(0.05);
                builder.setScanDataType("22222");
                builder.setMonitoringTime("2020-03-19 02:21:50.839Z");
                //获取文件
                String path = "D://需求//监控量测//监控量测数据版本2//测试1//test1（里程DK0+000)/0.5_1";
                String path2 = "D://需求//监控量测//监控量测数据版本2//测试1//test1（里程DK0+000)/1_1.5";
                String path3 = "D://需求//监控量测//监控量测数据版本2//测试1//test2（里程DK0+000)/0.5_1";
                String path4 = "D://需求//监控量测//监控量测数据版本2//测试1//test2（里程DK0+000)/1_1.5";
                builder.addFilesName(i+"_"+(i+0.5));
                BufferedInputStream data = new BufferedInputStream(new FileInputStream(path3));
                int len = data.available();
                byte[] bb = new byte[len];
                data.read(bb, 0, len);
                builder.addFiles(ByteString.copyFrom(bb));
                builder.addFilesName((i+0.5)+"_"+(i+1));
                BufferedInputStream data2 = new BufferedInputStream(new FileInputStream(path4));
                int len2 = data2.available();
                byte[] bb2 = new byte[len2];
                data2.read(bb2, 0, len2);
                builder.addFiles(ByteString.copyFrom(bb2));
                MonitoringFileEntity.MonitoringFile mf = builder.build();

                os.write(mf.toByteArray());
                os.flush();
                connection.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line = null;
                StringBuilder result = new StringBuilder();
                while ((line = br.readLine()) != null) { // 读取数据
                    result.append(line + "\n");
                }
                connection.disconnect();

                System.out.println(result.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
