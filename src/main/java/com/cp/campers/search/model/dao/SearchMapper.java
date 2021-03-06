package com.cp.campers.search.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.cp.campers.search.model.vo.SearchCamp;

@Mapper
public interface SearchMapper {
	
	/* 메인페이지에서 캠핑장 검색 조회 */
	List<SearchCamp> mainSearch(Map<String, Object> map);
	
	/* 메인 검색 총 개수 구하기 */
	int mainSearchCount(Map<String, Object> map);
	
	/* 캠핑장 검색페이지에서 전체 조회 및 페이징 처리 */
	List<SearchCamp> campAllSearch(int startRow, int endRow, int option);

	/* 게시글 총 개수 구하기 */
	int campListCount();
	
	// 조건 검색 총 개수 구하기
	int campFindCount(Map<String, Object> map);

	// 캠핑장 검색페이지에서 조건 검색 조회
	List<SearchCamp> campFindSearch(Map<String, Object> map);

	// 전체 조회에서 정렬 조회
	List<SearchCamp> selectOptionCamp(int optionVal);



	
}
