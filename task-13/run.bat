@echo off
chcp 65001 > nul
java -Dfile.encoding=UTF-8 -jar app/target/app-1.1-jar-with-dependencies.jar
pause