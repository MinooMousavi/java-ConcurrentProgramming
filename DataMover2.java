import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class DataMover2 {

    static AtomicInteger arrivalCount = new AtomicInteger();
    static AtomicInteger totalSent = new AtomicInteger();
    static AtomicInteger totalArrived = new AtomicInteger();
    static ExecutorService pool;
    static List<BlockingQueue<Integer>> queues;
    static List<Future<DataMover2Result>> moverResults;
    static List<Integer> discards = new ArrayList<>();

    static class DataMover2Result {
        public int count;
        public int data;
        public int forwarded;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int[] waitTimes;
        if(args.length > 0)
        {
            waitTimes = new int[args.length];
            for(int i=0; i < args.length; i++){
                waitTimes[i] = Integer.parseInt(args[i]);
            }
        }else{
            waitTimes = new int[]{123, 11, 256, 404};
        }
        int n = waitTimes.length;
        pool = Executors.newFixedThreadPool(100);
        queues = new ArrayList<>();
        moverResults = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            queues.add(new LinkedBlockingQueue<>());
        }

        for (int i = 0; i < n; i++) {
            int sleepTime;
            if (args.length > 0) {
                sleepTime = Integer.parseInt(args[i]);
            }else 
            {
                sleepTime = waitTimes[i];
            }
            int threadIndex = i;
            Callable<DataMover2Result> task = () -> {
                DataMover2Result result = new DataMover2Result();
                Random random = new Random();
                while (arrivalCount.get() < 5 * n) {
                    int x = random.nextInt(10001);
                    totalSent.addAndGet(x);
                    queues.get(threadIndex).put(x);
                    System.out.println("total " + arrivalCount.get() + "/" + (5 * n) + " | #" + threadIndex + " sends " + x);

                    Integer received = queues.get((threadIndex + 1) % n).poll(random.nextInt(701) + 300, TimeUnit.MILLISECONDS);
                    if (received == null) {
                        System.out.println("total " + arrivalCount.get() + "/" + (5 * n) + " | #" + threadIndex + " got nothing...");
                    } else if (received % n == threadIndex) {
                        arrivalCount.incrementAndGet();
                        result.count++;
                        result.data += received;
                        System.out.println("total " + arrivalCount.get() + "/" + (5 * n) + " | #" + threadIndex + " got " + received);
                    } else {
                        queues.get((threadIndex + 1) % n).put(received - 1);
                        result.forwarded++;
                        System.out.println("total " + arrivalCount.get() + "/" + (5 * n) + " | #" + threadIndex + " forwards " + (received - 1) + " [" + ((received - 1) % n) + "]");
                    }
                    Thread.sleep(sleepTime);
                }
                return result;
            };
            moverResults.add(pool.submit(task));
        }

        try {
            pool.shutdown();
         if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
            pool.shutdownNow();
         }

        }catch (InterruptedException e){
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        for (Future<DataMover2Result> future : moverResults) {
            try {
            DataMover2Result result = future.get();
            totalArrived.addAndGet(result.data + result.forwarded);
            } catch(InterruptedException | ExecutionException e){
            Thread.currentThread().interrupt();
            }
        }
        

        for (BlockingQueue<Integer> queue : queues) {
            while (!queue.isEmpty()) {
                discards.add(queue.poll());
            }
        }

        int discardedSum = discards.stream().mapToInt(Integer::intValue).sum();
        System.out.println("discarded " + discards + " = " + discardedSum);
        int total = totalArrived.get() + discardedSum;
        if (totalSent.get() == total) {
            System.out.println("sent " + totalSent.get() + " === got " + total + " = " + totalArrived.get() + " + discarded " + discardedSum);
        } else {
            System.out.println("WRONG sent " + totalSent.get() + " !== got " + total + " = " + totalArrived.get() + " + discarded " + discardedSum);
        }
    }
}
