package com.cp.campers.mypage.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cp.campers.admin.model.service.AdminService;
import com.cp.campers.admin.model.vo.CampRecord;
import com.cp.campers.board.model.service.BoardService;
import com.cp.campers.board.model.vo.Attachment;
import com.cp.campers.board.model.vo.Board;
import com.cp.campers.camp.model.service.CampService;
import com.cp.campers.main.model.service.MainService;
import com.cp.campers.main.model.vo.Recommend;
import com.cp.campers.member.model.vo.Member;
import com.cp.campers.member.model.vo.UserImpl;
import com.cp.campers.mypage.model.service.MypageService;
import com.cp.campers.mypage.model.vo.Camp;
import com.cp.campers.mypage.model.vo.Room;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/mypage")
public class MyPageController {

	private MessageSource messageSource;
	private MypageService mypageService;
	private BoardService boardService;
	private AdminService adminService;
	private CampService campService;
	private MainService mainService;

	@Autowired
	public MyPageController(MessageSource messageSource, MypageService mypageService, BoardService boardService,
			AdminService adminService, CampService campService, MainService mainService) {
		this.messageSource = messageSource;
		this.mypageService = mypageService;
		this.boardService = boardService;
		this.adminService = adminService;
		this.campService = campService;
		this.mainService = mainService;
	}

	/* ?????? ?????? */
	/*
	 * ????????? admin password : admin1! ?????? user12345 password: user12345!
	 */
	@GetMapping("")
	public ModelAndView mypageMember(Model model, Member member, Board board, ModelAndView mv,
			@AuthenticationPrincipal UserImpl user, Camp camp) {

		int writer = user.getUserNo();
		int userNo = user.getUserNo();

		int page = 1;
		/* ??? ????????? ?????? */
		Map<String, Object> map = mypageService.selectMyBoardList(writer, page);
		/* ??? ?????? ?????? */
		Map<String, Object> map2 = mypageService.selectMyMemberList(userNo, page);
		/* ?????? ????????? ??????*/
		Map<String, Object> map3 = mypageService.selectMyWishCampList(userNo, page);
		/* ???????????? ????????? ?????? */
		Map<String, Object> map4 = mypageService.selectMyCampList(camp, userNo, page);
		
		// ???????????? ????????? ?????? ?????????
		List<Recommend> mainSlider = mainService.mainSlider();
		
		Date today = new Date();

		//log.info("date : " + today);

		mv.addObject("boardList", map.get("boardList"));
		mv.addObject("pi", map.get("pi"));
		mv.addObject("memberList", map2.get("memberList"));
		mv.addObject("campList", map.get("campList"));
		mv.addObject("mainSlider", mainSlider);
		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("memberList", map2.get("memberList"));
		model.addAttribute("wishCampList", map3.get("wishCampList"));
		model.addAttribute("mypageCampManagementList", map4.get("mypageCampManagementList"));
		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("standardDate", new Date());
		
		//model.addAttribute("thumbnailList", map.get("thumbnailList"));
		mv.setViewName("mypage/mypage");

		return mv;
	}

	/* ??????????????? ????????? ?????? ?????? */
	@PostMapping("")
	public String mypageMember(@Value("${custom.path.upload-images}") String uploadFilesPath,
			@RequestParam MultipartFile singleFile, Model model, Member member, @AuthenticationPrincipal UserImpl user,
			RedirectAttributes rttr, Locale locale) {

		member.setUserNo(user.getUserNo());
		// member.setProfilePath(user.getProfilePath());
		member.setId(user.getId());

		log.info("Post member : " + member.toString());
		log.info("Post user : " + user.toString());

		if (singleFile.getSize() != 0) {
			String filePath = uploadFilesPath + "/profileImg";

			File mkdir = new File(filePath);
			if (!mkdir.exists())
				mkdir.mkdir();

			String originFileName = singleFile.getOriginalFilename();

			String ext = originFileName.substring(originFileName.lastIndexOf("."));

			String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

			try {
				singleFile.transferTo(new File(filePath + "/" + savedName));
				member.setProfilePath("/resources/images/uploadFiles/profileImg/" + savedName);
				log.info("try member : " + member.toString());
				// mypageService.updateProfilePath(member);
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		} /*
			 * else { member.setProfilePath("null"); log.info("else member : " +
			 * member.toString()); mypageService.updateProfilePath(member); }
			 */
		mypageService.updateProfilePath(member);
		log.info("????????? member : " + member.toString());

		rttr.addFlashAttribute("successMessage", messageSource.getMessage("mypageProfile", null, locale));
		
		// ???????????????
		return "redirect:/mypage";
	} 

	/* ?????? ?????? */
	@GetMapping("/changinfo")
	public String changeInfo(@AuthenticationPrincipal UserImpl user, Model model) {
		int userNo = user.getUserNo();
		int page = 1;
		Map<String, Object> map2 = mypageService.selectMyMemberList(userNo, page);

		model.addAttribute("memberList", map2.get("memberList"));

		log.info(user.toString());
		log.info(map2.toString());
		model.addAttribute("user", user.getUserNo());
		return "mypage/changinfo";
	}

	/* ?????? ?????? */
	@GetMapping("/changinfo/changinfoMemberout")
	public String changeInfoMemberout() {
		return "mypage/changinfo_memberout";
	}

	/* ???????????? ?????? ??? */
	@PostMapping("/changinfo/changinfoMemberout")
	@ResponseBody
	public String changeInfoMemberout(@AuthenticationPrincipal UserImpl user, Model model, Member member,
			RedirectAttributes rttr, Locale locale) {
		
		member.setUserNo(user.getUserNo());
		member.setPwd(user.getPwd());
		log.info("@@@@@@@@member="+member);
				
		int result = mypageService.changeInfoMemberout(member);
			
		String message = "";
		if(result > 0) {
			message = "success";
		}else {
			message = "fail";
		}
		
		//log.info(member.getId(), member.getPwd());
		//log.info("???????????? member : " + member.toString());
	
		//mypageService.changeInfoMemberout(member);

		//rttr.addFlashAttribute("successMessage", messageSource.getMessage("changeInfoMemberout", null, locale));

		//return mypageService.deleteMember(member);
		return message;		
	}

	/* ????????? ?????? ?????? */
	@PostMapping("/nickName")
	@ResponseBody
	public String nickNameCheck(@RequestParam("nickName") String nickName) {
		log.info(nickName);
		int cnt = mypageService.nickNameCheck(nickName);

		if (cnt != 0) {
			return "fail";
		} else {
			return "success";
		}

	}

	/* ?????? ?????? ?????? */
	@GetMapping("/changinfo/changinfoModify")
	public String changeInfoModify(@AuthenticationPrincipal UserImpl user, Model model) {

		int userNo = user.getUserNo();
		int page = 1;
		Map<String, Object> map2 = mypageService.selectMyMemberList(userNo, page);

		model.addAttribute("memberList", map2.get("memberList"));

		log.info("user : " + user.toString());
		log.info("map2 : " + map2.toString());
		model.addAttribute("user", user.getUserNo());

		return "mypage/changinfo_modify";
	}

	/* ?????? ?????? ?????? ??? */
	@PostMapping("/changinfo/changinfoModify/update") /* String email, String phone, String nickName, */
	public String changeInfoModify(Model model, Member member, @AuthenticationPrincipal UserImpl user,
			RedirectAttributes rttr, Locale locale) {

		// ?????? ?????? ????????????
		member.setUserNo(user.getUserNo());
		member.setId(user.getId());
		log.info("update : " + user.toString());
		log.info("member : " + member.toString());

		member = mypageService.changeInfoModify(member);

		log.info(member.toString());

		// ?????????
		rttr.addFlashAttribute("successMessage", messageSource.getMessage("changeInfoModify", null, locale));

		user.setDetails(member);

		// ???????????????
		return "redirect:/mypage/changinfo";
	}

	/* ?????? ???????????? ?????? */
	@GetMapping("/changinfo/changinfoModify/changinfoPwdModify")
	public String changeInfoPwdModify() {
		return "mypage/changinfo_pwd_modify";
	}
	
	 /* ???????????? ?????? */	
	 @PostMapping("/pwdCheck")	 
	 @ResponseBody /*Member member, Model model*/
	 public String pwdCheck(Member member, @RequestBody String pwd,@AuthenticationPrincipal UserImpl user, HttpSession session) {	 
	 /*member??? id??? ???????????? userPwd??? ???????????? ?????? String userPwd =*/
		 log.info("???????????? ?????? ?????? ??????");
		 
		 String result = null;
		 BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		 
		 member.setId(user.getId());
		 member.setPwd(pwd);
		 log.info("member ???????????? ????????? : " + member.getPwd());
		 log.info("form ?????? ????????? ???????????? : " + pwd);
		 log.info("member ?????? : " + member.toString());
		 if(encoder.matches(pwd, member.getPwd())) {
			 result ="pwdConfirmOK";
		 } else {
			 result = "pwdConfinmNO";
		 }
		 return result;
	 }
	 
	 /* ???????????? update */
	 /*
	 @PostMapping("/pwdUpdate") 
	 public String pwdUpdate(Member member,
	 RedirectAttributes rttr, HttpSession session) {
		 
		 log.info("???????????? ?????? ??????");
		 
		 mypageService.pwdUpdate(member);
		 
		 Member updateMember = new Member();
		 updateMember.setPwd(member.getPwd());
		 	 
	     return "redirect:/mypage/changinfo"; 
	 }
	*/

	/* ?????? ???????????? ?????? ?????? ??? */
	@PostMapping("/changinfo/changinfoModify/changinfoPwdModify")
	public String changeInfoPwdModify(Member member, @AuthenticationPrincipal UserImpl user,
			HttpServletRequest request, RedirectAttributes rttr, Locale locale) {

		// ?????? ?????? ????????????
		member.setUserNo(user.getUserNo());
		member.setId(user.getId());
		member.setPwd(user.getPwd());
		
		String pwd = request.getParameter("pwd");
		String newPwd = request.getParameter("newPwd");
		String userId = user.getId();
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		
		log.info("?????????????????? member ?????? : " + member.toString());
		log.info("newPwd ?????? : " + newPwd.toString());		
		log.info("pwd : " + pwd.toString());
		log.info("newPwd : " + newPwd.toString());
		log.info("userId : " + userId.toString());
		
		
		mypageService.pwdUpdate(userId, pwd, passwordEncoder.encode(newPwd));

		rttr.addFlashAttribute("successMessage", messageSource.getMessage("changeInfoPwdModify", null, locale));

		//user.setDetails(member);

		return "redirect:/mypage/changinfo";
	}

	/* ????????? ?????? ????????? */
	@GetMapping("/mypageCampEnrollment")
	public String mypageCampEnrollmentForm() {

		return "mypage/mypage_camp_enrollment";
	}

	/* ?????? ?????? */
	@GetMapping("/mypageCampEnrollmentRoom")
	public String mypage_camp_enrollment_roomForm(Camp camp, @AuthenticationPrincipal UserImpl user, Model model) {

		int userNo = user.getUserNo();
		int page = 1;
		
		// log.info("page : " + page);
		// log.info("user : " + user.toString());
		
		Map<String, Object> map = mypageService.selectMyCampList(camp, userNo, page);

		model.addAttribute("campList", map.get("campList"));
		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("campImageList", map.get("campImageList"));

		log.info("map : " + map.toString());
		log.info("model : " + model.toString());
		
		return "mypage/mypage_camp_enrollment_room";
	}

	/* ?????? ?????? form */
	@PostMapping("/mypageCampEnrollmentRoom")
	public String mypage_camp_enrollment_room(Member member, Camp camp, Room room, @AuthenticationPrincipal UserImpl user,
			@Value("${custom.path.upload-images}") String uploadFilesPath, Model model,
			@RequestParam MultipartFile[] roomMultiFiles, HttpServletRequest request, RedirectAttributes rttr,
			Locale locale, int campNo) {
				
		/* insertRoom??? campNo??? room??? ????????? */			
		camp.setUserNo(user.getUserNo());
				
		log.info("------------------------------------------------------------------");
		
		log.info("?????? ????????? room : " + room.toString());
		
		/*
		 * List<String> values = value; for(int i = 0; i < value.size(); i++) {
		 * model.addAttribute("value", values); log.info(values.toString());
		 * log.info(value.toString()); }
		 */

		/* ?????? ????????? ????????? ?????? */
		String roomFilePath = uploadFilesPath + "/roomImg";

		/* ?????? ?????? ?????? ?????? ?????? ???????????? ?????? ?????? make directory */
		File mkdir3 = new File(roomFilePath);
		if (!mkdir3.exists())
			mkdir3.mkdirs();

		/* ?????? ?????? ????????? ?????? ?????? ?????? */
		List<Map<String, String>> files3 = new ArrayList<>();
		List<MultipartFile> roomMultiFileList = new ArrayList<>();

		Attachment atta2 = new Attachment();

		for (int i = 0; i < roomMultiFiles.length; i++) {
			if (roomMultiFiles[i].getSize() != 0) {
				roomMultiFileList.add(roomMultiFiles[i]);
			}
		}

		/* List<MultipartFile> ????????? */
		for (int i = 0; i < roomMultiFileList.size(); i++) {
			/* ????????? ?????? ?????? */
			String originFileName3 = roomMultiFileList.get(i).getOriginalFilename();
			String ext3 = originFileName3.substring(originFileName3.lastIndexOf("."));
			String savedName3 = UUID.randomUUID().toString().replace("-", "") + ext3;

			/* ????????? ?????? ?????? ?????? ??? ?????? */
			Map<String, String> file3 = new HashMap<>();
			file3.put("originFileName3", originFileName3);
			file3.put("savedName3", savedName3);
			file3.put("roomFilePath", roomFilePath);

			files3.add(file3);
		}

		/* ?????? ?????? */
		try {
			for (int i = 0; i < roomMultiFileList.size(); i++) {
				Map<String, String> file3 = files3.get(i);
				roomMultiFileList.get(i).transferTo(new File(file3.get("roomFilePath") + "/" + file3.get("savedName3")));

				atta2 = new Attachment();
				atta2.setFileName(file3.get("savedName3"));

				atta2.setFileOriginName(file3.get("originFileName3"));
				// atta2.setFileNewName(file.get("originFileNmae"));

				atta2.setFileRoute("/resources/images/uploadFiles/roomImg/");
				// atta2.setFileLevel(5);

				if (i == 0)
					atta2.setFileLevel(0);
				else
					atta2.setFileLevel(1);

				// mypageService.insertRoomImage(atta2);
			}

		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			/* ?????? ??? ?????? ??? ?????? ?????? */
			for (int i = 0; i < roomMultiFileList.size(); i++) {
				Map<String, String> file3 = files3.get(i);
				new File(file3.get(roomFilePath) + "\\" + file3.get("savedName3")).delete();
			}
		}

		// log.info("atta2 : " + atta2.toString());
		log.info("-----------------------------------------------------------------------------------------------");
		log.info("?????? ???????????? room : " + room.toString());

		/* ?????? ?????? */
		//camp.setRoom(room)//
		// room.setCampNo(camp.getCampNo());
		
		/* ??????, ?????? ??????, ?????? ??????, ?????? ????????? ?????? ?????? ???????????? */
		// log.info("camp : " + camp.toString());

		mypageService.mypageCampEnrollmentRoom(room, atta2);

		/* ?????? ?????? ????????? */
		rttr.addFlashAttribute("successMessage", messageSource.getMessage("insertCamp", null, locale));
		return "redirect:/mypage/mypageCampManagement";
	}
	
	/* ????????? ?????? ????????? */
	/* @AuthenticationPrincipal UserImpl user ?????? ?????? ???????????? */
	@PostMapping("/mypageCampEnrollment")
	public String mypageCampEnrollment(Camp camp, @AuthenticationPrincipal UserImpl user, @Value("${custom.path.upload-images}") String uploadFilesPath,
			Model model, @RequestParam MultipartFile singleFile, @RequestParam MultipartFile[] campMultiFiles,
			@RequestParam List<MultipartFile> roomMultiFiles, HttpServletRequest request, CampRecord campRecord,
			RedirectAttributes rttr, Locale locale) {

		
		// Camp = ????????? 
		// Room = ??????
		// singleFile = ??????????????????
		// campMultiFiles = ????????? ?????????
		// roomMultiFiles = ?????? ?????????
		// campRocord = ???????
		// rttr , locale = ???????????? ????????? ????????? ??????
		
		camp.setUserNo(user.getUserNo());

		/*-----------------------1. ????????? ?????? insert ?????? ?????? ----------------------------*/
		String filePath = uploadFilesPath + "businessImg";
		
		File mkdir = new File(filePath);
		
		if (!mkdir.exists())
			mkdir.mkdirs();
		
		String originFileName = singleFile.getOriginalFilename();
		String ext = originFileName.substring(originFileName.lastIndexOf("."));
		String savedName = UUID.randomUUID().toString().replace("-", "") + ext;
		
		try {
			singleFile.transferTo(new File(filePath + "\\" + savedName));
			camp.setCampPath("/resources/images/uploadFiles/businessImg/" + savedName);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		
		// ?????? campNo??? ???????????? ??????????????? ??????
		campRecord.setCampNo(camp.getCampNo());

		// ????????? ??????, ???????????? insert?????? ??????(????????????)
		String[] businessTypeCheck = request.getParameterValues("businessType");
		String[] facilityNoCheck = request.getParameterValues("facilityNo");
		
		List<String> btypeList = new ArrayList<>();
		for (String check : businessTypeCheck) {
			btypeList.add(check);
		}
		
		List<String> ftypeList = new ArrayList<>();
		for (String check2 : facilityNoCheck) {
			ftypeList.add(check2);
		}
		
		mypageService.mypageCampEnrollment(camp, btypeList, ftypeList);

		/* ---------------------------2. ????????? ?????? ????????? ------------------------------ */
		String campFilePath = uploadFilesPath + "/campImg";

		File mkdir2 = new File(campFilePath);
		if (!mkdir2.exists())
			mkdir2.mkdirs();

		List<Map<String, String>> files = new ArrayList<>();
		List<MultipartFile> campMultiFileList = new ArrayList<>();
		
		// ???????????? ????????? ??????????????? ???????????? ?????? List??? ??????
		for (int i = 0; i < campMultiFiles.length; i++) {
			if (campMultiFiles[i].getSize() != 0) {
				campMultiFileList.add(campMultiFiles[i]);
			}
		}

		for (int i = 0; i < campMultiFileList.size(); i++) {
			String originFileName2 = campMultiFileList.get(i).getOriginalFilename();
			String ext2 = originFileName2.substring(originFileName2.lastIndexOf("."));
			String savedName2 = UUID.randomUUID().toString().replace("-", "") + ext2;

			Map<String, String> file = new HashMap<>();
			file.put("originFileName2", originFileName2);
			file.put("savedName2", savedName2);
			file.put("campFilePath", campFilePath);
			files.add(file);
		}

		try {
			for (int i = 0; i < campMultiFileList.size(); i++) {

				
				Map<String, String> file = files.get(i);
				campMultiFileList.get(i).transferTo(new File(file.get("campFilePath") + "\\" + file.get("savedName2")));
				Attachment attachment = new Attachment();
				attachment = new Attachment();
				attachment.setFileName(file.get("savedName2"));
				attachment.setFileOriginName(file.get("originFileName2"));
				attachment.setFileRoute("/resources/images/uploadFiles/campImg/");

				if (i == 0)
					attachment.setFileLevel(0);
				else
					attachment.setFileLevel(1);
				
				mypageService.campImageInsert(attachment);
				
			}

		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			for (int i = 0; i < campMultiFileList.size(); i++) {
				Map<String, String> file = files.get(i);
				new File(file.get("campFilePath") + "\\" + file.get("savedName")).delete();
			}
		}

		/* ---------------------------5. ?????? ?????? insert ---------------------------- */
		
		String[] roomName = request.getParameterValues("roomName");
		String[] roomMember = request.getParameterValues("roomMember");
		String[] roomPrice = request.getParameterValues("roomPrice");
		String[] roomSize = request.getParameterValues("roomSize");
		String[] roomFloor = request.getParameterValues("roomFloor");
		String[] roomParking = request.getParameterValues("roomParking");
		String[] roomInfo = request.getParameterValues("roomInfo");
		String[] roomAmount = request.getParameterValues("roomAmount");
		String[] roomForm = request.getParameterValues("roomForm");
		
		List<String> roomNoList = new ArrayList<>();
		for(int i = 0 ; i < roomName.length; i++) {
			Room room = new Room();
			room.setRoomName(roomName[i]);
			room.setRoomMember(Integer.parseInt(roomMember[i]));
			room.setRoomPrice(Integer.parseInt(roomPrice[i]));
			room.setRoomSize(roomSize[i]);
			room.setRoomFloor(roomFloor[i]);
			room.setRoomParking(roomParking[i]);
			room.setRoomInfo(roomInfo[i]);
			room.setRoomAmount(Integer.parseInt(roomAmount[i]));
			room.setRoomForm(roomForm[i]);
			room.setCampNo(camp.getCampNo());
			log.info("room = "+room);
			roomNoList.add(mypageService.roomInsert(room));
		}
		
		
		/* ---------------------------4. ?????? ?????? ????????? ------------------------------ */

		String roomFilePath = uploadFilesPath + "/roomImg";
 
		File mkdir3 = new File(roomFilePath);
		if (!mkdir3.exists())
			mkdir3.mkdirs();

		List<Map<String, String>> files3 = new ArrayList<>();

		for (int i = 0; i < roomMultiFiles.size(); i++) {
			
			Map<String, String> file3 = new HashMap<>();
			if(roomMultiFiles.get(i).getSize() > 0) {
			String originFileName3 = roomMultiFiles.get(i).getOriginalFilename();
			String ext3 = originFileName3.substring(originFileName3.lastIndexOf("."));
			String savedName3 = UUID.randomUUID().toString().replace("-", "") + ext3;

			
			file3.put("originFileName3", originFileName3);
			file3.put("savedName3", savedName3);
			file3.put("roomFilePath", roomFilePath);
			file3.put("thumbnail", i+"");
			file3.put("index", i+1+"");
			file3.put("isEmpty", "N");
			files3.add(file3);
			}else {
			file3.put("thumbnail", i+"");
			file3.put("index", i+1+"");
			file3.put("isEmpty", "Y");
			files3.add(file3);
			}
		}
		try {
			int index = Integer.parseInt(roomNoList.get(0));
			
			for (int i = 0; i < roomMultiFiles.size(); i++) {
				
				Map<String, String> file3 = files3.get(i);
				
				if(file3.get("isEmpty") == "N") {
					
				roomMultiFiles.get(i).transferTo(new File(file3.get("roomFilePath") + "\\" + file3.get("savedName3")));

				Attachment atta2 = new Attachment();
				atta2.setFileName(file3.get("savedName3"));
				atta2.setFileOriginName(file3.get("originFileName3"));
				atta2.setFileRoute("/resources/images/uploadFiles/roomImg/");

				if (Integer.parseInt(file3.get("thumbnail")) % 5 == 0) {
					atta2.setFileLevel(0);
				}else {
					atta2.setFileLevel(1);
				}
					
				atta2.setRoomNo(index);
			
				mypageService.roomImageInsert(atta2);
				
				}
				if(Integer.parseInt(file3.get("index")) % 5 == 0) {
					index++;
				}
				
				
			}

		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			for (int i = 0; i < roomMultiFiles.size(); i++) {
				Map<String, String> file3 = files3.get(i);
				new File(file3.get(roomFilePath) + "\\" + file3.get("savedName3")).delete();
			}
		}
		rttr.addFlashAttribute("successMessage", messageSource.getMessage("insertCamp", null, locale));
	      return "redirect:/mypage";
	      
	   }

	/* ????????? ?????? */
	@GetMapping("/mypageCampManagementOut")
	public String mypageCampManagementOut() {
		return "mypage/mypage_camp_management_out";
	}

	/* ????????? ??????(????????????) */
	@GetMapping("/mypageCampManagement")
	public String mypageCampManagement(Camp camp, @AuthenticationPrincipal UserImpl user, Model model) {
		
		int userNo = user.getUserNo();
		int page = 1;
		
		log.info("page : " + page);
		log.info("user : " + user.toString());
		
		Map<String, Object> map = mypageService.selectMyCampList(camp, userNo, page);

		model.addAttribute("campList", map.get("campList"));
		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("campImageList", map.get("campImageList"));
		model.addAttribute("mypageCampManagementList", map.get("mypageCampManagementList"));

		log.info("map : " + map.toString());
		log.info("model : " + model.toString());
				
		return "mypage/mypage_camp_management";
	}

	/* ??????????????? ???????????? */
	@GetMapping("/mypageCategory")
	public String mypageCategory() {
		return "mypage/mypage_category";
	}

	/* ?????? ?????? ?????? */
	@GetMapping("/mypageGuestReserve")
	public String mypageGuestReserve(@AuthenticationPrincipal UserImpl user, Model model,Camp camp, CampRecord campRecord) {
	
		int page = 1;
		int userNo = user.getUserNo();
		log.info("userNo : {}", userNo);

		Map<String, Object> map = mypageService.selectMyGuestReserveList(userNo, page);

		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("reserveList", map.get("reserveList"));
		
		if (map.get("reserveList").toString().equals("[]")) {
			model.addAttribute("noResult", "?????? ?????? ????????? ????????????.");
		}

		return "mypage/mypage_guest_reserve";
	}
	
	/* ?????? ?????? ?????? + ?????????*/
	@GetMapping("/mypageGuestReservePage")
	public String mypageGuestReservePage(@AuthenticationPrincipal UserImpl user, int page, Model model) {
	
		int userNo = user.getUserNo();
		log.info("userNo : {}", userNo);

		Map<String, Object> map = mypageService.selectMyGuestReserveList(userNo, page);

		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("reserveList", map.get("reserveList"));
		
		if (map.get("reserveList").toString().equals("[]")) {
			model.addAttribute("noResult", "?????? ?????? ????????? ????????????.");
		}

		return "mypage/mypage_guest_reserve";
	}
	
	/* ?????? ?????? */
	@GetMapping("/reserveCancle")
	public String reserveCancle(int reserNo, RedirectAttributes rttr, Locale locale) {
		
		int result = mypageService.reserveCancle(reserNo);
		
		if(result > 0) {
			rttr.addFlashAttribute("successMessage", messageSource.getMessage("reserveCancle", null, locale));
		}
		
		return "redirect:/mypage/mypageGuestReserve";
	}
	
	/* ????????? ?????? */
	@GetMapping("/reserveDelete")
	public String reserveDelete(int campNo, RedirectAttributes rttr, Locale locale) {
		
		int result = mypageService.reserveDelete(campNo);
				
		return "redirect:/mypage/mypageCampManagement";
	}

	/* ????????? ?????? ?????? */
	@GetMapping("/mypageHostReserve")
	public String mypageHostReserve(Camp camp, Model model, @AuthenticationPrincipal UserImpl user) {

		int userNo = user.getUserNo();
		int page = 1;
		
				
		log.info("user : " + user.toString());
		
		// log.info("camp : " + camp.toString());
		// log.info("model : " + model.toString());

		Map<String, Object> map = mypageService.selectMyHostReserveList(userNo, page);

		model.addAttribute("campList", map.get("campList"));
		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("campImageList", map.get("campImageList"));
		model.addAttribute("reserveInfoList", map.get("reserveInfoList"));
		model.addAttribute("businessReservationList", map.get("businessReservationList"));

		
		
		// model.addAttribute("user", user.getUserNo());

		//log.info("map : " + map.toString());
		//log.info("model : " + model.toString());

		return"mypage/mypage_host_reserve";
	}

	/* ?????? ????????? */
	@GetMapping("/wishcamp")
	public String wishCamp(Camp camp, Model model, @AuthenticationPrincipal UserImpl user) {
		
		int userNo = user.getUserNo();
		int page = 1;
		
		Map<String, Object> map = mypageService.selectwishCampList(userNo, page);
		
		model.addAttribute("wishCampList", map.get("wishCampList"));
		model.addAttribute("pi", map.get("pi"));
		
		return "mypage/wishCamp";
	}
	
	/* ??? ?????? */
	@GetMapping("/likeDown/{campNo}")
	 @ResponseBody
	 public String campLikeDown(@PathVariable int campNo, @AuthenticationPrincipal UserImpl loginUser) {
		 Map<String, Object> param = new HashMap<>(); 
		 param.put("campNo", campNo);
		 param.put("userNo", loginUser.getUserNo()); 
		 
		 String count = mypageService.campLikeDown(param);
		 
		 return count;
	 }
		

}
