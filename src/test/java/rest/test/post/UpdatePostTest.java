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

public class UpdatePostTest {
    private static Client client;
    private static WebTarget webTarget;
    private static Integer randomPostId;

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
        webTarget = client.target("https://jsonplaceholder.typicode.com");
        randomPostId = getRandomPostId();
    }

    @AfterClass
    public static void cleanUp() {
        randomPostId = null;
        webTarget = null;
        client.close();
        client = null;
    }

    private static Integer getRandomPostId() {
        WebTarget postsWebTarget = webTarget.path("posts");
        Invocation.Builder postsInvocationBuilder = postsWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        List<Post> posts = postsInvocationBuilder.get(new GenericType<List<Post>>() {
        });
        return new Random().nextInt(posts.size()) + 1;
    }

    @Test
    public void testUpdatePost() {
        WebTarget postsWebTarget = webTarget.path("posts/").path(String.valueOf(randomPostId));
        Invocation.Builder postInvocationBuilder = postsWebTarget.request();
        //  Response getAnAllreadyCreatedPost = postInvocationBuilder.get();

        Post newPost = new Post();
        newPost.setUserId(5);
        newPost.setId(randomPostId);
        newPost.setTitle("Created Post Title");
        newPost.setBody("Created Post Body");

        Response putResponse = postInvocationBuilder.put(Entity.entity(newPost, MediaType.APPLICATION_JSON_TYPE));
        //verify status code
        Assert.assertEquals("Expected status code: 200 ", 200, putResponse.getStatus());
        putResponse.close();
        //verify postResponse
        WebTarget putWebTarget = webTarget.path("posts").path(String.valueOf(randomPostId));
        Invocation.Builder putInvocationBuilder = putWebTarget.request(MediaType.APPLICATION_JSON_TYPE);
        try {
            //do a GET to see if the data was correctly added
            Response getUpdatedPostResponse = putInvocationBuilder.get();
            Assert.assertEquals("Expected status code: 200", 200, getUpdatedPostResponse.getStatus());
            Post retrievedPost = getUpdatedPostResponse.readEntity(Post.class);
            Assert.assertEquals("userId is not valid ", newPost.getUserId(), retrievedPost.getUserId());
            Assert.assertEquals("Id is not valid ", newPost.getId(), retrievedPost.getId());
            Assert.assertEquals("Title is not valid.", newPost.getTitle(), retrievedPost.getTitle());
            Assert.assertEquals("Body is not valid.", newPost.getBody(), retrievedPost.getBody());
        } catch (ResponseProcessingException e) {
            Assert.fail("Retrieved entity is not the same type of the updated entity.");
        }
    }
}
