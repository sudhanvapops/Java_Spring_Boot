### lombok

gives autmoatically the getters adn seetrs for the repos class

add
@Data in top of the repo class

@NoArgsConstructor
Generates: public JobPost() {}


@AllArgsConstructor
Generates constructor with all fields


### Workign with Form  229

in action give the handled url name

in spring 

@PostMapping("handleForm")
// Spring automatically creates and fills jobPost from form fields (data binding).
public String handleForm(JobPost jobPost){
    System.out.println(jobPost);
    return "success";
}

So here jobPost inside spring is used
- creats object of JobPost
- getts all the attributes from the form 
- sets all the attributes taken fromt the form to the fields
- and send them to succes page 


