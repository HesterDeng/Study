package file;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.text.NumberFormat;

/**
 * @Version 0.0.1
 * @Description PCAS二进制文件读取工具类
 * @Author lxt
 * @Date 2019/7/5 9:26
 */
public class PCASBinReader extends DataInputStream {

    private byte[] bytes = new byte[8];
    private byte[] intBytes = new byte[4];
    private static final NumberFormat format = NumberFormat.getInstance();
    static {
        //取消科学计数
        format.setGroupingUsed(false);
        //直接截断
        format.setRoundingMode(RoundingMode.DOWN);
        //保留16位小数
        format.setMaximumFractionDigits(8);
    }
    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public PCASBinReader(InputStream in) {
        super(in);
    }

    /**
     * 读取C# 二进制 double
     * @return double文件
     * 此处的原因是，java读取的东西和c#写入的不一致
     * 需要将数组反序
     */
    public double readJavaDouble(){

        try {
            int read = in.read(bytes);
            if (read < 0 ) return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0,j = bytes.length -1; i < j ; i++ ,j--) {
            byte temp = bytes[i];
            bytes[i] = bytes[j];
            bytes[j] = temp;
        }

        return new BigDecimal(format.format(ByteBuffer.wrap(bytes).asDoubleBuffer().get())).doubleValue();
    }

    public int readJavaInt32(){
        try {
            int read = in.read(intBytes);
            if (read < 0 ) return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0,j = intBytes.length -1; i < j ; i++ ,j--) {
            byte temp = intBytes[i];
            intBytes[i] = intBytes[j];
            intBytes[j] = temp;
        }

        return new Integer(format.format(ByteBuffer.wrap(intBytes).asIntBuffer().get())).intValue();
    }
}
