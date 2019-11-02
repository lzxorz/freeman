package com.freeman.juc;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.stream.LongStream;

public class ForkJoinPoolDemo {
	
	public static void main(String[] args) {
        test1();
        test2();
    }


    public static void test1() {
        try {
            // 创建包含Runtime.getRuntime().availableProcessors()返回值作为个数的并行线程的ForkJoinPool
            ForkJoinPool forkJoinPool = new ForkJoinPool();

            // 创建ForkJoin 任务
            ForkJoinTask<Long> task = new ForkJoinSumCalculate(0L, 500000000L);

            // 提交可分解的PrintTask任务
            ForkJoinTask<Long> sum = forkJoinPool.submit(task);

            // 关闭线程池
            forkJoinPool.shutdown();

            System.out.println("计算结果： "+sum.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public static void test2() {
        // ForkJoinPool 的每个工作线程都维护着一个工作队列（WorkQueue），这是一个双端队列（Deque），里面存放的对象是任务（ForkJoinTask）。
        // 每个工作线程在运行中产生新的任务（通常是因为调用了 fork()）时，会放入工作队列的队尾，并且工作线程在处理自己的工作队列时，使用的是 LIFO 方式，也就是说每次从队尾取出任务来执行。
        // 每个工作线程在处理自己的工作队列同时，会尝试窃取一个任务（或是来自于刚刚提交到 pool 的任务，或是来自于其他工作线程的工作队列），窃取的任务位于其他线程的工作队列的队首，也就是说工作线程在窃取其他工作线程的任务时，使用的是 FIFO 方式。
        // 在遇到 join() 时，如果需要 join 的任务尚未完成，则会先处理其他任务，并等待其完成。
        // 在既没有自己的任务，也没有可以窃取的任务时，进入休眠。
        ForkJoinPool pool = new ForkJoinPool(); // ForkJoinPool.commonPool()

        // 创建ForkJoin 任务
        ForkJoinTask<Long> task = new ForkJoinSumCalculate(0L, 500000000L);

        // ForkJoinPool 执行任务
        Long sum = pool.invoke(task);
        System.out.println("计算结果： "+sum);
    }

	//java8 新特性
	public static void testJava8(){
		Instant start = Instant.now();

		Long sum = LongStream.rangeClosed(0L, 500000000L).parallel().reduce(0L, Long::sum);

		System.out.println("计算结果： "+sum);

		Instant end = Instant.now();

		System.out.println("耗费时间为：" + Duration.between(start, end).toMillis());
	}

}

class ForkJoinSumCalculate extends RecursiveTask<Long>{

	private static final long serialVersionUID = -1L;
	
	private long start;
	private long end;
	
	private static final long THURSHOLD = 10000L;  // 停止继续拆分的临界值
	
	public ForkJoinSumCalculate(long start, long end) {
		this.start = start;
		this.end = end;
	}

	// compute 把任务拆分,拆到不能再拆,再计算
	@Override
	protected Long compute() {
		long num = end - start;
		
		if(num <= THURSHOLD){
			long sum = 0L;
			for (long i = start; i <= end; i++) {
				sum += i;
			}
			return sum;
		}else{
			long middle = (start + end) / 2;
			
			// 将大任务分解成两个小任务，同时压入线程队列
			ForkJoinSumCalculate left = new ForkJoinSumCalculate(start, middle); 
			ForkJoinSumCalculate right = new ForkJoinSumCalculate(middle+1, end);
			
			// ForkJoinTask#fork 方法只做一件事，既是把任务推入当前工作线程的工作队列里。
			left.fork(); 
			right.fork();
			

			// ForkJoinTask join 方法
			// 1. 检查调用 join() 的线程是否是 ForkJoinThread 线程。如果不是（例如 main 线程），则阻塞当前线程，等待任务完成。如果是，则不阻塞。
			// 2. 查看任务的完成状态，如果已经完成，直接返回结果。
			// 3. 如果任务尚未完成，但处于自己的工作队列内，则完成它。
			// 4. 如果任务已经被其他的工作线程偷走，则窃取这个小偷的工作队列内的任务（以 FIFO 方式），执行，以期帮助它早日完成欲 join 的任务。
			// 5. 如果偷走任务的小偷也已经把自己的任务全部做完，正在等待需要 join 的任务时，则找到小偷的小偷，帮助它完成它的任务。
			// 6. 递归地执行第5步。
			return left.join() + right.join();
		}
	}
}