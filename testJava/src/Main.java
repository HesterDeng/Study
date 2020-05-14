import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Double[] aa = new Double[853];
        aa[790] = 55.5;
        List<Double> bb = Arrays.asList(aa);
        bb.stream().forEach(it->{
            System.out.println(it);
        });
        System.out.println(bb.size());
    }

    public static double getDayBetwwen(LocalDateTime t1,LocalDateTime t2){
        if(t1 == null || t2 == null) return 0;
        double second = ChronoUnit.SECONDS.between(t1,t2);
        return (double) Math.round(second/(24*60*60)*100)/100;
    }



}
