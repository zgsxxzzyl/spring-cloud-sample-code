* /{application}/{profile}[/{label}]
* /{application}-{profile}.yml
* /{label}/{application}-{profile}.yml
* /{application}-{profile}.properties
* /{label}/{application}-{profile}.properties


* http://localhost:8888/foo/dev


spring.cloud.config.server.git.uri：配置git仓库地址
spring.cloud.config.server.git.searchPaths：配置仓库路径
spring.cloud.config.label：配置仓库的分支
spring.cloud.config.server.git.username：访问git仓库的用户名
spring.cloud.config.server.git.password：访问git仓库的用户密码