# 

spring.application.name=todolist
spring.sql.init.mode=always
spring.mvc.hiddenmethod.filter.enabled=true

# 接続URL（MySQLのデフォルトポートは3306。データベース名が「todolist」の場合）
spring.datasource.url=jdbc:mysql://localhost:3306/todolist?serverTimezone=Asia/Tokyo&useSSL=false&zeroDateTimeBehavior=CONVERT_TO_NULL

#spring.datasource.url=jdbc:mysql://localhost:3306/todolist

# ユーザー名とパスワード
# パスワードは未設定
spring.datasource.username=xxxx（仮）
spring.datasource.password=

# ドライバークラス（MySQL 8.0以降用）
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# メール送信
spring.mail.host=smtp.gmail.com
spring.mail.port=587

# 送信元メールアドレス
spring.mail.username=xxxx@xxxx.com 

# 16桁
# 送信元アドレスのアカウントで2段階認証を設定する
spring.mail.password=xxxx xxxx xxxx xxxx 

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# 送信元メールアドレス
app.mail.from=xxxx@xxxx.com









