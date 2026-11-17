# ビルド: Maven で実行可能 JAR を作る
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

# 実行: JRE のみのイメージで起動する
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app app

COPY --from=build /app/target/todolist-1.0.0.jar app.jar
RUN chown app:app app.jar

USER app

# Render は環境変数 PORT を渡す。未設定時は 8080
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar app.jar --server.port=${PORT}"]
