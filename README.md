

该项目是一个基于Spring Boot的全文搜索应用，具备基本的用户交互和HTML页面展示功能。

## 安装步骤

1. 确保已安装Java开发环境（JDK 8或更高版本）和Maven。
2. 克隆项目到本地：
   ```
   git clone https://gitee.com/mimsq/full-text-searching
   ```
3. 进入项目目录并使用Maven构建：
   ```
   cd full-text-searching
   mvn clean install
   ```
4. 启动项目：
   ```
   mvn spring-boot:run
   ```
5. 项目启动后，默认访问端口为8080。

## 使用方法

- 访问`/hello`接口，可以获取问候信息，参数name为可选。
- 访问`/user`接口，可以获取一个用户对象。
- 访问`/save_user`接口，可以提交用户信息。
- 访问`/html`接口，可以展示HTML页面。
- 通过路径变量访问`/user/{userId}/roles/{roleId}`，可以获取指定用户和角色的信息。
- 访问`/javabeat/{regexp1:[a-z-]+}`，可以使用正则表达式路径变量。

## 项目结构

- `FullTextSearchingApplication.java`: Spring Boot应用的主类。
- `BasicController.java`: 包含基本请求处理的控制器。
- `PathVariableController.java`: 包含路径变量请求处理的控制器。
- `User.java`: 用户实体类。
- `application.yml`: 应用的主要配置文件。
- `index.html`: 静态资源页面。

## 测试

- `FullTextSearchingApplicationTests.java`: 提供了Spring Boot应用上下文加载测试。

## 贡献者指南

欢迎贡献代码。提交代码前，请确保代码符合编码规范，并包含必要的单元测试。

## 许可证

本项目采用MIT许可证。详细信息请查看仓库中的许可证文件。