FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 9081
ENTRYPOINT ["java","-cp","app:app/lib/*","com.freeman.FreemanApplication"]

# 本地构建 在freeman-parent执行 clean package
# maven打包时, 解压出来的文件(需要传到服务器上的那一部分)目录结构
#├─ Dockerfile   ##上传服务器
#├─ target
#└── dependency ##上传服务器(包括所有子目录及文件)
#    ├── BOOT-INF
#    │   ├── classes
#    │   │   └── 略
#    │   └── lib
#    │       └── 略
#    ├── META-INF
#    │   └── 略
#    └── org
#        └── 略

# linux服务器上使用docker部署项目
#1. 在linux服务器上创建文件夹, 例如： freeman
#2. 将maven打包后时解压出来(target目录下)的 dependency 目录和Dockerfile文件 传到服务器的freeman文件夹(以后修改代码,只需要替换部分内容,然后重新构建镜像)
#3. docker build镜像
#   进入freeman文件夹
#   docker build -t freeman:test .
#   注意语句结束后的“.”,不可省略
#4. 创建容器,并启动(如果存在,先删除), 预览效果
#   docker run -it --rm -p 9081:9081 --name freeman-app -v /etc/localtime:/etc/localtime freeman:test
#5. 创建容器,在后台启动(预览能正常启动后)
#   docker run -d -p 9081:9081 --name freeman-app -v /etc/localtime:/etc/localtime freeman:test
#
#到此部署成功。

