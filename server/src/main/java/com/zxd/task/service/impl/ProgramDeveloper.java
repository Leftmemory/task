package com.zxd.task.service.impl;

import com.zxd.task.service.Person;
import org.springframework.stereotype.Component;

/**
 * @author zxd
 * @since 16/5/19.
 */
@Component("programDeveloper")
public class ProgramDeveloper implements Person {
    @Override
    public void play() {
        System.out.println("play computer game!");
    }
}
