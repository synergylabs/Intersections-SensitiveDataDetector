package capstone.sdd.core;

import capstone.sdd.gui.GuiListener;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lieyongzou on 7/7/16.
 */
public class CompletionExecutor extends ThreadPoolExecutor {

    private final AtomicInteger executing = new AtomicInteger(0);
    private GuiListener listener;

    // true if all the tasks are to scan the device
    // false if all the tasks are to match the content
    private boolean isScanning;

    public CompletionExecutor(GuiListener listener) {
        super(Runtime.getRuntime().availableProcessors(), Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        this.listener = listener;
    }

    public CompletionExecutor(int coorPoolSize, int maxPoolSize, long keepAliveTime,
                                TimeUnit seconds, BlockingQueue<Runnable> queue) {
        super(coorPoolSize, maxPoolSize, keepAliveTime, seconds, queue);
    }


    public void setMode(boolean flag) {
        isScanning = flag;
    }

    @Override
    public void execute(Runnable command) {
        //intercepting beforeExecute is too late!
        //execute() is called in the parent thread before it terminates
        executing.incrementAndGet();
        super.execute(command);
    }


    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        int count = executing.decrementAndGet();
        if(count == 0) {
            if (isScanning) {
                listener.finishScanTasks();
            } else {
                listener.finishMatchTasks();
                this.shutdown();
            }
        }
    }

}
