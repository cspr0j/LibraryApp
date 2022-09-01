package com.user.app.server.controller;

import com.user.app.server.filter.LoginValidator;
import com.user.app.server.filter.UserRegistrationValidator;
import com.user.app.server.model.User;
import com.user.app.server.service.GetTokenService;
import com.user.app.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final GetTokenService tokenService;

    @Autowired
    private UserRegistrationValidator userValidator;

    @Autowired
    private LoginValidator loginValidator;

    @GetMapping("/")
    public String startPage() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String homePage() {
        return "home";
    }

    @GetMapping("/users")
    public String getUsers() {
//        return "home";
        return "redirect:http://localhost:8080/";
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, Model model) {
        User user = new User();
        model.addAttribute("user", user);

        if (isCookiesExists(request)) return "redirect:/users";

        return "login";
    }

    @PostMapping("/signin")
    public String loginUser(HttpServletRequest request,
                            HttpServletResponse response,
                            @ModelAttribute("user") User user,
                            BindingResult bindingResult) {

        loginValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "login";
        }

        userService.getUser(user.getUsername());

        Map<String, String> tokens = tokenService.getTokens(request, user.getUsername(), user.getPassword());

        Cookie cookie = new Cookie("auth", tokens.get("access_token"));
        cookie.setPath("/");
        cookie.setMaxAge(1800);
        response.addCookie(cookie);

//        Cookie cookie1 = new Cookie("refresh", tokens.get("refresh_token"));
//        cookie1.setPath("/");
//        cookie1.setMaxAge(43200);
//        response.addCookie(cookie1);

        return "redirect:/users";
    }

    @GetMapping("/register")
    public String registerUser(HttpServletRequest request, Model model) {
        User user = new User();
        model.addAttribute("my_user", user);

        if (isCookiesExists(request)) return "redirect:/users";

        return "register";
    }

    @PostMapping("/register")
    public String saveUser(@ModelAttribute("my_user") User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.saveUser(user);

        return "redirect:/login";
    }

    private boolean isCookiesExists(HttpServletRequest request) {
        if (request.getCookies() != null) {
            List<String> auth = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("auth"))
                    .map(Cookie::getValue)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            return auth.size() != 0;
        }
        return false;
    }
}
