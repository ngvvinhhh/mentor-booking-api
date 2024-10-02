# Bước 1: Build ứng dụng bằng Maven
FROM maven:3.9.5-openjdk-17 AS build
WORKDIR /app

# Sao chép toàn bộ mã nguồn vào container
COPY . .

# Build ứng dụng, tạo file JAR
RUN mvn clean package -DskipTests

# Bước 2: Tạo image chứa file JAR đã build
FROM openjdk:17-jdk-alpine

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép file JAR từ bước build
COPY --from=build /app/target/mentorbooking-1.jar app.jar

# Expose cổng 8080
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
