package com.zxd.task.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/8/14.
 */
public class ValueEvent {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public final static EventFactory<ValueEvent> EVENT_FACTORY = new EventFactory<ValueEvent>() {
        public ValueEvent newInstance() {
            return new ValueEvent();
        }
    };

}
