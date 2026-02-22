package com.phonebook.okhttpTests;

import com.phonebook.core.TestBase;
import com.phonebook.dto.AllContactsDto;
import com.phonebook.dto.ContactDto;
import com.phonebook.dto.ErrorDto;
import okhttp3.Request;
import okhttp3.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class GetAllContactsTests extends TestBase {

    @Test
    public void getAllContactsSuccessTest() throws IOException {
        Request request = new Request.Builder()
                .url(baseUri + contactPath)
                .get()
                .addHeader(AUTH, TOKEN)
                .build();
        Response response = client.newCall(request).execute();
        Assert.assertTrue(response.isSuccessful());

        AllContactsDto contactsDto = gson.fromJson(response.body().string(), AllContactsDto.class);
        for (ContactDto contactDto : contactsDto.getContacts()) {
            System.out.println(contactDto.getId());
            System.out.println(contactDto.getName());
            System.out.println("*************************");
        }
    }

    @Test
    public void getAllContactsWithWrongTokenTest() throws IOException {

        Request request = new Request.Builder()
                .url(baseUri + contactPath)
                .get()
                .addHeader(AUTH, "WrongToken")
                .build();

        Response response = client.newCall(request).execute();

        softAssert.assertEquals(response.code(), 401);

        ErrorDto errorDto = gson.fromJson(response.body().string(), ErrorDto.class);
        softAssert.assertEquals(errorDto.getError(), "Unauthorized");

        softAssert.assertAll();
    }
}
