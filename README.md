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