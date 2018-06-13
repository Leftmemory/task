package com.zxd.task.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceReportingEventHandler;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/8/14.
 */
public class DisruptorTest {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 8 , exec);

        final EventHandler<ValueEvent> handler1 = new SequenceReportingEventHandler<ValueEvent>() {
            @Override
            public void setSequenceCallback(Sequence sequenceCallback) {
                sequenceCallback.get();
                System.out.println("---");
            }

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
            long left = ringBuffer.remainingCapacity();
            long hi = seq + left;
            try {

                for(long j = seq;j <= hi; j++) {
                    String uuid = String.valueOf(i + j);
                    ValueEvent valueEvent = ringBuffer.get(j);
                    valueEvent.setValue(uuid);
                }
            } finally {
                ringBuffer.publish(seq, hi);
            }
        }

        disruptor.shutdown();
        exec.shutdown();
    }
}
