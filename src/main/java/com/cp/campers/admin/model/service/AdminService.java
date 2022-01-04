package com.cp.campers.admin.model.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.cp.campers.admin.model.vo.Camp;
import com.cp.campers.admin.model.vo.Search;
import com.cp.campers.member.model.vo.Member;

@Service
public interface AdminService {

	// 회원목록
	Map<String, Object> findAllMember(int page);
	
	// 회원수정
	int updateMember(Member member, int authorityCode);

	// 회원검색
	Map<String, Object> searchMember(int page, Search search);

	// 숙소목록
	Map<String, Object> findAllCamp(int page);
	
	// 숙소검색
	Map<String, Object> fineCampBySearch(int page, Search search);

	// 숙소신청상세
	Camp detailCamp(int campNo);

	// 숙소삭제
	int deleteCamp(int campNo);
}
