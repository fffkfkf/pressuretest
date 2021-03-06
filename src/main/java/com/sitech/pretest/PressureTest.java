package com.sitech.pretest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.sitech.ijcf.boot.core.service.ArchitectureType;
import com.sitech.ijcf.boot.core.service.client.IServiceClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.lang.reflect.Type;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author gmq
 * @date 2019/12/23
 * 版权：Copyright 2000-2001 si-tech.com.cn  All Rights Reserved.
 */
@Component
public class PressureTest {
    Logger log = LogManager.getLogger(PressureTest.class);
    @Resource
    private FileValue fileValue;
    @Resource
    private IServiceClient serviceClient;
    public static int counti = 0;

    @PostConstruct
    public  void testOrderCreate() {

        //请求总数
        int clientTotal = fileValue.getTotal();
        //同时并发执行的线程数
        int threadTotal = fileValue.getCount();

        try {
            String serviceName = fileValue.getAddr();

            ExecutorService executorService = Executors.newCachedThreadPool();
            final Semaphore semaphore = new Semaphore(threadTotal);
            final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
            for (int i = 0; i < clientTotal ; i++) {
                executorService.execute(() -> {
                    try {
                        semaphore.acquire();
                        execTest();
                        semaphore.release();
                    } catch (Exception e) {
                        log.info("exception", e);
                    }
                    countDownLatch.countDown();
                });
            }

            countDownLatch.await();
            executorService.shutdown();
            log.info(counti+"总数========");
        }catch (Exception e){
            log.info("异常信息："+e.getMessage());
        }

    }


    private synchronized  void execTest() {
        JSONObject param = null;
        try {
            param = readJsonFromClassPath("inparam.json", JSONObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object root = param.get("ROOT");
        JSONObject jsonObject = JSONObject.parseObject(root.toString());
        Object body = jsonObject.get("BODY");
        JSONObject jsonObject1 = JSONObject.parseObject(body.toString());
        Object busi_info = jsonObject1.get("BUSI_INFO");
        JSONObject jsonObject2 = JSONObject.parseObject(busi_info.toString());
        Object custom_info = jsonObject2.get("CUSTOM_INFO");
        JSONObject jsonObject3 = JSONObject.parseObject(custom_info.toString());
        Object busi_model = jsonObject3.get("BUSI_MODEL");
        JSONObject jsonObject4 = JSONObject.parseObject(busi_model.toString());
        Object user_info = jsonObject4.get("USER_INFO");
        JSONObject jsonObject5 = JSONObject.parseObject(user_info.toString());
        Object phone_no = jsonObject5.get("PHONE_NO");
        jsonObject5.remove("PHONE_NO");
        //----
        jsonObject5.put("PHONE_NO",counti);
        jsonObject4.remove("USER_INFO");
        jsonObject4.put("USER_INFO",jsonObject5);
        jsonObject3.remove("BUSI_MODEL");
        jsonObject3.put("BUSI_MODEL",jsonObject4);
        jsonObject2.remove("CUSTOM_INFO");
        jsonObject2.put("CUSTOM_INFO",jsonObject3);
        jsonObject1.remove("BUSI_INFO");
        jsonObject1.put("BUSI_INFO",jsonObject2);
        jsonObject.remove("BODY");
        jsonObject.put("BODY",jsonObject1);
        param.remove("ROOT");
        param.put("ROOT",jsonObject);
        //log.info("param："+param);
        counti++;
        log.info(counti);
        long begin = System.currentTimeMillis();
        String rtnStr ="";
        //serviceClient.callService(serviceName,param.toString() , String.class, ArchitectureType.SPRINGCLOUD);
        long end = System.currentTimeMillis();
        long time=end-begin;
        //log.info("time"+counti+"---------------"+time+"毫秒---rtnStr---------------"+rtnStr);
    }
    public static <T> T readJsonFromClassPath(String path, Type type) throws IOException {

        ClassPathResource resource = new ClassPathResource(path);
        if (resource.exists()) {
            return JSON.parseObject(resource.getInputStream(), StandardCharsets.UTF_8, type,
                    // 自动关闭流
                    Feature.AutoCloseSource,
                    // 允许注释
                    Feature.AllowComment,
                    // 允许单引号
                    Feature.AllowSingleQuotes,
                    // 使用 Big decimal
                    Feature.UseBigDecimal);
        } else {
            throw new IOException();
        }
    }


}
