#! /usr/bin/bash

## Устанавливает текущий каталог в расположение shell-скрипта
cd `dirname $0`

## Удаляет рекурсивно каталог out
if [ -d "./out" ]; then rm -r ./out; fi

## Создает путь ./out/classes
mkdir ./out
mkdir ./out/classes

## Компилирует исходники в каталог classes
javac -d ./out/classes -sourcepath ./java ./java/ru/subbotin/FilteringUtilMainClass.java

cd ./out/classes

## Создает JAR файл
jar cmf ./../../manifest.txt ./../application.jar ./*

### Выполняет программу с аргументами, переданными при запуске скрипта
#java -jar ./../application.jar "$@"

cd ./../..

## Запускает приложение с установленными параметрами
java -jar ./out/application.jar -s -o ./out ./input/input.txt