package capstone.sdd.core;

import capstone.sdd.gui.GuiListener;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread pool executor which can set the working mode, and call the listener
 * when all the tasks have been executed and finished
 * Created by lieyongzou on 7/7/16.
 */
public class CompletionExecutor extends ThreadPoolExecutor {

    public enum Mode {
        SCAN, MATCH;
    }

    // The number of tasks in the pool
    private final AtomicInteger executing = new AtomicInteger(0);
    
    private GuiListener listener;
    private Mode mode;

    public CompletionExecutor(GuiListener listener) {
        super(Runtime.getRuntime().availableProcessors(), Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

        if (listener != null) {
            this.listener = listener;
        } else {
            throw new NullPointerException();
        }

    }


    public void setMode(Mode mode) {
        this.mode = mode;
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
            if (mode == Mode.SCAN) {
                listener.finishScanTasks();
            } else if (mode == Mode.MATCH) {
                listener.finishMatchTasks();
            }
        }
    }

}
