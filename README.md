# 웹 애플리케이션 서버
## 진행 방법
* 웹 애플리케이션 서버 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

---
---

## 3단계. HTTP 웹 서버 리팩토링 
- 요구사항: HTTP 요청 Header/Body 처리, 응답 Header/Body 처리만을 담당하는 역할을 분리해 재사용 가능하도록 한다.
  1. [ ] 메서드 분리 및 클래스 분리 
    - RequestHandler 클래스의 책임을 분리한다.
      - RequestHandler 클래스는 많은 책임을 가지고 있다. 객체 지향 설계 원칙 중 “단일 책임의 원칙”에 따라 RequestHandler 클래스가 가지고 있는 책임을 찾아 각 책임을 새로운 클래스를 만들어 분리한다.
    - 다음과 같이 3개의 역할로 분리해 리팩토링을 시도하고, 테스트 가능한 부분은 단위 테스트를 진행한다.
      - 클라이언트 요청 데이터를 처리하는 로직을 별도의 클래스로 분리한다.(HttpRequest)
      - 클라이언트 응답 데이터를 처리하는 로직을 별도의 클래스로 분리한다.(HttpResponse)
      - 다형성을 활용해 클라이언트 요청 URL에 대한 분기 처리를 제거한다.
  2. [ ] 클라이언트 요청 데이터를 처리하는 로직을 별도의 클래스로 분리한다.(HttpRequest)
    - 클라이언트 요청 데이터를 담고 있는 InputStream을 생성자로 받아 HTTP 메소드, URL, 헤더, 본문을 분리하는 작업을 한다.
    - 헤더는 Map<String, String>에 저장해 관리하고 getHeader(“필드 이름”) 메소드를 통해 접근 가능하도록 구현한다.
    - GET과 POST 메소드에 따라 전달되는 인자를 Map<String, String>에 저장해 관리하고 getParameter(“인자 이름”) 메소드를 통해 접근 가능하도록 구현한다.
    - RequestHandler가 새로 추가한 HttpRequest를 사용하도록 리팩토링한다.
  3. [ ] 클라이언트 응답 데이터를 처리하는 로직을 별도의 클래스로 분리한다.(HttpResponse)
      - RequestHandler 클래스를 보면 응답 데이터 처리를 위한 많은 중복이 있다. 이 중복을 제거해 본다.
      - 응답 헤더 정보를 Map<String, String>으로 관리한다.
      - 응답을 보낼 때 HTML, CSS, 자바스크립트 파일을 직접 읽어 응답으로 보내는 메소드는 forward(), 다른 URL로 리다이렉트하는 메소드는 sendRedirect() 메소드를 나누어 구현한다.
      - RequestHandler가 새로 추가한 HttpResponse를 사용하도록 리팩토링한다.
  4. [ ] 다형성을 활용해 클라이언트 요청 URL에 대한 분기 처리를 제거
  5. [ ] 추가 요구사항이나 변경이 발생하는 경우 처리에 대한 고려


---
--- 
## 2단계. HTTP 웹 서버 구현
- [x] 기능 요구사항 1
> http://localhost:8080/index.html 로 접속했을 때 webapp 디렉토리의 index.html 파일을 읽어 클라이언트에 응답한다.
- HTTP Request Header 예
```
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
```

<br>

- [x] 기능 요구사항 2
> “회원가입” 메뉴를 클릭하면 http://localhost:8080/user/form.html 으로 이동하면서 회원가입할 수 있다. 회원가입한다.

> 회원가입을 하면 다음과 같은 형태로 사용자가 입력한 값이 서버에 전달된다.
```
/create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net
```
> HTML과 URL을 비교해 보고 사용자가 입력한 값을 파싱해 model.User 클래스에 저장한다.

<br>

- [x] 기능 요구사항 3
> http://localhost:8080/user/form.html 파일의 form 태그 method를 get에서 post로 수정한 후 회원가입 기능이 정상적으로 동작하도록 구현한다.

- HTTP Request Header 예
```
POST /user/create HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Content-Length: 59
Content-Type: application/x-www-form-urlencoded
Accept: */*

userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net
```

<br>

- [x] 기능 요구사항 4
> “회원가입”을 완료하면 /index.html 페이지로 이동하고 싶다. 현재는 URL이 /user/create 로 유지되는 상태로 읽어서 전달할 파일이 없다. 따라서 redirect 방식처럼 회원가입을 완료한 후 “index.html”로 이동해야 한다. 즉, 브라우저의 URL이 /index.html로 변경해야 한다.

<br>

- [x] 기능 요구사항 5 
>  “로그인” 메뉴를 클릭하면 http://localhost:8080/user/login.html 으로 이동해 로그인할 수 있다. 로그인이 성공하면 index.html로 이동하고, 로그인이 실패하면 /user/login_failed.html로 이동해야 한다.  

>  앞에서 회원가입한 사용자로 로그인할 수 있어야 한다. 로그인이 성공하면 cookie를 활용해 로그인 상태를 유지할 수 있어야 한다. 로그인이 성공할 경우 요청 header의 Cookie header 값이 logined=true, 로그인이 실패하면 Cookie header 값이 logined=false로 전달되어야 한다.
- HTTP Request Header 예
```
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
Cookie: logined=true
```
- HTTP Response Header 예
```
HTTP/1.1 200 OK
Content-Type: text/html
Set-Cookie: logined=true; Path=/
```

<br>

- [x] 기능 요구사항 6
> 접근하고 있는 사용자가 “로그인” 상태일 경우(Cookie 값이 logined=true) 경우 http://localhost:8080/user/list 로 접근했을 때 사용자 목록을 출력한다. 만약 로그인하지 않은 상태라면 로그인 페이지(login.html)로 이동한다.

> 동적으로 html을 생성하기 위해 handlebars.java template engine을 활용한다. 

<br> 

- [x] 기능 요구사항 7
> 지금까지 구현한 소스 코드는 stylesheet 파일을 지원하지 못하고 있다. Stylesheet 파일을 지원하도록 구현하도록 한다.
```
HTTP Request Header 예
GET ./css/style.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
```

---
---

## 1단계. RequestLine 을 파싱한다.
- RequestLine을 파싱해 원하는 값을 가져올 수 있는 API를 제공해야 한다.
- RequestLine은 HTTP 요청의 첫번째 라인을 의미한다.

--- 

- [x] 요구사항 1 - GET 요청

- HTTP GET 요청에 대한 RequestLine을 파싱한다.
- 파싱하는 로직 구현을 TDD로 구현한다.
```
예를 들어 "GET /users HTTP/1.1"을 파싱하면 다음과 같은 결과를 얻을 수 있어야 한다.

method는 GET
path는 /users
protocol은 HTTP
version은 1.1
```

--- 

- [x] 요구사항 2 - POST 요청
- HTTP POST 요청에 대한 RequestLine을 파싱한다.
- 파싱하는 로직 구현을 TDD로 구현한다.
```
예를 들어 "POST /users HTTP/1.1"을 파싱하면 다음과 같은 결과를 얻을 수 있어야 한다.

method는 POST
path는 /users
protocol은 HTTP
version은 1.1
```

--- 

- [x] 요구사항 3 - Query String 파싱
- HTTP 요청(request)의 Query String으로 전달되는 데이터를 파싱한다.
- 클라이언트에서 서버로 전달되는 데이터의 구조는 name1=value1&name2=value2와 같은 구조로 전달된다.
- 파싱하는 로직 구현을 TDD로 구현한다.

```
Query String 예 - GET 요청

GET /users?userId=javajigi&password=password&name=JaeSung HTTP/1.1
```

--- 

- [x] 요구사항 4 - enum 적용(선택)  
HTTP method인 GET, POST를 enum으로 구현한다.
