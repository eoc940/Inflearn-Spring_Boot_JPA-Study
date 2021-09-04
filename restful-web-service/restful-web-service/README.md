## Spring Boot 기본 정보
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

## 예외처리
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

## Validation
```
@PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user)
```
validation을 시행할 객체에 @Valid를 붙인 후 
```
@Data
@AllArgsConstructor
public class User {

    private Integer id;
    @Size(min = 2)
    private String name;
    @Past
    private Date joinDate;

}
```
해당 클래스에서 validation 내용을 어노테이션으로 처리하면 된다
```
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>2.0.1.Final</version>
</dependency>
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.0.7.Final</version>
</dependency>
```
스프링 부트 2.3버전 이후로는 해당 dependency를 추가해주어야 한다

```
@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), "Validation Failed", ex.getBindingResult().toString());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
```
CustomizedResponseEntityExceptionHandler 클래스에 해당 메서드를 추가하고
```
@Size(min = 2, message = "Name은 2글자 이상 입력해 주세요")
    private String name;
```
User 클래스에도 message를 추가한다

CustomizedResponseEntityExceptionHandler는 ResponseEntityExceptionHandler를 상속받는데 내부 메서드인 handleMethodArgumentNotValid를
Override한다. 이 메서드는 validation에서 예외가 발생하면 처리해준다. 
```
{
    "timestamp": "2021-09-04T01:10:56.100+00:00",
    "message": "Validation Failed",
    "details": "org.springframework.validation.BeanPropertyBindingResult: 1 errors\nField error in object 'user' on field 'name': rejected value [A]; codes [Size.user.name,Size.name,Size.java.lang.String,Size]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [user.name,name]; arguments []; default message [name],2147483647,2]; default message [반드시 최소값 2과(와) 최대값 2147483647 사이의 크기이어야 합니다.]"
}
```
postman에서 테스트 결과 400-Bad Request와 함께 ExceptionResponse 객체가 잘 출력됨을 알 수 있다




