package com.phonebook.okhttpTests;

import com.phonebook.core.TestBase;
import com.phonebook.dto.AuthRequestDto;
import com.phonebook.dto.ErrorDto;
import com.phonebook.dto.TokenDto;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;

public class LoginTests extends TestBase {

    @Test
    public void loginSuccessTest() throws IOException {
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
                .username(email)
                .password(password)
                .build();

        RequestBody requestBody = RequestBody.create(gson.toJson(authRequestDto), JSON);
        Request request = new Request.Builder()
                .url(baseUri + loginPath)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        Assert.assertTrue(response.isSuccessful());

        TokenDto tokenDto = gson.fromJson(response.body().string(), TokenDto.class);
        System.out.println(tokenDto.getToken());
    }

    @Test
    public void loginErrorTestWrongPassword() throws IOException {
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
                .username(email)
                .password("Bernd12345")
                .build();

        RequestBody body = RequestBody.create(gson.toJson(authRequestDto), JSON);
        Request request = new Request.Builder()
                .url(baseUri + loginPath)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        softAssert.assertEquals(response.code(), 401);

        ErrorDto errorDto = gson.fromJson(response.body().string(), ErrorDto.class);
        softAssert.assertEquals(errorDto.getError(), "Unauthorized");
        softAssert.assertEquals(errorDto.getMessage(), "Login or Password incorrect");
        softAssert.assertAll();
    }
}
