package com.zxd.task.interceptor;

import org.springframework.stereotype.Component;

/**a
 * @author zxd
 * @since 16/12/22.
 */
@Component
public class AopUseCglibTest {
    public void play() {
        System.out.println(" cglib play computer game!");
    }

    public void testInner() {
        play();
    }
}
