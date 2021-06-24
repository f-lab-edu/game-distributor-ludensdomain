package com.ludensdomain.controller;

import com.ludensdomain.aop.LoginCheck;
import com.ludensdomain.dto.GameDto;
import com.ludensdomain.dto.UserDto;
import com.ludensdomain.service.LoginService;
import com.ludensdomain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;

import static com.ludensdomain.util.ResponseEntityConstants.*;

@Log4j2
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LoginService loginService;

    @PostMapping("login")
    public ResponseEntity<Void> login(@Valid long id, String password) {

        return userService.login(id, password) ? RESPONSE_OK : RESPONSE_BAD_REQUEST;
    }

    @PostMapping
    public void signUp(@RequestBody UserDto user) {

        userService.insertUserInfo(user);
    }

    @GetMapping("{id}")
    public UserDto userInfo(@PathVariable long id) {

        return userService.getUserInfo(id);
    }

    @LoginCheck
    @PutMapping
    public void update(@RequestBody @Valid UserDto user) {

        userService.updateUserInfo(user);
    }

    @LoginCheck
    @PutMapping("{id}")
    public void changePassword(@PathVariable long id, String newPw) {

        userService.changePassword(id, newPw);
    }

    @LoginCheck
    @PostMapping("logout")
    public void logout() {

        loginService.logout();
    }

    @LoginCheck
    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable long id) {

        userService.deleteUser(id);
    }

    @GetMapping("/{id}/games")
    public List<GameDto> getPurchaseHistory(@PathVariable long id) {

        return userService.getPurchaseHistory(id);
    }
}