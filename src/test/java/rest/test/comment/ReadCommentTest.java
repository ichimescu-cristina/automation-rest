package rest.test.comment;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rest.model.Comment;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadCommentTest {
    private static Client client;
    private static WebTarget webTarget;
    private static Integer testCommentId;
    private static Comment testCommentReturnedByGetCall;

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
        webTarget = client.target("https://jsonplaceholder.typicode.com");
        testCommentId = getRandomCommentId();

        WebTarget commentWebTarget = webTarget.path("comments").path(String.valueOf(testCommentId));
        Invocation.Builder commentInvocationBuilder = commentWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response commentResponse = commentInvocationBuilder.get();
        try {
            testCommentReturnedByGetCall = commentResponse.readEntity(Comment.class);
        } catch (ResponseProcessingException e) {
            Assert.fail("The returned entity does not map correctly on the defined Comment entity.");
        }
        //verify status code
        Assert.assertEquals("Status code should be 200. ", 200, commentResponse.getStatus());
    }

    @AfterClass
    public static void cleanUp() {
        testCommentId = null;
        webTarget = null;
        client.close();
        client = null;
    }

    private static Integer getRandomCommentId() {
        WebTarget commentWebTarget = webTarget.path("comments");
        Invocation.Builder commentInvocationBuilder = commentWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        List<Comment> comments = commentInvocationBuilder.get(new GenericType<List<Comment>>() {
        });
        return new Random().nextInt(comments.size()) + 1;
    }

    @Test
    public void verifyIfCommentIdIsValid() {
        Assert.assertTrue("Id should be a positive Integer. ", testCommentReturnedByGetCall.getId() > 0);
    }

    @Test
    public void verifyIfPostIdIsValid() {
        Assert.assertTrue("PostId should be a positive Integer. ", testCommentReturnedByGetCall.getPostId() > 0);
    }

    @Test
    public void verifyIfNameIsValid() {
        Pattern namePattern = Pattern.compile("^[a-zA-Z\\s]+");
        Matcher nameMatcher = namePattern.matcher(testCommentReturnedByGetCall.getName());
        Assert.assertTrue("Name should only contain letters and spaces. ", nameMatcher.matches());
    }

    @Test
    public void verifyIfEmailAddressIsValid() {
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");
        Matcher emailMatcher = emailPattern.matcher(testCommentReturnedByGetCall.getEmail());
        Assert.assertTrue("Email format should match a specific expression.", emailMatcher.matches());
    }

    @Test
    public void verifyIfBodyIsValid() {
        System.out.println(testCommentReturnedByGetCall.getBody());
        Pattern bodyPattern = Pattern.compile("[a-zA-Z0-9._\\-\n\\s]{3,}");
        Matcher bodyMatcher = bodyPattern.matcher(testCommentReturnedByGetCall.getBody());
        Assert.assertTrue("Body should have more than 3 characters. Only letters, digits, points, dashes and underscores are accepted.", bodyMatcher.matches());
    }
}
