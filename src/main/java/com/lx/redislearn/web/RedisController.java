package com.lx.redislearn.web;

import com.lx.redislearn.service.RedisService;
import com.lx.redislearn.service.RedisService1;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Title RedisController
 * Package com.lx.redislearn.web
 *
 * @author lx
 * @version v1
 * **
 * ----------Dragon be here!----------/
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑
 * 　　　　┃　　　┃代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━神兽出没━━━━━━
 * @date 2018-09-19 23:35
 * description
 * modifie
 * updateDate
 */
@Slf4j
@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisService1 redisService1;
    @GetMapping(value = "/test")
    public String test(String input){
        boolean test = redisService.set(input, "test0", 60L);
        boolean test1 = redisService1.set(input, "test", 120L);
        log.info("结果为:{}",String.valueOf(test && test1) );
        return String.valueOf(test && test1);
    }
}
