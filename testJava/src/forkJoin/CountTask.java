package forkJoin;

import java.util.concurrent.RecursiveTask;

//RecursiveTask有返回值，RecursiveAction 没有返回值
public class CountTask extends RecursiveTask<Long> {

    Long maxCountRange = 100000000l;//最大计算范围
    Long startNum, endNum;

    public CountTask(Long startNum, Long endNum) {
        this.startNum = startNum;
        this.endNum = endNum;
    }

    @Override
    protected Long compute() {
        long range = endNum - startNum;
        long sum = 0;
        if (range >= maxCountRange) {//如果这次计算的范围大于了计算时规定的最大范围，则进行拆分
            //每次拆分时，都拆分成原来任务范围的一半
            //如1-10,则拆分为1-5,6-10
            Long middle = (startNum + endNum) / 2;
            CountTask subTask1 = new CountTask(startNum, middle);
            CountTask subTask2 = new CountTask(middle + 1, endNum);
            //拆分后，执行fork
            subTask1.fork();
            subTask2.fork();

            sum += subTask2.join();
            sum += subTask1.join();
        } else {//在范围内，则进行计算
            for (; startNum <= endNum; startNum++) {
                sum += startNum;
            }
        }
        return sum;
    }
}
