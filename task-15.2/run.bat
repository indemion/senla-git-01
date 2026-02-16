@echo off
chcp 65001 > nul
java -Dfile.encoding=UTF-8 -jar target/carservice-1.3-with-dependencies.jar
pause