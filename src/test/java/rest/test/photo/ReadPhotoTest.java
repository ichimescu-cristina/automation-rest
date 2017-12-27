package rest.test.photo;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rest.model.Photo;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadPhotoTest {
    private static Client client;
    private static WebTarget webTarget;
    private static Integer testPhotoId;
    private static Photo testPhotoReturnedByGetCall;

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
        webTarget = client.target("https://jsonplaceholder.typicode.com");
        testPhotoId = getRandomPhotoId();

        WebTarget photoWebTarget = webTarget.path("photos").path(String.valueOf(testPhotoId));
        Invocation.Builder photoInvocationBuilder = photoWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response photoResponse = photoInvocationBuilder.get();
        try {
            testPhotoReturnedByGetCall = photoResponse.readEntity(Photo.class);
        } catch (ResponseProcessingException e) {
            Assert.fail("The returned entity does not map correctly on the defined Photo entity.");
        }
        //verify status code
        Assert.assertEquals("Status code should be 200. ", 200, photoResponse.getStatus());
    }

    @AfterClass
    public static void cleanUp() {
        testPhotoId = null;
        webTarget = null;
        client.close();
        client = null;
    }

    private static Integer getRandomPhotoId() {
        WebTarget photoWebTarget = webTarget.path("photos");
        Invocation.Builder photoInvocationBuilder = photoWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        List<Photo> photos = photoInvocationBuilder.get(new GenericType<List<Photo>>() {
        });
        return new Random().nextInt(photos.size()) + 1;
    }

    @Test
    public void verifyIfAlbumIdIsValid() {
        Assert.assertTrue("AlbumId should be a positive Integer. ", testPhotoReturnedByGetCall.getAlbumId() > 0);
    }

    @Test
    public void verifyIfIdIsValid() {
        Assert.assertTrue("Id should be a positive Integer. ", testPhotoReturnedByGetCall.getId() > 0);
    }

    @Test
    public void verifyIfTitleIsValid() {
        Pattern titlePattern = Pattern.compile("[a-zA-Z0-9._\\-\n\\s]{3,}");
        Matcher titleMatcher = titlePattern.matcher(testPhotoReturnedByGetCall.getTitle());
        Assert.assertTrue("Title should have more than 3 characters. Only letters, digits, points, dashes and underscores are accepted.", titleMatcher.matches());
    }

    @Test
    public void verifyIfUrlIsValid() {
        Pattern titlePattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        Matcher titleMatcher = titlePattern.matcher(testPhotoReturnedByGetCall.getUrl());
        Assert.assertTrue("Url should match the agreed format.", titleMatcher.matches());
    }

    @Test
    public void verifyIfThumbnailUrlIsValid() {
        Pattern titlePattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        Matcher titleMatcher = titlePattern.matcher(testPhotoReturnedByGetCall.getThumbnailUrl());
        Assert.assertTrue("ThumbnailUrl should match the agreed format.", titleMatcher.matches());
    }
}
