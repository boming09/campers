package com.cp.campers.member.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.cp.campers.member.model.service.MemberService;
import com.cp.campers.member.model.vo.KakaoProfile;
import com.cp.campers.member.model.vo.Member;
import com.cp.campers.member.model.vo.OAuthToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/member")
public class MemberController {

	@Value("${cos.key}")
	private String cosKey;
	
	private MemberService memberService;
	
	private AuthenticationManager authenticationManager;
	
	 @Autowired
	   public MemberController(MemberService memberService,AuthenticationManager authenticationManager) {
	      this.memberService = memberService;
	      this.authenticationManager = authenticationManager;
	 }
	 
	 @RequestMapping("/login")
	 public String loginForm(@RequestParam(value = "error", required = false) String error,
             				 @RequestParam(value = "exception", required = false) String exception, Model model) {
		 model.addAttribute("error",error);
		 if(exception != null) {
			 if(exception.equals("exceptionNo1")) {
				 model.addAttribute("failureMessage","???????????? ?????? ??????????????????.");
			 }else if(exception.equals("exceptionNo2Count1")) {
				 model.addAttribute("failureMessage","???????????? ?????? ?????? 1???. 5??? ?????? ?????? ??? ????????? ???????????? ?????????.");
			 }else if(exception.equals("exceptionNo2Count2")) {
				 model.addAttribute("failureMessage","???????????? ?????? ?????? 2???. 5??? ?????? ?????? ??? ????????? ???????????? ?????????.");
			 }else if(exception.equals("exceptionNo2Count3")) {
				 model.addAttribute("failureMessage","???????????? ?????? ?????? 3???. 5??? ?????? ?????? ??? ????????? ???????????? ?????????.");
			 }else if(exception.equals("exceptionNo2Count4")) {
				 model.addAttribute("failureMessage","???????????? ?????? ?????? 4???. 5??? ?????? ?????? ??? ????????? ???????????? ?????????.");
			 }else if(exception.equals("exceptionNo2Count5")) {
				 model.addAttribute("failureMessage","???????????? ?????? ?????? 5???. ????????? ???????????? ?????????.");
			 }else if(exception.equals("exceptionNo2")) {
				 model.addAttribute("failureMessage","???????????? ?????? ????????? ??????????????? ???????????????. ???????????? ????????? ???????????? ???????????? ??? ??? ????????????.");
			 }else if(exception.equals("exceptionNo3")) {
				 model.addAttribute("failureMessage","????????? ????????? ?????? ????????? ???????????????.");
			 }else if(exception.equals("exceptionNo3And2")) {
				 model.addAttribute("failureMessage","????????? ???????????????.");
			 }else if(exception.equals("exceptionNo4")) {
				 model.addAttribute("failureMessage","?????? ???????????????. ???????????? ?????? ??? ?????? ??????????????????.");
			 }
		 }
		 return "member/login";
	 }
	 
	 @GetMapping("/login/kakao")
	 public String kakaoCallback(String code) { // Data??? ??????????????? ???????????? ??????
		 
		 // POST???????????? key=value ???????????? ?????? (??????????????????)
		 RestTemplate rt = new RestTemplate();
		 
		 // HttpHeader ???????????? ??????
		 HttpHeaders headers = new HttpHeaders();
		 headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		 
		 // HttpBody ???????????? ??????
		 MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		 params.add("grant_type","authorization_code");
		 params.add("client_id", "dad02eb2ea641fad46611f516cc28cbf");
		 params.add("redirect_uri", "http://localhost:8081/member/login/kakao");
		 params.add("code", code);
		 
		 // HttpHeader??? HttpBody??? ????????? ??????????????? ??????
		 HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
				 new HttpEntity<>(params,headers);
		 
		 // Http???????????? - Post ???????????? - ????????? response????????? ?????? ??????
		 ResponseEntity<String> response = rt.exchange(
				 "https://kauth.kakao.com/oauth/token",
				 HttpMethod.POST,
				 kakaoTokenRequest,
				 String.class
		 );
		 
		 // Gson, Json Simple, ObjectMapper
		 ObjectMapper ObjectMapper = new ObjectMapper();
		 OAuthToken oauthToken = null;
		 try {
			 oauthToken = ObjectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 System.out.println("????????? ????????? ?????? : "+oauthToken.getAccess_token());
		 
		 RestTemplate rt2 = new RestTemplate();
				 
		 // HttpHeader ???????????? ??????
		 HttpHeaders headers2 = new HttpHeaders();
		 headers2.add("Authorization", "Bearer "+oauthToken.getAccess_token());
		 headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		 
		 // HttpHeader??? HttpBody??? ????????? ??????????????? ??????
		 HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 =
				 new HttpEntity<>(headers2);
		 
		 // Http???????????? - Post ???????????? - ????????? response????????? ?????? ??????
		 ResponseEntity<String> response2 = rt2.exchange(
				 "https://kapi.kakao.com/v2/user/me",
				 HttpMethod.POST,
				 kakaoProfileRequest2,
				 String.class
		 );
		 
		 ObjectMapper ObjectMapper2 = new ObjectMapper();
		 KakaoProfile kakaoProfile = null;
		 try {
			kakaoProfile = ObjectMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 UUID garbagePassword = UUID.randomUUID();
		 
		 Member kakaoMember = new Member();
		 kakaoMember.setId(kakaoProfile.getKakao_account().getEmail().substring(0, 5)+"_"+kakaoProfile.getId());
		 kakaoMember.setPwd(cosKey);
		 kakaoMember.setEmail(kakaoProfile.getKakao_account().getEmail());
		 kakaoMember.setNickName(kakaoProfile.getKakao_account().getProfile().getNickname());
		 kakaoMember.setUserName(kakaoProfile.getKakao_account().getProfile().getNickname());	
		 kakaoMember.setProfilePath(kakaoProfile.getKakao_account().getProfile().getProfile_image_url());
		 kakaoMember.setPhone("null");
		 
		 Member originMember = memberService.findUserByUserId(kakaoMember.getId());
		 
		 if(originMember == null) {
			 System.out.println("??????????????? ????????????");
			 memberService.signUp(kakaoMember);
		 }
		 Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(kakaoMember.getId(), cosKey));
		 SecurityContextHolder.getContext().setAuthentication(authentication);
		 
		 return "redirect:/";
	 }
	 
	 @GetMapping("/signup")
	 public String signUpForm() {
		 return "member/signup";
	 }
	 
	 @PostMapping("/signup")
	 public String signUp(@Value("${custom.path.upload-images}") String uploadFilesPath, Model model, @RequestParam MultipartFile singleFile,
             HttpServletRequest request,Member member) {
	      
	      /* ????????? ????????? ?????? */
		 //String filePath = "C:\\Users\\calls\\git\\campers\\src\\main\\resources\\static\\resources\\images\\uploadFiles\\profileImg";
		 
		
		 String email1 = request.getParameter("email1");
		 String email2 = request.getParameter("email2");
		 String email3 = request.getParameter("email3");
		 
		 if(email2.equals("direct")) {
			 member.setEmail(email1+"@"+email3);
		 }else {
			 member.setEmail(email1+"@"+email2);
		 }
		
		 
		 String phone1 = request.getParameter("phone1");
		 String phone2 = request.getParameter("phone2");
		 String phone3 = request.getParameter("phone3");
		 
		 member.setPhone(phone1+""+phone2+""+phone3);
	      
	      /* ????????? ?????? */
		 log.info("size"+singleFile.getSize());
	      if(singleFile.getSize() != 0) {
	    	  String filePath = uploadFilesPath + "/profileImg";
	    	  
	    	  File mkdir = new File(filePath);
	    	  if(!mkdir.exists()) mkdir.mkdir();
	    	  
		      String originFileName = singleFile.getOriginalFilename();
	
		      String ext = originFileName.substring(originFileName.lastIndexOf("."));
		      
		      String savedName = UUID.randomUUID().toString().replace("-", "") + ext;
	      
	      try {
	         singleFile.transferTo(new File(filePath + "/" + savedName));
	         member.setProfilePath("/resources/images/uploadFiles/profileImg/"+savedName);
	         memberService.signUp(member);
	      } catch (IllegalStateException | IOException e) {
	         e.printStackTrace();
	      }
	      }else {
	    	  member.setProfilePath("null");
	    	  memberService.signUp(member);
	      }
	      
		 return "redirect:/";
	 }
	 
	 @PostMapping("/idFind")
	 @ResponseBody
	 public String selectUserId(Member member) {
		 log.info(member.getUserName(), member.getPhone());
		 
		 return memberService.selectUserId(member);
		 
	 }
	 @PostMapping("/pwdFind")
	 @ResponseBody
	 public String updateUserPwd(Member member) throws Exception {
		  log.info(member.getId(), member.getEmail());
		  
		  return memberService.findPwd(member);
	 }
	 
	 @ResponseBody
	 @GetMapping("/idCheck/{id}")
	 public String selectIdCheck(@PathVariable String id) {
		 int result = memberService.idCheck(id);
		 
		 String message ="";
		 if(result>0) {
			 message = "fail";
		 }else {
			 message = "success";
		 }
		return message;
		
		 
	 }
	
}
