# AWS Disto for OpenTelemetry (ADOT) Java sample

## Prerequisite
1. Mac或者Linux主机配置有AWS CLI V2与docker
2. 安装有Java SDK 8+与Maven

## Demo Steps

Clone souce code 

```bash
git clone https://github.com/zjutdp/adot.git
```

下载ADOT Collector

```bash
wget https://github.com/aws-observability/aws-otel-java-instrumentation/releases/download/v1.7.1/aws-opentelemetry-agent.jar
```

启动ADOT Collector

```bash
docker run --rm -p 4317:4317 -p 55680:55680 -p 8889:8888 -e AWS_REGION=us-west-2 \
 -e AWS_PROFILE=default  -v ~/.aws:/root/.aws \
 --name awscollector public.ecr.aws/aws-observability/aws-otel-collector:latest
```

编译示例代码

```bash
mvn clean compile assembly:single
```

运行示例程序，将trace通过collector上传至us-west-2区域的x-ray服务

```bash
OTEL_RESOURCE_ATTRIBUTES=service.name=MyApp,service.namespace=MyTeam \
  java -javaagent:./aws-opentelemetry-agent.jar \
       -jar target/myapp-1.0-SNAPSHOT-jar-with-dependencies.jar
       
```

### Reference
1. https://aws.amazon.com/blogs/aws/new-for-aws-distro-for-opentelemetry-tracing-support-is-now-generally-available/
