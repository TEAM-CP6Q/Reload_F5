package com.f5.accountserver.Controller;

import com.f5.accountserver.DTO.StatusCodeDTO;
import com.f5.accountserver.DTO.UserDetailDTO;
import com.f5.accountserver.Entity.DormantEntity;
import com.f5.accountserver.Repository.DormantRepository;
import com.f5.accountserver.Service.Communication.CommunicationService;
import com.f5.accountserver.Service.UserDetails.UserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class UserDetailController {
    private final UserDetailsService userDetailsService;
    private final CommunicationService communicationService;
    private final DormantRepository dormantRepository;

    public UserDetailController(UserDetailsService userDetailsService, CommunicationService communicationService, DormantRepository dormantRepository) {
        this.userDetailsService = userDetailsService;
        this.communicationService = communicationService;
        this.dormantRepository = dormantRepository;
    }

    @GetMapping("/search-account/{email}")
    public ResponseEntity<?> getUserDetail(@PathVariable String email) {
        try{
            UserDetailDTO user = userDetailsService.getUserDetails(communicationService.searchInfo(email));
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StatusCodeDTO.builder()
                            .Code(404L)
                            .Msg(e.getMessage())
                            .build());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/update-account")
    public ResponseEntity<?> updateAccount(@RequestBody UserDetailDTO userDetailDTO) {
        try {
            userDetailsService.updateUserDetails(userDetailDTO);  // 서비스 레이어에서 업데이트 처리
            return ResponseEntity.ok(StatusCodeDTO.builder()
                            .Code(200L)
                            .Msg("유저 상세 정보가 성공적으로 업데이트 되었습니다.")
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StatusCodeDTO.builder()
                            .Code(404L)
                            .Msg(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(405).body(StatusCodeDTO.builder()
                            .Code(405L)
                            .Msg(e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/withdraw/{id}")
    public ResponseEntity<?> withdraw(@PathVariable Long id) {
        try{
            userDetailsService.deleteAccount(id);
            return ResponseEntity.ok(StatusCodeDTO.builder()
                            .Code(200L)
                            .Msg("탈퇴 성공")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(StatusCodeDTO.builder()
                            .Code(405L)
                            .Msg(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/dormant-accounts")
    public ResponseEntity<?> getDormantAccounts() {
        try{
            List<DormantEntity> dormantEntities = dormantRepository.findAll();
            return ResponseEntity.ok(dormantEntities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(StatusCodeDTO.builder()
                            .Code(405L)
                            .Msg(e.getMessage())
                            .build());
        }
    }
    @GetMapping("/user-list")
    public ResponseEntity<?> getUserList() {
        LinkedHashMap<String, UserDetailDTO> userList = new LinkedHashMap<>();
        try {
            List<UserDetailDTO> users = userDetailsService.findAllUserDetails(); // 전체 사용자 리스트 가져오기
            for (UserDetailDTO user : users) {
                String email = communicationService.getEmail(user.getName()); // 각 사용자의 이메일 가져오기
                userList.put(email, user); // email을 키로, userDetailDTO를 값으로 맵에 추가
            }
            return ResponseEntity.ok(userList); // 성공 시 맵을 응답으로 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(StatusCodeDTO.builder()
                            .Code(500L)
                            .Msg(e.getMessage())
                            .build());
        }
    }
}
