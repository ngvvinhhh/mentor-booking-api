# Bước 1: Build ứng dụng bằng Maven
FROM maven:3-openjdk-17 AS build
WORKDIR /app

# Sao chép toàn bộ mã nguồn vào container
COPY . .

# Build ứng dụng, tạo file JAR
RUN mvn clean package -DskipTests

# Bước 2: Tạo image chứa file JAR đã build
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Sao chép file JAR từ bước build trước đó
COPY --from=build /app/target/mentorbooking-0.0.1-SNAPSHOT.jar app.jar

# Expose cổng 8080
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
