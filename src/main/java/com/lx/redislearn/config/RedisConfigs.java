package com.lx.redislearn.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Title RedisConfigs
 * Package com.lx.springbootlearn.config
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
 * @date 2018-09-19 22:12
 * description
 * modifie
 * updateDate
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisConfigs extends CachingConfigurerSupport {

    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.password}")
    private String password;
    @Value("${redis.pool.max-total}")
    private int maxTotal;
    @Value("${redis.pool.max-idle}")
    private int maxIdle;
    @Value("${redis.pool.min-idle}")
    private int minIdle;
//    @Value("${redis.keytimeout}")
//    private long keytimeout;
    @Value("${redis.timeout}")
    private int timeout;


    /**
     * 生成key的策略
     *
     * @return
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    /**
     * 管理缓存
     *
     * @param redisTemplate
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        //设置缓存过期时间
//        rcm.setDefaultExpiration(60);//秒
        //设置value的过期时间
        Map<String,Long> map=new HashMap<>(16);
        map.put("test",60L);
        rcm.setExpires(map);
        return rcm;
    }


    /**
     * JedisPoolConfig 连接池
     * @return
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxTotal(maxTotal);
        //每次释放连接的最大数目,默认3
        jedisPoolConfig.setNumTestsPerEvictionRun(10);

//        //是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
//        jedisPoolConfig.setTestOnBorrow(true);
//
//        jedisPoolConfig.setTestOnReturn(true);
//        //在空闲时检查有效性, 默认false
//        jedisPoolConfig.setTestWhileIdle(true);

//        //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
//        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(60000);

        return jedisPoolConfig;
    }

    /**
     *
     * database 0 的连接工厂
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig());
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.setPassword(password);
        //编号为0的数据库
        jedisConnectionFactory.setDatabase(0);
        return jedisConnectionFactory;
    }

    /**
     *
     * database 1 的连接工厂
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory1() {

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig());
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.setPassword(password);
        //编号为1的数据库
        jedisConnectionFactory.setDatabase(1);
        return jedisConnectionFactory;
    }

    /**
     *此处注意定义的bean要注入到相应的service中使用 只有对应service才能处理对应的数据库
     */
    @Bean(name = "redisTemplate0")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplateObject = new RedisTemplate<>();
        redisTemplateObject.setConnectionFactory(redisConnectionFactory());
        setSerializer(redisTemplateObject);
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplateObject.setKeySerializer(stringSerializer);
        redisTemplateObject.setHashKeySerializer(stringSerializer );
        redisTemplateObject.afterPropertiesSet();
        return redisTemplateObject;
    }

    @Bean(name = "stringRedisTemplate0")
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        return stringRedisTemplate;
    }

    @Bean(name = "redisTemplate1")
    public RedisTemplate<String, Object> redisTemplate1() {
        RedisTemplate<String, Object> redisTemplateObject = new RedisTemplate<>();
        redisTemplateObject.setConnectionFactory(redisConnectionFactory1());
        setSerializer(redisTemplateObject);
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplateObject.setKeySerializer(stringSerializer);
//        redisTemplateObject.setHashKeySerializer(stringSerializer );
        redisTemplateObject.afterPropertiesSet();
        return redisTemplateObject;
    }

    @Bean(name = "stringRedisTemplate1")
    public StringRedisTemplate stringRedisTemplate1() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory1());
        return stringRedisTemplate;
    }


    private void  setSerializer(RedisTemplate template) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
//        template.setKeySerializer(template.getStringSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);
//        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        //在使用String的数据结构的时候使用这个来更改序列化方式
        /*
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer );
         template.setValueSerializer(stringSerializer );
         template.setHashKeySerializer(stringSerializer );
        template.setHashValueSerializer(stringSerializer );*/
    }

    @Bean(name = "redisPool0")
    public JedisPool redisPoolFactory0() {

        if(log.isInfoEnabled()){
            log.info("JedisPool注入成功！！");
            log.info("redis地址：%s:%s",host,port);
        }
        JedisPool jedisPool = new JedisPool(jedisPoolConfig(), host, port, timeout,password,0);
        return jedisPool;
    }

    @Bean(name = "redisPool1")
    public JedisPool redisPoolFactory1() {

        if(log.isInfoEnabled()){
            log.info("JedisPool注入成功！！");
            log.info("redis地址：%s:%s",host,port);
        }
        JedisPool jedisPool = new JedisPool(jedisPoolConfig(), host, port, timeout,password,1);
        return jedisPool;
    }
}