package com.zxd.task.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/8/14.
 */
public class Smaple {

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 4 , exec);

        final EventHandler<ValueEvent> handler1 = new EventHandler<ValueEvent>() {
            // event will eventually be recycled by the Disruptor after it wraps
            public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
                System.out.println("handler1:  Sequence: " + sequence + "   ValueEvent: " + event.getValue());
            }
        };
//      final EventHandler<ValueEvent> handler2 = new EventHandler<ValueEvent>() {
//          // event will eventually be recycled by the Disruptor after it wraps
//          public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
//              System.out.println("handler2:  Sequence: " + sequence + "   ValueEvent: " + event.getValue());
//          }
//      };

//      disruptor.handleEventsWith(handler1, handler2);
        disruptor.handleEventsWith(handler1);
        RingBuffer<ValueEvent> ringBuffer = disruptor.start();

        int bufferSize = ringBuffer.getBufferSize();
        System.out.println("bufferSize =  " + bufferSize);

        for (long i = 0; i < 1000; i++) {
            long seq = ringBuffer.next();
            try {
                String uuid = String.valueOf(i);
                ValueEvent valueEvent = ringBuffer.get(seq);
                valueEvent.setValue(uuid);
            } finally {
                ringBuffer.publish(seq);
            }
        }

        disruptor.shutdown();
        exec.shutdown();
    }
}
