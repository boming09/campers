package com.cp.campers.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import com.cp.campers.board.model.service.BoardService;
import com.cp.campers.board.model.vo.Attachment;
import com.cp.campers.board.model.vo.Board;
import com.cp.campers.board.model.vo.BoardFileNo;
import com.cp.campers.board.model.vo.Comment;
import com.cp.campers.board.model.vo.Search;
import com.cp.campers.member.model.vo.UserImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {

	private BoardService boardService;

	@Autowired
	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}

	@GetMapping("/list")
	public void boardList(Model model) {

		int page = 1;

		Map<String, Object> map = boardService.selectBoardList(page);

		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("thumbnailList", map.get("thumbnailList"));
	}

	@GetMapping("/listPage")
	public String boardListPage(Model model, @RequestParam int page) {

		Map<String, Object> map = boardService.selectBoardList(page);

		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("thumbnailList", map.get("thumbnailList"));

		return "board/list";
	}

	@GetMapping("/list/search")
	public String searchBoard(Search search, Model model) {

		int page = 1;

		Map<String, Object> map = boardService.searchBoardList(page, search);

		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("thumbnailList", map.get("thumbnailList"));

		if (map.get("boardList").toString().equals("[]")) {
			model.addAttribute("message", "'" + search.getSearchValue() + "' ??????????????? ????????????.");
		}

		return "board/list";
	}

	@GetMapping("/list/searchPage")
	public String searchBoardPage(Search search, Model model, int page) {

		Map<String, Object> map = boardService.searchBoardList(page, search);

		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("thumbnailList", map.get("thumbnailList"));

		return "board/list";
	}

	@GetMapping("/detail")
	public String detailList(Model model, int bid, HttpServletRequest request, HttpServletResponse response,
			@AuthenticationPrincipal UserImpl loginUser) {

		// ????????? ?????? ????????? ??????
		Cookie[] cookies = request.getCookies();

		String bcount = "";

		if (cookies != null && cookies.length > 0) {
			for (Cookie c : cookies) {

				log.info(c.getName());
				if (c.getName().equals("bcount")) {
					bcount = c.getValue();
				}
			}
		}
		if (bcount.indexOf("|" + bid + "|") == -1) {

			Cookie newBcount = new Cookie("bcount", bcount + "|" + bid + "|");

			response.addCookie(newBcount);

			int result = boardService.increaseCount(bid);

			if (result > 0) {
				log.info("????????? ?????? ??????");
			} else {
				log.info("????????? ?????? ??????");
			}
		}
		// board ?????? ????????????
		Board board = boardService.boardDetail(bid);

		// ??????????????? ????????????
		List<Comment> commentList = boardService.selectCommentList(bid);

		// ??????????????? ????????????
		List<BoardFileNo> boardFileNoList = boardService.selectBoardImgae(bid);

		// ????????? ???????????? view??? ????????????
		model.addAttribute(board);

		if (!boardFileNoList.toString().equals("[]")) {
			model.addAttribute(boardFileNoList);
		}

		model.addAttribute(commentList);

		// ????????? ??? ???????????? ????????? ???????????? bid??? userNo ????????????
		Map<String, Object> bidAndUserNo = new HashMap<>();
		bidAndUserNo.put("bid", bid);

		if (loginUser != null) {

			bidAndUserNo.put("userNo", loginUser.getUserNo());
			String likedBid = boardService.selectLikedBid(bidAndUserNo);
			model.addAttribute("likedBid", likedBid);
		}

		// ??? ???????????? ????????? ??????
		String likeCounts = boardService.selectLikeCount(bid);
		model.addAttribute("likeCounts", likeCounts);

		return "board/detail";

	}

	@PostMapping("/comment")
	public String commentWrite(Model model, int bid, String reply, Comment comment,
			@AuthenticationPrincipal UserImpl loginUser) {
		comment.setBid(bid);
		comment.setCWriter(loginUser.getUserNo());
		comment.setCContent(reply);

		boardService.insertComment(comment);

		return "redirect:/board/detail?bid=" + bid;
	}

	@PostMapping("/refcomment")
	public String refcommentWrite(Model model, Comment comment, @AuthenticationPrincipal UserImpl loginUser) {
		log.info("refWriter = " + comment.getRefWriter());
		log.info("bid = " + comment.getBid());
		log.info("comment=" + comment.getCContent());
		log.info("ref" + comment.getRefCid());

		comment.setCWriter(loginUser.getUserNo());

		boardService.insertRefComment(comment);

		return "redirect:/board/detail?bid=" + comment.getBid();
	}

	@GetMapping("/write")
	public void writeBoard() {
	}

	@GetMapping("/mycomment")
	public void myCommentList(Model model, @AuthenticationPrincipal UserImpl loginUser) {

		int writer = loginUser.getUserNo();

		int page = 1;

		Map<String, Object> map = boardService.selectMyCommentList(writer, page);

		log.info("commentMap" + map);

		model.addAttribute("myCommentList", map.get("myCommentList"));
		model.addAttribute("pi", map.get("pi"));

	}

	@GetMapping("/mycommentPage")
	public String myCommentListPage(Model model, @AuthenticationPrincipal UserImpl loginUser, int page) {

		int writer = loginUser.getUserNo();

		Map<String, Object> map = boardService.selectMyCommentList(writer, page);

		log.info("commentMap" + map);

		model.addAttribute("myCommentList", map.get("myCommentList"));
		model.addAttribute("pi", map.get("pi"));

		return "/board/mycomment";
	}

	@GetMapping("/myboard")
	public void myBoardList(Model model, @AuthenticationPrincipal UserImpl loginUser) {

		int writer = loginUser.getUserNo();

		int page = 1;

		Map<String, Object> map = boardService.selectMyBoardList(writer, page);

		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("thumbnailList", map.get("thumbnailList"));

	}

	@GetMapping("/myboardPage")
	public String myBoardPageList(Model model, @AuthenticationPrincipal UserImpl loginUser, int page) {

		int writer = loginUser.getUserNo();

		log.info("page=" + page);

		Map<String, Object> map = boardService.selectMyBoardList(writer, page);

		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("pi", map.get("pi"));
		model.addAttribute("thumbnailList", map.get("thumbnailList"));

		return "/board/myboard";
	}

	@GetMapping("/update")
	public String boardUpdate(Model model, int bid) {

		Board board = boardService.selectBoardUpdate(bid);
		log.info("board=" + board);
		List<BoardFileNo> boardFileNoList = boardService.selectBoardImgae(bid);
		log.info("File=" + boardFileNoList);

		if (board != null) {
			model.addAttribute(board);
		}
		if (!boardFileNoList.toString().equals("[]")) {
			model.addAttribute(boardFileNoList);
			log.info("size=" + boardFileNoList.size());
		}

		return "board/update";
	}

	@PostMapping("/update")
	public String boardUpdate(Model model, Board board, @Value("${custom.path.upload-images}") String uploadFilesPath,
			@RequestParam(required = false) List<MultipartFile> images, String[] deleteImgs, HttpServletRequest request,
			String[] changeName) {

		List<String> deleteList = new ArrayList<>();

		// setFilePath ?????? ?????? ??????
		String filePath = uploadFilesPath + "/boardImg";
		// ?????? ?????? ?????? ?????? ?????? ??????
		File mkdir = new File(filePath);
		if (!mkdir.exists())
			mkdir.mkdir();

		boolean haveFile = false;
		
		// ????????? ?????? ?????? ?????? list
		List<Map<String, String>> files = new ArrayList<>();

		for (int i = 0; i < images.size(); i++) {

			Map<String, String> file = new HashMap<>();
			if (images.get(i).getSize() > 0) {
				haveFile = true;

				// ????????? ?????? ??????
				String originFileName = images.get(i).getOriginalFilename();
				String ext = originFileName.substring(originFileName.lastIndexOf("."));
				String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

				// ????????? ?????? ?????? ?????? ??? ??????
				file.put("filePath", filePath);
				file.put("originFileName", originFileName);
				file.put("savedName", savedName);
				file.put("index", i + "");
				file.put("isNew", "Y");
			
				if (changeName.length >= i + 1) {
					file.put("deletedName", changeName[i]);
				}
				files.add(file);

			} else {
				file.put("index", i + "");
				file.put("isNew", "N");
				files.add(file);
			}

		}

		// ?????? ??????
		try {
			// title, content ?????? update
			boardService.updateBoard(board);
			
			int min = 0;

			for (int i = 0; i < images.size(); i++) {

				Map<String, String> file = files.get(i);

				if (file.get("isNew") == "Y") {

					// view?????? ????????? ?????? ??? ??????, ????????? ?????????, ????????? ???????????? ?????? ??????
					images.get(i).transferTo(new File(file.get("filePath") + "\\" + file.get("savedName")));

					// Attachment ????????? ????????? db??? ?????? ?????? setter
					Attachment attachment = new Attachment();
					attachment.setFileRoute("/resources/images/uploadFiles/boardImg/");
					attachment.setFileName(file.get("savedName"));
					attachment.setFileOriginName(file.get("originFileName"));
					attachment.setDeletedName(file.get("deletedName"));
					attachment.setFileLevel(1);
				

					int result = boardService.updateBoardImage(attachment, board.getBid());

					log.info("result=" + result);
					if (result > 0) {
						for (Map<String, String> photo : files) {
							if (photo.get("deletedName") != null) {
								File deletedFile = new File(filePath + photo.get("deletedName"));
								deletedFile.delete();
							}
						}
					}
				}
			}

		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			for (int i = 0; i < images.size(); i++) {
				Map<String, String> file = files.get(i);
				new File(file.get("filePath") + "\\" + file.get("savedName")).delete();
			}
		}
		if (deleteImgs != null) {
			for (String deleteImg : deleteImgs) {
				if (!deleteImg.equals("")) {
					deleteList.add(deleteImg);
				}
				log.info("????????? ?????????" + deleteImg);
			}
		}
	
		boardService.updateDeletedImage(deleteList);
		if(haveFile==true) {
			boardService.updateFileLevel(board.getBid());
		}
		

		return "redirect:/board/detail?bid=" + board.getBid();
	}

	@GetMapping("/delete")
	public String boardDelete(Model model, int bid) {

		boardService.deleteBoard(bid);

		return "redirect:/board/list";

	}

	@GetMapping("/comment/delete")
	public String commentDelete(int cid, int bid) {

		boardService.commentDelete(cid);

		return "redirect:/board/detail?bid=" + bid;
	}

	@PostMapping("/write")
	public String insertBoard(@Value("${custom.path.upload-images}") String uploadFilesPath, Model model,
			@RequestParam MultipartFile[] images, HttpServletRequest request, Board board,
			@AuthenticationPrincipal UserImpl loginUser) {

		board.setWriter(loginUser.getUserNo());

		String filePath = uploadFilesPath + "/boardImg";

		File mkdir = new File(filePath);
		if (!mkdir.exists())
			mkdir.mkdir();

		int bid = 0;
		List<Map<String, String>> files = new ArrayList<>();

		List<MultipartFile> imageList = new ArrayList<>();

		for (int i = 0; i < images.length; i++) {

			if (images[i].getSize() != 0) {
				imageList.add(images[i]);
			}
		}

		for (int i = 0; i < imageList.size(); i++) {

			String originFileName = imageList.get(i).getOriginalFilename();
			String ext = originFileName.substring(originFileName.lastIndexOf("."));
			String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

			Map<String, String> file = new HashMap<>();
			file.put("originFileName", originFileName);
			file.put("savedName", savedName);
			file.put("filePath", filePath);
			files.add(file);
		}

		try {

			boardService.insertBoard(board);
			for (int i = 0; i < imageList.size(); i++) {

				Map<String, String> file = files.get(i);
				imageList.get(i).transferTo(new File(file.get("filePath") + "\\" + file.get("savedName")));

				Attachment attachment = new Attachment();
				attachment.setFileName(file.get("savedName"));
				attachment.setFileOriginName(file.get("originFileName"));
				attachment.setFileRoute("/resources/images/uploadFiles/boardImg/");

				if (i == 0)
					attachment.setFileLevel(0);
				else
					attachment.setFileLevel(1);

				boardService.insertBoardImage(attachment);

			}
			bid = boardService.selectBid();

		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			for (int i = 0; i < imageList.size(); i++) {

				Map<String, String> file = files.get(i);
				new File(file.get("filePath") + "\\" + file.get("savedName")).delete();

			}
		}

		return "redirect:/board/detail?bid=" + bid;
	}

	@GetMapping("/likeUp/{bid}")
	@ResponseBody
	public String boardLikeUp(@PathVariable int bid, @AuthenticationPrincipal UserImpl loginUser) {

		Map<String, Object> param = new HashMap<>();
		param.put("bid", bid);
		param.put("userNo", loginUser.getUserNo());

		log.info("bid=" + bid);
		log.info("userNo" + loginUser.getUserNo());

		String count = boardService.boardLikeUp(param);

		log.info("count=" + count);

		return count;

	}

	@GetMapping("/likeDown/{bid}")
	@ResponseBody
	public String boardLikeDown(@PathVariable int bid, @AuthenticationPrincipal UserImpl loginUser) {
		Map<String, Object> param = new HashMap<>();
		param.put("bid", bid);
		param.put("userNo", loginUser.getUserNo());

		String count = boardService.boardLikeDown(param);

		return count;
	}

}
