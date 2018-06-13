package com.zxd.task.java8.stream;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/6/6.
 */
public class FirstTest {

    public static class Person {
        private int id;

        private String name;

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

//    public static void main(String[] args) {
//        List<String> testList = Lists.newArrayList("11","222","3333");
//
//        List<Person> persons = Lists.newArrayList(new Person(1,"tom"), new Person(2, "Bob"));
//
//        reduce(testList);
//
//        collect(testList);
//
//        toMap(persons);
//    }

    static class ModuleInfo{
        private Long id;

        private Long age;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getAge() {
            return age;
        }

        public void setAge(Long age) {
            this.age = age;
        }
    }

    public static void main(String[] args) {

        List<ModuleInfo> moduleInfoList = Lists.newArrayList();
        ModuleInfo moduleInfo = new ModuleInfo();
        ModuleInfo moduleInfo2 = new ModuleInfo();
        moduleInfo.setId(1L);
        moduleInfo2.setId(2L);
        moduleInfo.setAge(22L);
        moduleInfo2.setAge(22L);
        moduleInfoList.add(moduleInfo);
        moduleInfoList.add(moduleInfo2);
        moduleInfoList.add(moduleInfo2);

        List<Long> moduleIds = moduleInfoList.stream().map(ModuleInfo::getId).collect(Collectors.toList());
//        Map<String, ModuleInfo> map = moduleInfoList.stream().collect(Collectors.toMap(m -> m.getId().toString() + m
//                .getAge(), Function.identity()));

        Map<String, List<ModuleInfo>> map1 = moduleInfoList.stream().collect(Collectors.groupingBy(m -> m.getId() +
                "" + m.getAge()));
        System.out.println(moduleIds);
    }

    private static void  reduce(List<String> list){
        Optional<String> optional = list.stream().reduce(String::concat);
        System.out.println(optional.get());
    }

    private static void  collect(List<String> list){
        String str = list.stream().collect(Collectors.joining(","));
        System.out.println(str);
    }


    private static void toMap(List<Person> list){
        Map<Integer, Person> map = list.stream().collect(Collectors.toMap(Person::getId, Function.identity()));
        System.out.println(map.toString());
    }
}
