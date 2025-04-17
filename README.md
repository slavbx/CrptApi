## CrptApi
Метод для сохранения документа в системе Честный знак

Описание: 
Документ представлен классом *slavbx.Document*, который хранит в себе информацию о товарах (класс *Product*)  
Документ сериализуется в *json* библиотекой *Gson* для последующей передачи  
Отправка документа осуществляется методом *addProductToTrade(slavbx.Document document, String signature)*  
Сигнатура документа отправляется в заголовках метода *POST*  
Для отправки запроса и получения ответа подключены классы *HttpClient, HttpRequest, HttpResponse* из пакета *java.net.http*.  
Ограничивает отправку по времени с заданными параметрами вспомогательный класс *Limiter*  

Версия Java: 17  
Система сборки: Maven 4.0.0  
Сторонние библиотеки: Gson 2.11  

Зависимости для подключения бибилиотек:
```
<!-- https://mvnrepository.com/artifact/com.google.code.gson/gson -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.11.0</version>
</dependency>

```
