### application.yml VS application.properties
#### application.yml
- 설정이름 : 값
```
logging:
  level:
    org.springframework: debug
```
#### application.properties
- 설정이름 = 값
```
logging.level.org.springframework = debug
```

--> 위의 설정정보는 org.springframework 패키지를 debug레벨로 로깅하겠다는 뜻이다

### DispatcherServlet

- 서블릿 컨테이너에서 http프로토콜을 통해 들어오는 모든 요청값을 처리하기 위해
프리젠테이션 계층의 제일 앞에 놓여지며 FrontController로서 사용된다. 즉 게이트웨이 역할이다
- 요청에 맞는 Handler로 요청을 전달
- Handler의 실행 결과를 Http Response 형태로 만들어서 반환

### RestController
- Spring4부터 @RestController 지원
- @Controller + @ResponseBody
- View를 갖지 않는 REST Data(JSON/XML)를 반환

### 예외처리
```
@GetMapping("/users/{id}")
    public User retrieveUser(@PathVariable int id) {
        User user = service.findOne(id);
        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }
        return user;
    }
```
위와 같은 RestController API에서 예외가 발생했다고 하자. 어떻게 처리할 것인가?

```
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private Date timestamp;
    private String message;
    private String details;
}
```
```
@RestController
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }
}
```
- 예외를 처리하는 API들은 해당 예외가 발생하는 컨트롤러에 있어야 한다. 즉 handleUserNotFoundExceptions 메서드는 UserController 클래스에
있어야만 작동한다. 하지만 @ControllerAdvice을 사용하면, 다른 모든 컨트롤러에서 예외가 발생할 시 @ControllerAdvice 어노테이션이 붙은 컨트롤러로
넘어와서 예외를 처리할 수 있다
- UserNotFoundException 예외가 발생했다면 handleUserNotFoundExceptions 메서드가 처리한다. HttpStatus.NOT_FOUND를 반환하므로
Postman으로 테스트 시 Status 404를 확인할 수 있다
- 이외 모든 예외는 Exception에 해당하므로 handleAllExceptions 메서드가 처리하여 Status 500을 반환한다
