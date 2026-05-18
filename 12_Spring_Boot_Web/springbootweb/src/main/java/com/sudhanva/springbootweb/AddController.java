package com.sudhanva.springbootweb;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AddController {
    
    @RequestMapping("/add")
    public String add(){
        return "add.jsp";
    }

    // @RequestMapping("/output")
    // public String result(HttpServletRequest req,HttpSession session){


    //     int num1 = Integer.parseInt(req.getParameter("num1"));
    //     int num2 = Integer.parseInt(req.getParameter("num2"));
    //     int result = num1 + num2;

    //     session.setAttribute("result", result);

    //     return "output.jsp";
    // }

    // Spring Boot Way

    @RequestMapping("/output")
    public String result(@RequestParam("num1") int a, @RequestParam("num2") int b,Model model){

        int result = a + b;

        model.addAttribute("result", result);

        return "output.jsp";
    }

}
