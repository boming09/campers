package com.cp.campers.mypage.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestParam;

import com.cp.campers.board.model.vo.Attachment;
import com.cp.campers.board.model.vo.Board;
import com.cp.campers.member.model.vo.Member;
import com.cp.campers.member.model.vo.UserImpl;
import com.cp.campers.mypage.model.vo.Camp;
import com.cp.campers.mypage.model.vo.CampBusinessType;
import com.cp.campers.mypage.model.vo.CampFacility;
import com.cp.campers.mypage.model.vo.Room;
import com.cp.campers.reservePayment.model.vo.ReserveInfo;

public interface MypageService{

	/* 회원 목록 */
	List<Member> findAllMember();
	
	List<Board> findAllBoard();
		
	/* 회원정보 수정 */
	Member changeInfoModify(Member member);

	/* 비밀번호 변경 */
	void changeInfoPwdModify(Member member);

	/* 닉네임 체크 */
	int nickNameCheck(String nickName);

	/* 회원 탈퇴 */
	int changeInfoMemberout(Member member);

	/* 내 게시판 목록 */
	Map<String, Object> selectMyBoardList(int writer, int page);

	/* 내 정보 목록 */
	Map<String, Object> selectMyMemberList(int userNo, int page);

	/* 캠프장 사진 등록 */
	void insertCampImage(Attachment attachment);

	/* 숙소 사진 등록 */
	void insertRoomImage(Attachment atta2);

	int selectCampNo();

	Map<String, Object> selectMyPageList(int writer);

	/* 캠핑장 등록 */
	void mypageCampEnrollment(Camp camp, List<String> btypeList, 
			List<String> ftypeList);


	/* 숙소 등록 List<Room> roomList */
	void mypageCampEnrollmentRoom(Room room, Attachment atta2);

	/* 내 숙소 찾기 */
	Map<String, Object> selectMyCampList(Camp camp, int userNo, int page);

	/* 사업자 예약내역 확인   */
	Map<String, Object> selectMyHostReserveList(int userNo, int page);

	/* 일반회원 예약내역 확인*/
	Map<String, Object> selectMyGuestReserveList(int userNo, int page);

	/* 프로필 사진 변경 */
	Member updateProfilePath(Member member);

	void deleteMember(Member member);

	void pwdUpdate(String userId, String pwd, String newPwd);

	/* 예약 취소 */
	int reserveCancle(int reserNo);

	void campImageInsert(Attachment attachment);

	String roomInsert(Room room);

	void roomImageInsert(Attachment atta2);
 
	Map<String, Object> selectwishCampList(int userNo, int page);

	Map<String, Object> selectMyWishCampList(int userNo, int page);

	String campLikeUp(Map<String, Object> param);

	String selectLikeCount(int campNo);

	String campLikeDown(Map<String, Object> param);

	String selectLikedCampNo(Map<String, Object> campNoAndUserNo);
	
	int reserveDelete(int campNo);

	/*
	Member pwdCheck(Member member);

	void pwdUpdate(String id, String hashedPwd);
	*/

	
}
