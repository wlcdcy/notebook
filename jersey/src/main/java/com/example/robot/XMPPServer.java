package com.example.robot;

import java.util.HashMap;
import java.util.Map;

public class XMPPServer {
    private String host = "192.168.1.101";
    private static String domain = "bar";
    private int port = 5222;

    public static final String TURINGURL = "http://www.tuling123.com/openapi/api";
    private static Map<String, Robot> robots = new HashMap<>();
    static {
        Robot xiaoai = new Robot();
        xiaoai.setNicknme("小艾");
        xiaoai.setPassword("111111");
        xiaoai.setRobotname("1000");
        xiaoai.setUserjid(String.format("%s@%s", xiaoai.getRobotname(), domain));
        xiaoai.setTuringId("xiaoai");
        xiaoai.setTuringKey("f3d7228474114e99aecc6c05fd03c176");
        robots.put(xiaoai.getRobotname(), xiaoai);

        Robot turing = new Robot();
        xiaoai.setNicknme("图灵");
        xiaoai.setPassword("111111");
        xiaoai.setRobotname("999");
        xiaoai.setUserjid(String.format("%s@%s", xiaoai.getRobotname(), domain));
        xiaoai.setTuringId("tuling");
        xiaoai.setTuringKey("c232f980ef2b261b6934506d67e8f0a8");
        robots.put(turing.getRobotname(), turing);
    }

    public String getHost() {
        return host;
    }

    public String getDomain() {
        return domain;
    }

    public int getPort() {
        return port;
    }

    public Map<String, Robot> getRobots() {
        return robots;
    }

}
