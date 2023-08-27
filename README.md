# ExpenseTracker
Web Application to track your expenses

### Run the Application:

* From your IDE:
> Locate the main class Application.class in `src/main/java/com/example/application` Right-click on the class and select "Run".

* Using Maven:
```
cd /path/to/your/project
mvn spring-boot:run
```


Once the application is running, you can access it in a web browser or via API endpoints. By default, Spring Boot applications run on http://localhost:8080.



#### The following table shows how to load various resources and where the resource files should be placed in a project.

File type |	Import |	File location
--- | --- | ---
CSS files | `@CssImport("./my-styles/styles.css")` | `/frontend/my-styles/styles.css`
JavaScript, TypeScript | `@JsModule("./my-script.js")` | `/frontend/my-script.js`
Static files, such as images and icons | `new Image("img/flower.jpg", "A flower")` | `/src/main/resources/META-INF/resources/img/flower.jpg`



