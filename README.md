---
typora-copy-images-to: ./img
typora-root-url: ./
---

> 尚硅谷 SpringCloud 速通课程代码
> 
> 视频链接: [Spring Cloud 快速通关](https://www.bilibili.com/video/BV1UJc2ezEFU/)

# 1. Nacos

## 1.1 简介与下载

Nacos 是 Dynamic Naming and Configuration Service 的首字母简称，一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。

官网：[Nacos官网](https://nacos.io/)

安装：

- 下载最新的 Nacos 安装包，本文使用 Nacos-2.5.1
- 启动命令：`startup.cmd -m standalone`

下载好最近的安装包后，解压到非中文目录，进入 `bin` 目录，执行启动命令。

## 1.2 服务注册

1. 引入 `spring-boot-starter-web`、`spring-cloud-starter-alibaba-nacos-discovery` 依赖

2. 编写主启动类，编写配置文件

3. 配置 Naocs 地址

   ```yaml
   spring:
     cloud:
       nacos:
         # 配置 Nacos 地址
         server-addr: 127.0.0.1:8848
   ```

4. 启动微服务

5. 查看注册中心效果，访问 `http://localhost:8848/nacos/`

6. 测试集群模式启动：单机情况下通过改变端口号模拟微服务集群，例如添加 Program arguments 信息为 `--server.port=8001`

## 1.3 服务发现

1. 开启服务发现，在主启动类上添加 `@EnableDiscoveryClient` 注解
2. 测试两款 API 的服务发现功能：`DiscoveryClient` 和 `NacosServiceDiscovery`。前者为 Spring 提供的服务发现标准接口，后者由 Nacos 提供。

## 1.4 远程调用

远程调用基本流程：

![远程调用基本流程](/img/远程调用基本流程.svg)

## 1.5 负载均衡

> 使用 `LoadBalancerClient` 实现

注入 `LoadBalancerClient`，调用其 `choose()` 方法，传入服务名，实现负载均衡。

> 使用 `@LoadBalanced` 注解实现

在配置类中向 Spring 容器添加 `RestTemplate` 的 Bean，在 Bean 方法上添加 `@LoadBalanced` 注解，使用 `RestTemplate` 进行远程调用时，修改传入的 URL 为服务名，比如：

```java
private Product getProductFromRemoteWithLoadBalancerAnnotation(Long productId) {
    // 给远程发送请求：service-product 会被动态替换
    String url = "http://service-product/product/" + productId;
    log.info("远程请求: {}", url);
    // 给远程发送请求
    return restTemplate.getForObject(url, Product.class);
}
```

此时底层会将服务名替换为负载均衡后的目标 URL。

> 经典面试题：如果注册中心宕机，远程调用是否可以成功？

![远程调用步骤](/img/远程调用步骤.svg)

- 如果从未调用过，此时注册中心宕机，调用会立即失败
- 如果调用过：
  - 此时注册中心宕机，会因为存在缓存的服务信息，调用会成功
  - 如果注册中心和对方服务都宕机，因为会缓存名单，调用会阻塞后失败（Connection Refused）

## 1.6 配置中心

配置中心的动态刷新步骤：

- `@Value("${xx}")` 获取配置 + `@RefreshScope` 实现动态刷新
- `@ConfigurationProperties` 无感自动刷新
- `NacosConfigManager` 监听配置变化

如果存在多个相同的配置信息，那么：

![配置信息优先级](/img/配置信息优先级.svg)

## 1.7 数据隔离

一个项目通常部署在多套环境上，比如 dev、test、prod。

项目中每个微服务的配置信息在每套环境上的值可能不一样，要求项目可以通过切换环境，加载本环境的配置。

如果要完成以上需求，其中的难点是如何：

- 区分多套环境
- 区分多种微服务
- 区分多种配置
- 按需加载配置

![Nacos数据隔离解决方案](/img/Nacos数据隔离解决方案.svg)

Nacos 的解决方案：

- 用名称空间区分多套环境
- 用 Group 区分多种微服务
- 用 Data-id 区分多种配置
- 使用 SpringBoot 激活对应环境的配置

# 2. OpenFeign

## 2.1 简介与使用

OpenFeign，是一种 Declarative REST Client，即声明式 Rest 客户端，与之对应的是编程式 Rest 客户端，比如 RestTemplate。

OpenFeign 由注解驱动：

- 指定远程地址：`@FeignClien`
- 指定请求方式：`@GetMapping`、`@PostMapping`、`@DeleteMapping`...
- 指定携带数据：`@RequestHeader`、`@RequestParam`、`@RequestBody`...
- 指定返回结果：响应模式

其中的 `@GetMapping` 等注解可以沿用 Spring MVC：

- 当它们标记在 Controller 上时，用于接收请求
- 当他们标记在 FeignClien 上时，用于发送请求

使用时引入以下依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

在主启动类上使用以下注解：

```java
@EnableFeignClients
```

![OpenFeign的远程调用](/img/OpenFeign的远程调用.svg)

- 远程调用注册中心中的服务参考：`ProductFeignClient`
- 远程调用指定 URL 参考：`MockUrlFeignClient`

## 2.2 小技巧

如何编写好 OpenFeign 声明式的远程调用接口：

- 针对业务 API：直接复制对方的 Controller 签名即可；
- 第三方 API：根据接口文档确定请求如何发

## 2.3 一道面试题

客户端负载均衡与服务端负载均衡的区别：

![客户端负载均衡与服务端负载均衡](/img/客户端负载均衡与服务端负载均衡.svg)

## 2.4 进阶用法

> 日志

在配置文件中指定 feign 接口所在包的日志级别：

```yaml
logging:
  level:
    # 指定 feign 接口所在的包的日志级别为 debug 级别
    indi.mofan.order.feign: debug
```

向 Spring 容器中注册 `feign.Logger.Level` 对象：

```java
@Bean
public Logger.Level feignlogLevel() {
    // 指定 OpenFeign 发请求时，日志级别为 FULL
    return Logger.Level.FULL;
}
```

> 超时控制

连接超时（connectTimeout），默认 10 秒。

读取超时（readTimeout），默认 60 秒。

如果需要修改默认超时时间，在配置文件中进行如下配置：

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          # 默认配置
          default:
            logger-level: full
            connect-timeout: 1000
            read-timeout: 2000
          # 具体 feign 客户端的超时配置
          service-product:
            logger-level: full
            # 连接超时，3000 毫秒
            connect-timeout: 3000
            # 读取超时，5000 毫秒
            read-timeout: 5000
```

> 重试机制

远程调用超时失败后，还可以进行多次尝试，如果某次成功则返回 ok，如果多次尝试后依然失败则结束调用，返回错误。

OpenFeign 底层默认使用 `NEVER_RETRY`，即从不重试策略。

向 Spring 容器中添加 `Retryer` 类型的 Bean：

```java
@Bean
public Retryer retryer() {
    return new Retryer.Default();
}
```

这里使用 OpenFeign 的默认实现 `Retryer.Default`，在这种默认实现下：

```java
public Default() {
    this(100L, TimeUnit.SECONDS.toMillis(1L), 5);
}
```

OpenFeign 的重试规则是：

- 重试间隔 100ms
- 最大重试间隔 1s。新一次重试间隔是上一次重试间隔的 1.5 倍，但不能超过最大重试间隔。
- 最多重试 5 次

> 拦截器

![OpenFeign的拦截器](/img/OpenFeign的拦截器.svg)

以请求拦截器为例，自定义的请求拦截器需要实现 `RequestInterceptor` 接口，并重写 `apply()` 方法：

```java
package indi.mofan.order.interceptor;

public class XTokenRequestInterceptor implements RequestInterceptor {
    /**
     * 请求拦截器
     *
     * @param template 封装本次请求的详细信息
     */
    @Override
    public void apply(RequestTemplate template) {
        System.out.println("XTokenRequestInterceptor ...");
        template.header("X-Token", UUID.randomUUID().toString());
    }
}
```

要想要该拦截器生效有两种方法：

1. 在配置文件中配置对应 Feign 客户端的请求拦截器，此时该拦截器只对指定的 Feign 客户端生效

   ```yaml
   spring:
     cloud:
       openfeign:
         client:
           config:
             # 具体 feign 客户端
             service-product:
               # 该请求拦截器仅对当前客户端有效
               request-interceptors:
                 - indi.mofan.order.interceptor.XTokenRequestInterceptor
   ```

2. 还可以直接将自定义的请求拦截器添加到 Spring 容器中，此时该拦截器对服务内的所有 Feign 客户端生效

   ```java
   @Component
   public class XTokenRequestInterceptor implements RequestInterceptor {
       // --snip--
   }
   ```

> Fallback

![OpenFeign的Fallback](/img/OpenFeign的Fallback.svg)

Fallback，即兜底返回。

注意，此功能需要整合 Sentinel 才能实现。

因此需要先导入 Sentinel 依赖：

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```

并在需要进行 Fallback 的服务的配置文件中开启配置：

```yaml
feign:
  sentinel:
    enabled: true
```

现在需要对 Feign 客户端 `ProductFeignClient` 配置 Fallback，那么需要先实现 `ProductFeignClient` 编写兜底返回逻辑，并将其交由 Spring 管理：

```java
@Component
public class ProductFeignClientFallback implements ProductFeignClient {
    @Override
    public Product getProductById(Long id) {
        System.out.println("Fallback...");
        Product product = new Product();
        product.setId(id);
        product.setPrice(new BigDecimal("0"));
        product.setProductName("未知商品");
        product.setNum(0);
        return product;
    }
}
```

之后回到对应的 Feign 客户端，配置 Fallback：

```java
@FeignClient(value = "service-product", fallback = ProductFeignClientFallback.class)
public interface ProductFeignClient {

    @GetMapping("/product/{id}")
    Product getProductById(@PathVariable("id") Long id);
}
```
