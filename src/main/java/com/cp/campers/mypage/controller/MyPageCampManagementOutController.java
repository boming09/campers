package com.cp.campers.mypage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPageCampManagementOutController {
	
	@GetMapping("/mypage_camp_management_out")
	public String myPage() {
		return "mypage/mypage_camp_management_out";
	}
}