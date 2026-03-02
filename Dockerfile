# Stage 1: Build the application using Maven (Multi-stage build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Tối ưu hóa Cache của Docker bằng cách copy và tải dependency trước
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source code và tiến hành build (Bỏ qua chạy Test để build nhanh hơn)
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the minimal runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Kỹ thuật bảo mật: Không chạy app bằng quyền root trong container
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Chỉ copy file .jar đã build được từ Stage 1 sang Stage 2
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080 cho ứng dụng
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
