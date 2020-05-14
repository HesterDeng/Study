package http;

import com.google.protobuf.ByteString;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class sendHttp2 {


    public static void main(String[] args) {
        String uri = "http://localhost:8021/api/tmms/v3";
        String author = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoyLCJzdWIiOiJlMjUwYWY1NS0yM2U0LTQ0ZjktYWQ2OS03NTc0Y2FkYWFlMTIiLCJDb21wYW55SWQiOiIyNGY0NWNhOC01NDQ2LTQ5ZWQtODE4NS1kZjJiNmIwZDFiNTIiLCJVc2VySWQiOiJlMjUwYWY1NS0yM2U0LTQ0ZjktYWQ2OS03NTc0Y2FkYWFlMTIiLCJleHAiOjE1ODg5MTY1NTgsImlhdCI6MTU4ODkwMjE1OCwianRpIjoiMjY4NjU1NmUtMGVhYS00NzJmLWFjYzktNDJhNWFlYjY4MGE5In0.gTDiLLvnQHyMC5crfPor-bcEEPa9vrI80QJ2dfIHoCo";

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
            builder.setTunnelId("7c84e6ea-d259-4789-b2ea-bf62d7078d39");
            builder.setGalleryId("89ff2dad-3311-44e6-9d4d-5d692efcac52");
            builder.setStartLegend(String.valueOf(135240));
            builder.setEndLegend(String.valueOf(135280));
            builder.setRectLegend(0.05);
            builder.setScanDataType("22222");
            builder.setMonitoringTime("2020/03/28 01:21:50");
            String path = "D://需求//监控量测//监控量测白道//新增监测区间0//tyck003-1（里程DK135+261.54)";
            String path2 = "D://需求//监控量测//监控量测白道//新增监测区间0//tyck004（里程DK135+261.21)";
            String path3 = "D://需求//监控量测//监控量测白道2//新增监测区间0//tyck003（里程DK135+261.54)";
            String path4 = "D://需求//监控量测//监控量测白道2//新增监测区间0//tyck004（里程DK135+261.21)";
            File file = new File(path2);
            File[] files = file.listFiles();
            String[] names = file.list();
            for (int i = 0; i < names.length; i++) {
                String[] pp = names[i].split("_");
                int start = Integer.parseInt(pp[0]);
                int end = Integer.parseInt(pp[1]);
                builder.addFilesName(start + "_" + end);

                BufferedInputStream data = new BufferedInputStream(new FileInputStream(files[i]));
                int len = data.available();
                byte[] bb = new byte[len];
                data.read(bb, 0, len);
                builder.addFiles(ByteString.copyFrom(bb));
            }

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
