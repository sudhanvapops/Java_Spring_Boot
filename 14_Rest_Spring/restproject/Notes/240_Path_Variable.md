### Path Variable 

- @PathVariable is used to get values from the URL path and pass them into your controller method.

@RestController
public class UserController {

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable int id) {
        return "User ID: " + id;
    }
}