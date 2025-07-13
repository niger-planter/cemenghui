@echo off
setlocal

:: 请将以下路径替换为您下载的金仓数据库JDBC驱动JAR文件路径
:: Download Kingbase JDBC driver from official website first:
:: https://www.kingbase.com.cn/product/jdbc/
set KINGBASE_JAR_PATH=kingbase8-8.2.0.jar

:: Check if JAR file exists
if not exist "%KINGBASE_JAR_PATH%" (
    echo ERROR: JAR file %KINGBASE_JAR_PATH% not found
    echo Please download the driver and place it in the current directory,
    echo or modify the KINGBASE_JAR_PATH in this script
    pause
    exit /b 1
)

:: 安装JAR到本地Maven仓库
mvn install:install-file -Dfile="%KINGBASE_JAR_PATH%" -DgroupId=com.kingbase8 -DartifactId=kingbase8 -Dversion=8.2.0 -Dpackaging=jar

endlocal
pause