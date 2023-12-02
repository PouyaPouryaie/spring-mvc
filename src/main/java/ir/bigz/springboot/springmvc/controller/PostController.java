package ir.bigz.springboot.springmvc.controller;

import ir.bigz.springboot.springmvc.dal.CommentRepository;
import ir.bigz.springboot.springmvc.dal.PostRepository;
import ir.bigz.springboot.springmvc.dao.Comment;
import ir.bigz.springboot.springmvc.dao.Post;
import ir.bigz.springboot.springmvc.exceptions.PostDeletionException;
import ir.bigz.springboot.springmvc.exceptions.ResourceNotFoundException;
import ir.bigz.springboot.springmvc.model.ErrorDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@RestController
@RequestMapping(value="/posts")
public class PostController {

    final PostRepository postRepository;

    final CommentRepository commentRepository;

    public PostController(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }


    /* @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Post createPost(@RequestBody Post post)
    {
        return postRepository.save(post);
    }*/

    @PostMapping("")
    public ResponseEntity<?> createPost(@RequestBody @Valid Post post, BindingResult result){

        if(result.hasErrors()){
            StringBuilder devErrorMsg = new StringBuilder();
            List<ObjectError> allErrors = result.getAllErrors();

            for(ObjectError objectError: allErrors){
                devErrorMsg.append(objectError.getDefaultMessage() + "\n");
            }

            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setErrorCode("ERR-1400");
            errorDetails.setErrorMessage("Invalid Post data received");
            errorDetails.setDevErrorMessage(devErrorMsg.toString());

            return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        Post savedPost = postRepository.save(post);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("MyResponseHeader", "MyValue");
        return new ResponseEntity<>(savedPost, responseHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public List<Post> listPosts()
    {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable("id") Integer id){

        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No post found with id= " + id));
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable("id") Integer id)
    {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No post found with id= " + id));
        try {
            postRepository.deleteById(post.getId());
        } catch (Exception e) {
            throw new PostDeletionException("Post with id= " + id + " can't be deleted");
        }
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable("id") Integer id, @RequestBody
            Post post)
    {
        postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("No post found with id= " + id));
        return postRepository.save(post);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}/comments")
    public void createPostComment(@PathVariable("id") Integer id, @RequestBody Comment comment)
    {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No post found with id="+id));
        post.getComments().add(comment);
    }

    @GetMapping("/{postId}/comments/{commentId}")
    public Comment getPostComment(@PathVariable("postId") Integer postId,
                                  @PathVariable("commentId") Integer commentId)
    {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("No comment found with id="+commentId));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public void deletePostComment(@PathVariable("postId") Integer postId,
                                  @PathVariable("commentId") Integer commentId)
    {
        commentRepository.deleteById(commentId);
    }

    @ExceptionHandler(PostDeletionException.class)
    public ResponseEntity<?> servletRequestBindingException(PostDeletionException e) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage(e.getMessage());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        errorDetails.setDevErrorMessage(sw.toString());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
