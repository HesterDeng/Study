package utils;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Auther: dengguiping
 * @Date: 2019/10/8 17:53
 * @Description:
 */
public class Word2PdfUtils {
    public static boolean getLicense() {
        boolean result = false;
        try {
            ClassPathResource resource = new ClassPathResource("/config/license.xml");
            InputStream is = resource.getInputStream();
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void doc2pdf(InputStream inPath, OutputStream os) {

        if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        try {
            long old = System.currentTimeMillis();
//            File file = new File(outPath); // 新建一个空白pdf文档
//            FileOutputStream os = new FileOutputStream(file);
            Document doc = new Document(inPath); // Address是将要被转化的word文档
            doc.save(os, SaveFormat.PDF);// 全面支持DOC, DOCX, OOXML, RTF HTML,
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
