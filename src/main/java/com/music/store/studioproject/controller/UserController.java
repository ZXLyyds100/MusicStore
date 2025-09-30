package com.music.store.studioproject.controller;

import com.music.store.studioproject.dto.ChangePasswordDto;
import com.music.store.studioproject.utils.Response;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
public class UserController {
    @PutMapping("/password")
    @PreAuthorize("hasRole('USER')")
    public Response changePassword(@RequestBody ChangePasswordDto changePasswordDto) {

    }
}
