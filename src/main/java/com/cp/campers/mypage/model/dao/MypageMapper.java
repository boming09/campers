package com.cp.campers.mypage.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.cp.campers.admin.model.vo.PageInfo;
import com.cp.campers.board.model.vo.Attachment;
import com.cp.campers.board.model.vo.Board;
import com.cp.campers.camp.model.vo.ImageFile;
import com.cp.campers.member.model.vo.Member;
import com.cp.campers.mypage.model.vo.BusinessType;
import com.cp.campers.mypage.model.vo.Camp;
import com.cp.campers.mypage.model.vo.CampBusinessType;
import com.cp.campers.mypage.model.vo.CampFacility;
import com.cp.campers.mypage.model.vo.CampFile;
import com.cp.campers.mypage.model.vo.Room;
import com.cp.campers.mypage.model.vo.RoomFile;

@Mapper
public interface MypageMapper {
	
	// 회원 목록
	List<Member> findAllMember();
	
	// 게시물 조회
	List<Board> findAllBoard();
		
	/* 캠핑장 등록 */
	void insertCamp(Camp camp);

	/* 캠핑장 타입 등록 */
	void insertBusinessType(BusinessType businessType);

	// 회원수
	int getListCount();	

	// 사업장 타입 등록
	// Integer 체크박스 담기
	void insertCampBusinessType(Integer businessNo);

	// 사업장 시설 등록
	// Integer 체크박스 담기
	void insertCampFacility(Integer facilityNo);

	// 객실 등록
	void insertRoom(Room room);
	
	// 캠핑장 사진 파일 등록
	void insertCampFile(CampFile campFile);
	//void insertCampFile(Integer fileNo);
	
	// 숙소 사진 파일 등록
	void insertRoomFile(RoomFile roomFile);
	
	// 회원정보 수정
	int changeInfoModify(Member member);

	// 비밀번호 변경
	int changeInfoPwdModify(Member member);
	
	// 회원 탈퇴
	void changeInfoMemberout(Member member);

	/* 닉네임 체크 */
	int nickNameCheck(String nickName);

	List<Board> selectMyBoardList(Map<String, Object> param);

	List<Board> selectThumbnailList();

	int getListCountMyBoard(int writer);

	/* 캠프 사진 */
	void insertCampImage(Attachment attachment);

	/* 숙소 사진 */
	void insertRoomImage(Attachment atta2);

	void insertImageNo();

	void insertImageNo2();

	int selectCampNo();

	

	
	// public Map<String, Object> findAllMember();
}
