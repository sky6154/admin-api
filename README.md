### DEVELOBEER 관리 운영툴 Back-end 코드 입니다.

application.yml은 git에서 제외되어 있으며 /src/main/resources/application.yml 에 넣어야 합니다.

```
spring:
  profiles:
    active: test
---
spring:
  profiles: test

  devtools:
    livereload:
      enabled: false

  session:
    timeout: 0s
    store-type: redis
    redis:
      flush-mode: on_save
      namespace: spring:session

  jpa:
    hibernate:
      ddl-auto: none
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect # use InnoDB
    show-sql: true

  datasource:
    blog:
         type: com.zaxxer.hikari.HikariDataSource
         jdbc-url: # DB URL #
         username: # ID #
         password: # PASSWORD #
         hikari:
           maximum-pool-size: 5
           max-lifetime: 30

    admin:
          type: com.zaxxer.hikari.HikariDataSource
          jdbc-url: # DB URL #
          username: # ID #
          password: # PASSWORD #
          hikari:
            maximum-pool-size: 5
            max-lifetime: 30

  redis:
    host: # HOST #
    port: # PORT #
    password: # PASSWORD #
    database: # DB NUM #


origin:
    hosts:
        - http://localhost:3000
        - http://localhost:3001

path:
  upload-root: # UPLOAD ROOT LOCATION #
  access-addr: # FILE URL #


key:
  private-key: # PRIVATE KEY #

  public-key: # PUBLIC KEY #

#logging:
#    level:
#      org:
#        springframework:
#          web: trace
#          security: trace

#logging:
#    level:
#      root: trace
#      org:
#        springframework:
#          web: trace
#        apache:
#          tomcat: info


---
spring:
  profiles: live

  session:
      timeout: 0s
      store-type: redis
      redis:
        flush-mode: on_save
        namespace: spring:session

  jpa:
    hibernate:
      ddl-auto: none
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect # use InnoDB
#   show-sql: true

  datasource:
    blog:
         type: com.zaxxer.hikari.HikariDataSource
         jdbc-url: # JDBC URL #
         username: # ID #
         password: # PASSWORD #
         hikari:
           maximum-pool-size: 5
           max-lifetime: 30

    admin:
          type: com.zaxxer.hikari.HikariDataSource
          jdbc-url: # JDBC URL #
          username: # ID #
          password: # PASSWORD #
          hikari:
            maximum-pool-size: 5
            max-lifetime: 30

  redis:
      host: # HOST #
      port: # PORT #
      database: # DB NUM #

origin:
    hosts:
        - https://develobeer.blog


path:
  upload-root: # UPLOAD ROOT LOCATION #
  access-addr: # FILE URL #


key:
  private-key: # PRIVATE KEY #

  public-key: # PUBLIC KEY #
              
```
