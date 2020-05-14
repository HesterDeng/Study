package file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AnalysisFile {
    public static void main(String args[]){
        String path = "D://需求//监控量测//13526121//新增监测区间0//tyck004（里程DK135+261.21)//135250_135251";
        String path2 = "D://需求//监控量测//监控量测白道//新增监测区间0//tyck003-1（里程DK135+261.54)//135240_135241";
        String path3 = "D://需求//监控量测//监控量测白道2//新增监测区间0//tyck003（里程DK135+261.54)//135260_135261";
        String path4 = "D://需求//监控量测//监控量测白道2//新增监测区间0//tyck004（里程DK135+261.21)//135260_135261";
        File file = new File(path3);
        double cjValueMin = Double.MAX_VALUE;
        double cjValueMax = Double.MIN_VALUE;

        PCASBinReader reader = null;
        try {
            reader = new PCASBinReader(new BufferedInputStream(new FileInputStream(file)));
            double[] data = null;
            int verticalNum = reader.readJavaInt32();
            int horizonNum = 0;
            System.out.println("V:"+verticalNum);
            for (int i = 0; i < verticalNum; i++) {
                double mile = reader.readJavaDouble();
                horizonNum = reader.readJavaInt32();
                System.out.println("H:"+horizonNum);
                if(data == null){
                    data = new double[horizonNum*verticalNum*4];
                }
                double minH = Double.MAX_VALUE;
                int minIndex = -1;
                for (int j = 0; j < horizonNum; j++) {
                    double horizon = reader.readJavaDouble();
                    data[(i*horizonNum+j)*4+0] = horizon;
                    double vertical = reader.readJavaDouble();
                    data[(i*horizonNum+j)*4+1] = vertical;
                    double distance = reader.readJavaDouble();
                    data[(i*horizonNum+j)*4+2] = distance;
                    double thickness = reader.readJavaDouble();
                    data[(i*horizonNum+j)*4+3] = thickness;
                    if(thickness != -1000D&&thickness<cjValueMin){
                        cjValueMin = thickness;
                    }
                    if(thickness>cjValueMax){
                        cjValueMax = thickness;
                    }
                    if (horizon != -1000D && Math.abs(horizon) < minH){
                        minH = Math.abs(horizon);
                        minIndex = j;
                    }
                }
                System.out.println("minIndex:"+minIndex);
            }
            System.out.println(data.length);
            saveLocal(data,"ttttt2.txt",horizonNum);
            System.out.println(cjValueMin);
            System.out.println(cjValueMax);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveLocal(double[] data,String fileName,int horizonNum){

        try {

            File writeName = new File("D:\\"+fileName); // 相对路径，如果没有则要建立一个新的output.txt文件
            if(!writeName.exists()) {
                writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            }
            FileWriter writer = new FileWriter(writeName);
            BufferedWriter out = new BufferedWriter(writer);
            int section = 0;
            for(int i =0;i<data.length;i++){
                if(i%(horizonNum*4) == 0){
                    section++;
                    out.newLine();
                    out.write(section+" ");
                    out.newLine();
                }
                if(i%4 == 0){
                    out.newLine();
                    out.write(String.valueOf(data[i])+" ");
                }else{
                    out.write(String.valueOf(data[i])+" ");
                }
            }

            out.flush(); // 把缓存区内容压入文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
