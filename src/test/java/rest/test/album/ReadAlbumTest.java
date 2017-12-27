package rest.test.album;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rest.model.Album;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadAlbumTest {
    private static Client client;
    private static WebTarget webTarget;
    private static Integer testAlbumId;
    private static Album testAlbumReturnedByGetCall;

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
        webTarget = client.target("https://jsonplaceholder.typicode.com");
        testAlbumId = getRandomAlbumId();

        WebTarget albumWebTarget = webTarget.path("albums").path(String.valueOf(testAlbumId));
        Invocation.Builder albumInvocationBuilder = albumWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response albumResponse = albumInvocationBuilder.get();
        try {
            testAlbumReturnedByGetCall = albumResponse.readEntity(Album.class);
        } catch (ResponseProcessingException e) {
            Assert.fail("The returned entity does not map correctly on the defined Album entity.");
        }
        //verify status code
        Assert.assertEquals("Status code should be 200. ", 200, albumResponse.getStatus());
    }

    @AfterClass
    public static void cleanUp() {
        testAlbumId = null;
        webTarget = null;
        client.close();
        client = null;
    }

    private static Integer getRandomAlbumId() {
        WebTarget albumWebTarget = webTarget.path("albums");
        Invocation.Builder albumInvocationBuilder = albumWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        List<Album> albums = albumInvocationBuilder.get(new GenericType<List<Album>>() {
        });
        return new Random().nextInt(albums.size()) + 1;
    }

    @Test
    public void verifyIfAlbumIdIsValid() {
        Assert.assertTrue("Id should be a positive Integer. ", testAlbumReturnedByGetCall.getId() > 0);
    }

    @Test
    public void verifyIfUserIdIsValid() {
        Assert.assertTrue("UserId should be a positive Integer. ", testAlbumReturnedByGetCall.getUserId() > 0);
    }

    @Test
    public void verifyIfTitleIsValid() {
        Pattern titlePattern = Pattern.compile("[a-zA-Z0-9._\\-\n\\s]{3,}");
        Matcher titleMatcher = titlePattern.matcher(testAlbumReturnedByGetCall.getTitle());
        Assert.assertTrue("Title should have more than 3 characters. Only letters, digits, points, dashes and underscores are accepted.", titleMatcher.matches());
    }
}
