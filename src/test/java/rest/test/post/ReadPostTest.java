package rest.test.post;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rest.model.Post;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadPostTest {
    private static Client client;
    private static WebTarget webTarget;
    private static Integer testPostId;
    private static Post testPostReturnedByGetCall;

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
        webTarget = client.target("https://jsonplaceholder.typicode.com");
        testPostId = getRandomPostId();

        WebTarget postWebTarget = webTarget.path("posts").path(String.valueOf(testPostId));
        Invocation.Builder postInvocationBuilder = postWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response postResponse = postInvocationBuilder.get();
        try {
            testPostReturnedByGetCall = postResponse.readEntity(Post.class);
        } catch (ResponseProcessingException e) {
            Assert.fail("The returned entity does not map correctly on the defined Post entity.");
        }
        //verify status code
        Assert.assertEquals("Status code should be 200. ", 200, postResponse.getStatus());
    }

    @AfterClass
    public static void cleanUp() {
        testPostId = null;
        webTarget = null;
        client.close();
        client = null;
    }

    private static Integer getRandomPostId() {
        WebTarget postWebTarget = webTarget.path("posts");
        Invocation.Builder postInvocationBuilder = postWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        List<Post> posts = postInvocationBuilder.get(new GenericType<List<Post>>() {
        });
        return new Random().nextInt(posts.size()) + 1;
    }

    @Test
    public void verifyIfPostIdIsValid() {
        Assert.assertTrue("PostId should be a positive Integer. ", testPostReturnedByGetCall.getId() > 0);
    }
    @Test
    public void verifyIfUserIdIsValid() {
        Assert.assertTrue("UserId should be a positive Integer. ", testPostReturnedByGetCall.getUserId() > 0);
    }
    @Test
    public void verifyIfTitleIsValid() {
        Pattern titlePattern = Pattern.compile("[a-zA-Z0-9._\\-\n\\s]{3,}");
        Matcher titleMatcher = titlePattern.matcher(testPostReturnedByGetCall.getTitle());
        Assert.assertTrue("Title should have more than 3 characters. Only letters, digits, points, dashes and underscores are accepted.", titleMatcher.matches());
    }
    @Test
    public void verifyIfBodyIsValid() {
        System.out.println(testPostReturnedByGetCall.getBody());
        Pattern bodyPattern = Pattern.compile("[a-zA-Z0-9._\\-\n\\s]{3,}");
        Matcher bodyMatcher = bodyPattern.matcher(testPostReturnedByGetCall.getBody());
        Assert.assertTrue("Body should have more than 3 characters. Only letters, digits, points, dashes and underscores are accepted.", bodyMatcher.matches());
    }
}
