@echo off

: https://www.postgresql.org/docs/current/app-psql.html
docker exec -i senlatask11 psql -h localhost -p 5432 -U testpguser -d senlatask11 -w -a -v ON_ERROR_STOP=1 < ./db/db.sql

if errorlevel 1 (
    echo Error
    pause
) else (
    echo Success
    pause
)