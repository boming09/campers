package com.cp.campers.mypage.model.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.cp.campers.mypage.model.vo.Camp;

public interface MypageService{

	void mypageCampEnrollment(Camp camp);
}
