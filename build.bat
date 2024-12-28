@ECHO OFF

:: Устанавливает текущий каталог в расположение batch-скрипта
cd %~dp0

:: Рекурсивно удаляет содержимое каталога out
del /s /f /q .\out\*.*
for /f %%f in ('dir /ad /b .\out') do rd /s /q .\out\%%f

:: Создает каталог classes
mkdir .\out\classes

:: Компилирует исходники в каталог classes
javac -d .\out\classes -sourcepath .\java .\java\ru\subbotin\Main.java

cd .\out\classes

:: Создает JAR файл. Необходимо, чтобы каталог с jar.exe был в PATH
jar cmf .\..\..\manifest.txt .\..\application.jar .\*

cd .\..\..

:: Запускает приложение с установленными параметрами
java -jar ./out/application.jar -s -o ./out ./input/input.txt

PAUSE