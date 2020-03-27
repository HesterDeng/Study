package forkJoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class Test {
    public static void main(String[] args) {
        Long startNum = 1l;
        Long endNum = 1000000000l;

        long startTime = System.currentTimeMillis();

        CountTask countTask = new CountTask(0l, 500000000l);
        CountTask countTask2 = new CountTask(5000l, 1000000l);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Future<Long> result = forkJoinPool.submit(countTask);
        Future<Long> result2 = forkJoinPool.submit(countTask2);
        try {
            System.out.println("结果--》result1:" + result.get());
            System.out.println("结果--》result2:" + result2.get());
            long endTime = System.currentTimeMillis();
            System.out.println("costTime:" + (endTime - startTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
