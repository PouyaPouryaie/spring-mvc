package ir.bigz.springboot.springmvc;

import ir.bigz.springboot.springmvc.dal.PostRepository;
import ir.bigz.springboot.springmvc.dao.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = SpringMvcApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringMvcApplicationTests {

    @Autowired
    PostRepository postRepository;

	@Test
	void contextLoads() {
	}

	@LocalServerPort
	private int port;

	RestTemplate restTemplate = new RestTemplate();

	private String getRootUrl(){
		return "http://localhost:" + port;
	}

	@Test
	public void testGetAllPosts()
	{
		ResponseEntity<Post[]> responseEntity = restTemplate.getForEntity(getRootUrl()+"/posts/", Post[].class);
        List<Post> posts = Arrays.asList(responseEntity.getBody());
		Assertions.assertNotNull(posts);
	}

	@Test
	public void testGetPostById()
	{
		Post post = restTemplate.getForObject(getRootUrl()+"/posts/1", Post.class);
		Assertions.assertNotNull(post);
	}

	@Test
	public void testCreatePost()
	{
		Post post = new Post();
		post.setTitle("Exploring SpringBoot REST");
		post.setContent("SpringBoot is awesome!!");
		post.setCreatedOn(new Date());

		ResponseEntity<Post> postResponse = restTemplate.postForEntity(getRootUrl()+"/posts", post, Post.class);
		Assertions.assertNotNull(postResponse);
		Assertions.assertNotNull(postResponse.getBody());	}

	@Test
	public void testUpdatePost()
	{
		int id = 1;
		Post post = restTemplate.getForObject(getRootUrl()+"/posts/"+id, Post.class);
		post.setContent("This my updated post1 content");
		post.setUpdatedOn(new Date());

		restTemplate.put(getRootUrl()+"/posts/"+id, post);

		Post updatedPost = restTemplate.getForObject(getRootUrl()+"/posts/"+id, Post.class);
		Assertions.assertNotNull(updatedPost);
	}

	@Test
	public void testDeletePost()
	{
		int id = 2;
		Post post = restTemplate.getForObject(getRootUrl()+"/posts/"+id, Post.class);
		Assertions.assertNotNull(post);

		restTemplate.delete(getRootUrl()+"/posts/"+id);

		try {
			post = restTemplate.getForObject(getRootUrl()+"/posts/"+id, Post.class);
		}
		catch (final HttpClientErrorException e) {
			Assertions.assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
		}
	}

	@Test
    public void getPost(){
	    Post post = postRepository.findById(1).orElse(null);

        System.out.println(post.toString());

        Assertions.assertNotNull(post);
    }


}
