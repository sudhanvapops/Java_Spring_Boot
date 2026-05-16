Create jsp inside
webapp folder
webapp is under main folder


And for controller you just make a class and add 
@Controller anotation
it autmaticaly makes the controller



### Controller


no need of extending class adn creaiting it manaully


@RequestMapping("/")
public String home(){
    return "index.jsp"
}

url: /
index.jsp file is served

Spring maps that URL to the home() method.


jsp file is nt served directly 
so u need
tomcat jasper to serve jsp file

@RequestMapping("/"):
“When HTTP request comes for /home, execute this method.


### Mappings

GetMapping
PostMapping
others
