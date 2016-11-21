package org.baeldung.okhttp;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/api-servlet.xml")
public class OkHttpGetLiveTest {

    private static final String BASE_URL = "http://localhost:8080/spring-rest";

    OkHttpClient client;

    @Before
    public void init() {

    	client = new OkHttpClient();
    }

    @Test
    public void whenGetRequest_thenCorrect() throws IOException {

        client = new OkHttpClient();

        Request request = new Request.Builder()
          .url(BASE_URL + "/date")
          .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        assertThat(response.code(), equalTo(200));
    }

    @Test
    public void whenGetRequestWithQueryParameter_thenCorrect() throws IOException {

        client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/ex/bars").newBuilder();
        urlBuilder.addQueryParameter("id", "1");

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
          .url(url)
          .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        assertThat(response.code(), equalTo(200));
    }

    @Test
    public void whenAsynchronousGetRequest_thenCorrect() throws InterruptedException {

        client = new OkHttpClient();

        Request request = new Request.Builder()
          .url(BASE_URL + "/date")
          .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                assertThat(response.code(), equalTo(200));
            }

            public void onFailure(Call call, IOException e) {
            	fail();
            }
        });

        Thread.sleep(3000);
    }
}
