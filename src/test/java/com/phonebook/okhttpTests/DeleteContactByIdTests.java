package com.phonebook.okhttpTests;

import com.phonebook.core.TestBase;
import com.phonebook.dto.ContactDto;
import com.phonebook.dto.MessageDto;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

public class DeleteContactByIdTests extends TestBase {

    String id;

    @BeforeMethod
    public void precondition() throws IOException {
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

        MessageDto messageDto = gson.fromJson(response.body().string(), MessageDto.class);
        String[] split = messageDto.getMessage().split(": ");
        id = split[1];
    }

    @Test
    public void deleteContactByIdSuccessTest() throws IOException {
        Request request = new Request.Builder()
                .url(baseUri + contactPath + "/" + id)
                .delete()
                .addHeader(AUTH, TOKEN)
                .build();

        Response response = client.newCall(request).execute();
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(response.code(), 200);

        MessageDto messageDto = gson.fromJson(response.body().string(), MessageDto.class);
        //System.out.println(messageDto.getMessage());
        Assert.assertEquals(messageDto.getMessage(), "Contact was deleted!");
    }
}
