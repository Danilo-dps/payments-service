package pay.application.controller;

import org.springframework.web.bind.annotation.*;
import pay.domain.dto.UserDTO;
import pay.domain.record.DepositResponse;
import pay.domain.record.TransferResponse;
import pay.domain.record.UserResponse;
import pay.domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        UserResponse userSearch = userService.getById(userId);
        return ResponseEntity.ok(userSearch);
    }

    @GetMapping("/email/{userEmail}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String userEmail) {
        UserResponse userSearch = userService.getByEmail(userEmail);
        return ResponseEntity.ok(userSearch);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID userId, @RequestBody UserResponse userResponse){
        UserDTO userUpdate = userService.update(userId, userResponse);
        return ResponseEntity.ok(userUpdate);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity <Void> deleteUser(@PathVariable UUID userId){
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deposit/{userId}")
    public ResponseEntity<List<DepositResponse>> getAllDeposit(@PathVariable UUID userId){
        List<DepositResponse> listAllDeposits = userService.getAllDeposits(userId);
        return listAllDeposits.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(listAllDeposits);
    }

//    @GetMapping("/transfer/{userId}")
//    public ResponseEntity<List<TransferResponse>> getAllTransfer(@PathVariable UUID userId){
//        List<TransferResponse> listAllTransfers = userService.getAllTransfers(userId);
//        return listAllTransfers.isEmpty()
//                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
//                : ResponseEntity.ok(listAllTransfers);
//    }
}
