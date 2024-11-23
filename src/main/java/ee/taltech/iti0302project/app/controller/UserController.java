package ee.taltech.iti0302project.app.controller;

import ee.taltech.iti0302project.app.dto.UserCriteria;
import ee.taltech.iti0302project.app.dto.UserDto;
import ee.taltech.iti0302project.app.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("")
    public ResponseEntity<Page<UserDto>> findUsers(@Valid @ModelAttribute UserCriteria criteria) {
        return ResponseEntity.ok(userService.findUsers(criteria));
    }

}
