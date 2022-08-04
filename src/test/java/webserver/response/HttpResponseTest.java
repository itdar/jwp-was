package webserver.response;

import static org.assertj.core.api.Assertions.assertThat;
import static webserver.supporter.SupportTemplates.PATH_TEMPLATES;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;
import webserver.domain.Cookie;
import webserver.enums.HttpStatus;

class HttpResponseTest {

    private String testDirectory = "./src/test/resources/";

    @Test
    void responseMethodNotAllowed() {
        HttpResponse response = new HttpResponse();
        response.methodNotAllowed();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void responseNotFound() {
        HttpResponse response = new HttpResponse();
        response.notFound();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void responseForward() throws Exception {
        // Http_Forward.txt 결과는 응답 body에 index.html이 포함되어 있어야 한다.
        HttpResponse response = new HttpResponse(createOutputStream("Http_Forward.txt"));
        response.forward(PATH_TEMPLATES + "/index.html");
    }

    @Test
    void responseRedirect() throws Exception {
        // Http_Redirect.txt 결과는 응답 headere에 Location 정보가 /index.html로 포함되어 있어야 한다.
        HttpResponse response = new HttpResponse(createOutputStream("Http_Redirect.txt"));
        response.sendRedirect("/index.html");
    }

    @Test
    void responseCookies() throws Exception {
        // Http_Cookie.txt 결과는 응답 header에 Set-Cookie 값으로 logined=true 값이 포함되어 있어야 한다.
        HttpResponse response = new HttpResponse(createOutputStream("Http_Cookie.txt"));
        response.addCookie(Cookie.loginedWithPath("/"));
        response.sendRedirect("/index.html");
    }

    private OutputStream createOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(testDirectory + filename);
    }

}