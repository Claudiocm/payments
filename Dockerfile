# Usa a imagem base do OpenJDK 17 da Zulu
FROM azul/zulu-openjdk:17 AS Build

# Define o diretório de trabalho como /app
WORKDIR /payments

# Copia o JAR da aplicação da imagem de compilação para a imagem de execução
COPY  /target/*.jar payments.jar

# Expõe a porta 8080 para acesso à aplicação
EXPOSE 8080

# Comando para iniciar a aplicação quando o contêiner for iniciado
CMD ["java", "-jar", "payments.jar"]
