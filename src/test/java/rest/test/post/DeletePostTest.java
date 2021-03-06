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

public class DeletePostTest {
    private static Client client;
    private static WebTarget webTarget;
    private static Integer testPostId;


    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
        webTarget = client.target("https://jsonplaceholder.typicode.com");
        testPostId = getRandomPostId();

        WebTarget postWebTarget = webTarget.path("posts").path(String.valueOf(testPostId));
        Invocation.Builder postInvocationBuilder = postWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response postResponse = postInvocationBuilder.get();
        try {
            postResponse.readEntity(Post.class);
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
    public void testDeletePost(){
        WebTarget postsWebTarget = webTarget.path("posts/").path(String.valueOf(testPostId));
        Invocation.Builder postInvocationBuilder = postsWebTarget.request();
        Response deletedPostResponse = postInvocationBuilder.delete();
        //verify status code
        Assert.assertEquals("Expected status code: 200 ", 200, deletedPostResponse.getStatus());
        deletedPostResponse.close();
        Response getDeletedPostResponse = postInvocationBuilder.get();
        Assert.assertEquals("Expected status code: 404", 404, getDeletedPostResponse.getStatus());
    }
}
