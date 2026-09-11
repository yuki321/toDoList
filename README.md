## application.properties 

spring.application.name=todolist
spring.sql.init.mode=always
spring.mvc.hiddenmethod.filter.enabled=true

### 接続URL（MySQLのデフォルトポートは3306。データベース名が「todolist」の場合）
spring.datasource.url=jdbc:mysql://localhost:3306/todolist?serverTimezone=Asia/Tokyo&useSSL=false&zeroDateTimeBehavior=CONVERT_TO_NULL

### ユーザー名とパスワード（パスワードは未設定）
spring.datasource.username=xxxx（仮）
spring.datasource.password=

### ドライバークラス（MySQL 8.0以降用）
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

## メール送信
spring.mail.host=smtp.gmail.com
spring.mail.port=587

## 送信元メールアドレス
spring.mail.username=xxxx@xxxx.com 

## 送信元アドレスのアカウントで2段階認証を設定する（16桁）
spring.mail.password=xxxx xxxx xxxx xxxx 

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

## 送信元メールアドレス
app.mail.from=xxxx@xxxx.com

## タスク実行
### 毎日6時間ごとにタスク実施
cron.task=0 0 */6 * * *

### テスト用
#cron.task=*/1 * * * * *

### ログ出力
#### Webリクエストの詳細（HTTPメソッド、URL、パラメータなど）を出力
logging.level.org.springframework.web=DEBUG

#### データベース操作（SQL文）のログを出力（JPA/Hibernate使用時）
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

#### 現在書き込み中のログファイル名
logging.file.name=logs/app.log
logging.file.path=logs

#### ローテーション（退避）時のファイル名パターン（日時を付与）
#### 例: logs/app-2026-09-12.0.log などのように保存されます
#### 日付が変わった時のログファイル退避パターン (%d{yyyy-MM-dd} で日付ごとに分割)
logging.logback.rollingpolicy.file-name-pattern=logs/app-%d{yyyy-MM-dd}.%i.log

#### 1ファイルあたりの最大サイズ（到達すると自動で新しいファイルが作成される）
logging.logback.rollingpolicy.max-file-size=10MB

#### 保存しておく過去ログファイルの最大世代数（日・世代）
logging.logback.rollingpolicy.max-history=30

#### ログアーカイブの総容量上限
logging.logback.rollingpolicy.total-size-cap=1GB






