package file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class filetest {
    public void saveLocal(double[] data,String fileName){

        try {

            File writeName = new File("D:\\"+fileName); // 相对路径，如果没有则要建立一个新的output.txt文件
            if(!writeName.exists()) {
                writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            }
            FileWriter writer = new FileWriter(writeName);
            BufferedWriter out = new BufferedWriter(writer);
            for(int i =0;i<data.length;i++){
                if((i+1)%3 == 0){
                    out.newLine();
                }else{
                    out.write(String.valueOf(data[i]));
                    out.newLine();
                }
            }

            out.flush(); // 把缓存区内容压入文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        filetest test = new filetest();
        double[] data = {1.0,2,3,4,5};
        test.saveLocal(data,"test.txt");
    }
}
