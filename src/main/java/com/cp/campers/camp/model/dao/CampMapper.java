package com.cp.campers.camp.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.cp.campers.camp.model.vo.Camp;
import com.cp.campers.camp.model.vo.Review;
import com.cp.campers.camp.model.vo.Room;

@Mapper
public interface CampMapper {

	// 숙소 조회
	Camp campDetail(int campNo);

	// 객실목록 조회
	List<Room> roomList(int campNo);

	// 리뷰 조회
	List<Review> reviewList(int campNo);
	
	// 객실 조회
	Room roomDetail(int roomNo);

	// 리뷰 삭제
	void reviewDelete(int rid);

}
