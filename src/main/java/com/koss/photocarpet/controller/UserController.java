package com.koss.photocarpet.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.koss.photocarpet.controller.dto.request.UserRequestDTO;
import com.koss.photocarpet.controller.dto.response.ExhibitionResponse;
import com.koss.photocarpet.controller.dto.response.SocialUserResponse;
import com.koss.photocarpet.service.KakaoUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
 
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController
{
    @Autowired
    private final KakaoUserService kakaoUserService;

    // 클라이언트로부터 인가 코드, response 받아와서 백엔드에서 가입, 또는 로그인(동일하게) 실행.
    // 여기서 나온 결과 Response 에서 .getToken() 하여, 클라이언트에서 헤더에 다는 로직이 필요함.
    @PostMapping("/kakao/login")
    public Object kakaoLogin(@RequestBody UserRequestDTO userdto) throws IOException {
        System.out.println("Login process");
        try {
            SocialUserResponse existingUser  = kakaoUserService.validateKakaoUser(userdto);
            // 유저가 없을때
            if (existingUser==null){
                SocialUserResponse reject_response = SocialUserResponse.builder().validate_check(false).email(userdto.getEmail()).nickname(userdto.getNickname()).accessToken(userdto.getAccessToken()).build();
                return reject_response;
            } else {
                // 유저가 있을때.
                SocialUserResponse userDataWithJwt = kakaoUserService.AppLogin(existingUser);
                userDataWithJwt.setValidate_check(true);
                log.info("validate user, login succeed ", existingUser);
                return userDataWithJwt;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }


    // localStorage 에 kakao email, nickname(기존) 이 저장되어 있음.
    // 이를 보여준 뒤, 사용자가 nickname(string) 추가 입력 / 프로필 사진 업로드(imageHandler 이용하여 url 반환된 것 전달)하도록 한 뒤 Post
    // POST METHOD parameter: String email, String nickname, String profileurl(null 가능), String accessToken
    @PostMapping("/kakao/signup")
    public SocialUserResponse kakaoSignup(@RequestBody UserRequestDTO userdto) {
        try {
            SocialUserResponse userResponseAfterLogin = kakaoUserService.kakaoSignup(userdto);
            log.info("user signup", userResponseAfterLogin.toString());
            return userResponseAfterLogin;
        } catch (Exception ex) {
            log.error("Error in kakaoSignup: ", ex);
            return null;
        }
    }

    // 클라이언트에서 헤더에 Jwt Token 을 달고 다니면서, logout Post 시에, 로그아웃 처리 ->
    // 서버 단에서는 해당 kakao access_token 을 만료시켜주는 것이고, 클라이언트에서는 localStorage 에서 Jwt 토큰을 만료시키기.
    @PostMapping("/kakao/logout")
    public String kakaoLogout(@RequestHeader(value="Authorization") String jwtToken, HttpServletRequest request) throws JsonProcessingException {
        try {
            kakaoUserService.kakaoLogout(jwtToken, request);
            return "logout work";
        } catch (Exception e) {
            log.error("An error occured while logging out the user", e);
            throw e;
        }
    }



    // 유저 정보 수정 Param: email(식별용), nickname(변경가능), profileUrl(아직 빈값),
    @PutMapping("/update")
    public Object kakaoUserUpdate(@RequestBody UserRequestDTO userdto) {
        try {
            SocialUserResponse response =  kakaoUserService.updateUser(userdto);
            return response;
        } catch (Exception e) {
            log.error("Error while updating user information", e);
            return "An error occurred while updating user information: " + e.getMessage();
        }
    }

    @PostMapping(value = "/image/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String profileImageUpload(MultipartFile file) throws IOException {
        return kakaoUserService.imageUpload(file);
    }


    @GetMapping("/test")
    public String testController() {
        return "Test site is working.";
    }

    // user 정보 받아오는 것 nickname 으로 조회 ( 다른 아티스트 조회시 )
    @GetMapping()
    public SocialUserResponse getUser(@RequestParam String nickname){
        SocialUserResponse userResponse = kakaoUserService.getUser(nickname);
        return userResponse;
    }



    @GetMapping("/sortByNickname")
    public ResponseEntity<?> sortByNickname(){
        List<SocialUserResponse> userSortByNickname = kakaoUserService.sortByName();
        return ResponseEntity.ok(userSortByNickname);
    }

}
