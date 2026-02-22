package com.phonebook.okhttpTests;

import com.phonebook.core.TestBase;
import com.phonebook.dto.ContactDto;
import com.phonebook.dto.ErrorDto;
import com.phonebook.dto.MessageDto;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class AddContactTests extends TestBase {

    @Test
    public void addContactSuccessTest() throws IOException {
        ContactDto contactDto = ContactDto.builder()
                .name("John")
                .lastName("Smith")
                .email("kan@gm.com")
                .phone("1234567890")
                .address("Berlin")
                .description("goalkeeper")
                .build();

        RequestBody body = RequestBody.create(gson.toJson(contactDto), JSON);

        Request request = new Request.Builder()
                .url(baseUri + contactPath)
                .post(body)
                .addHeader(AUTH, TOKEN)
                .build();

        Response response = client.newCall(request).execute();
        Assert.assertTrue(response.isSuccessful());

        MessageDto messageDto = gson.fromJson(response.body().string(), MessageDto.class);
        System.out.println(messageDto.getMessage());
    }

    @Test
    public void addContactWithoutTokenTest() throws IOException {

        ContactDto contactDto = ContactDto.builder()
                .name("John")
                .lastName("Smith")
                .email("forbidden@gm.com")
                .phone("1234567890")
                .address("Berlin")
                .description("goalkeeper")
                .build();

        RequestBody body = RequestBody.create(gson.toJson(contactDto), JSON);

        Request request = new Request.Builder()
                .url(baseUri + contactPath)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        softAssert.assertEquals(response.code(), 403);

        String responseBody = response.body().string();
        System.out.println(responseBody);

        softAssert.assertAll();
    }

    @Test
    public void addContactWithWrongTokenTest() throws IOException {
        ContactDto contactDto = ContactDto.builder()
                .name("John")
                .lastName("Smith")
                .email("wrongtoken@gm.com")
                .phone("1234567890")
                .address("Berlin")
                .description("goalkeeper")
                .build();

        RequestBody body = RequestBody.create(gson.toJson(contactDto), JSON);

        Request request = new Request.Builder()
                .url(baseUri + contactPath)
                .post(body)
                .addHeader(AUTH, "WrongToken")
                .build();

        Response response = client.newCall(request).execute();

        softAssert.assertEquals(response.code(), 401);

        ErrorDto errorDto = gson.fromJson(response.body().string(), ErrorDto.class);
        softAssert.assertEquals(errorDto.getError(), "Unauthorized");

        softAssert.assertAll();
    }

    @Test
    public void addContactWithInvalidPhoneTest() throws IOException {

        ContactDto contactDto = ContactDto.builder()
                .name("John")
                .lastName("Smith")
                .email("kan@gm.com")
                .phone("John123")
                .address("Berlin")
                .description("test")
                .build();

        RequestBody body = RequestBody.create(gson.toJson(contactDto), JSON);

        Request request = new Request.Builder()
                .url(baseUri + contactPath)
                .post(body)
                .addHeader(AUTH, TOKEN)
                .build();

        Response response = client.newCall(request).execute();

        int statusCode = response.code();
        String responseBody = response.body().string();

        System.out.println("Status: " + statusCode);
        System.out.println("Body: " + responseBody);

        Assert.assertEquals(statusCode, 400);
        Assert.assertTrue(responseBody.contains("Phone number must contain only digits"));
    }

}
