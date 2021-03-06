package com.cp.campers.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cp.campers.board.model.dao.BoardMapper;
import com.cp.campers.board.model.vo.Attachment;
import com.cp.campers.board.model.vo.Board;
import com.cp.campers.board.model.vo.BoardFileNo;
import com.cp.campers.board.model.vo.Comment;
import com.cp.campers.board.model.vo.NextBoard;
import com.cp.campers.board.model.vo.PageInfo;
import com.cp.campers.board.model.vo.PrevBoard;
import com.cp.campers.board.model.vo.Search;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoardServiceImpl implements BoardService {

	private BoardMapper boardMapper;

	@Autowired
	public BoardServiceImpl(BoardMapper boardMapper) {
		this.boardMapper = boardMapper;
	}

	@Override
	public int insertBoard(Board board) {
		return boardMapper.insertBoard(board);
	}

	@Override
	public void insertBoardImage(Attachment attachment) {
		boardMapper.insertBoardImage(attachment);
		boardMapper.insertImageNo();
	}

	@Override
	public Map<String, Object> selectBoardList(int page) {

		int listCount = boardMapper.getListCount();
		
		PageInfo pi = new PageInfo(page, listCount, 10, 7);
		
		pi.setStartRow(page, pi.getBoardLimit()); 
		pi.setEndRow(pi.getStartRow(),pi.getBoardLimit() );
		
		List<Board> boardList = boardMapper.selectBoardList(pi);
		
		List<Board> thumbnailList = boardMapper.selectThumbnailList();

		Map<String, Object> map = new HashMap<>();
		map.put("pi", pi); 
		map.put("boardList", boardList);
		map.put("thumbnailList", thumbnailList);

		return map;
	}

	@Override
	public void deleteBoard(int bid) {
		boardMapper.deleteBoard(bid);
	}

	@Override
	public void updateBoard(Board board) {
		boardMapper.updateBoard(board);
	}

	@Override
	public Board boardDetail(int bid) {
		
		Board board = boardMapper.boardDetail(bid);
		
		return board;
	}

	@Override
	public Map<String, Object> searchBoardList(int page, Search search) {
		
		int listCount = boardMapper.getListCountBySearch(search);
		
		PageInfo pi = new PageInfo(page, listCount, 10, 7);
		
		pi.setStartRow(page, pi.getBoardLimit());
		pi.setEndRow(pi.getStartRow(), pi.getBoardLimit());
		
		Map<String, Object> param = new HashMap<>();
		param.put("pi", pi);
		param.put("search", search);
		
		List<Board> boardList = boardMapper.searchBoardList(param);
		
		List<Board> thumbnailList = boardMapper.selectThumbnailList();
		
		Map<String, Object> map = new HashMap<>();
		map.put("pi", pi);
		map.put("boardList", boardList);
		map.put("thumbnailList", thumbnailList);
		
		return map;
	}

	@Override
	public void insertComment(Comment comment) {
		
		boardMapper.insertComment(comment);
		
	}

	@Override
	public List<Comment> selectCommentList(int bid) {
		return boardMapper.selectCommentList(bid);
	}

	@Override
	public void insertRefComment(Comment comment) {
		boardMapper.insertRefComment(comment);
		
	}

	@Override
	public List<BoardFileNo> selectBoardImgae(int bid) {
		return boardMapper.selectBoardImage(bid);
	}

	@Override
	public int selectBid() {
		return boardMapper.selectBid();
	}

	@Override
	public Board selectBoardUpdate(int bid) {
		return boardMapper.selectBoardUpdate(bid);
	}

	@Override
	public int increaseCount(int bid) {
		return boardMapper.increaseCount(bid);
	}

	@Override
	public Map<String, Object> selectMyCommentList(int writer, int page) {
		int listCount = boardMapper.getListCountMyComment(writer);
		
		PageInfo pi = new PageInfo(page,listCount, 10, 7);
		pi.setStartRow(page, pi.getBoardLimit());
		pi.setEndRow(pi.getStartRow(), pi.getBoardLimit());
		
		Map<String, Object> param = new HashMap<>();
		param.put("pi", pi);
		param.put("writer", writer);
		
		List<Comment> myCommentList = boardMapper.selectMyCommentList(param);
		
		Map<String, Object> map = new HashMap<>();
		map.put("myCommentList", myCommentList);
		map.put("pi", pi);
		
		return map;
	}

	@Override
	public Map<String, Object> selectMyBoardList(int writer, int page) {
        int listCount = boardMapper.getListCountMyBoard(writer);
        
        log.info(listCount+"");
		
		PageInfo pi = new PageInfo(page,listCount, 10, 7);
		pi.setStartRow(page, pi.getBoardLimit());
		pi.setEndRow(pi.getStartRow(), pi.getBoardLimit());

		Map<String, Object> param = new HashMap<>();
		param.put("pi", pi);
		param.put("writer", writer);
		
		List<Board> boardList = boardMapper.selectMyBoardList(param);
		
		List<Board> thumbnailList = boardMapper.selectThumbnailList();
		
		Map<String, Object> map = new HashMap<>();
		map.put("pi", pi);
		map.put("boardList", boardList);
		map.put("thumbnailList", thumbnailList);
		
		return map;
	}

	@Override
	public void commentDelete(int cid) {
		boardMapper.commentDelete(cid);
		
	}


	@Override
	public String boardLikeUp(Map<String, Object> param) {
		boardMapper.boardLikeUp(param);
		return boardMapper.selectLikeCount(param.get("bid"));
	}

	@Override
	public String selectLikedBid(Map<String, Object> bidAndUserNo) {
		
		return boardMapper.selectLikedBid(bidAndUserNo);
	}

	@Override
	public String selectLikeCount(int bid) {
		
		return boardMapper.selectLikeCount(bid);
	}

	@Override
	public String boardLikeDown(Map<String, Object> param) {
		boardMapper.boardLikeDown(param);
		return boardMapper.selectLikeCount(param.get("bid"));
	}

	@Override
	public int updateBoardImage(Attachment attachment, String bid) {
		int updatePhotoResult = 0;
		int insertPhotoResult = 0;
		
		int updateListCount = 0;
		int insertListCount = 0;
		
		Map<String, Object> param = new HashMap<>();
		param.put("bid", bid);
		param.put("attachment", attachment);
		
		if(attachment.getDeletedName() != null) {
			updatePhotoResult += boardMapper.updateBoardImage(param);
			updateListCount++;
		}else {
			insertPhotoResult += boardMapper.insertAddedPhoto(param);
			boardMapper.insertAddedImageNo(param);
			insertListCount++;
		}
		
		int result = 0;
		
		if(updatePhotoResult == updateListCount &&
				insertPhotoResult == insertListCount) {
			result = 1;
		}
		
		return result;
	}

	@Override
	public void updateDeletedImage(List<String> deleteList) {
		for(String deleteImg : deleteList) {
			boardMapper.deleteImgs(deleteImg);
			log.info("@@@@@@@@@"+deleteImg);
		}
		
	}

	@Override
	public void updateFileLevel(String bid) {
		int fileNo = boardMapper.selectFileNo(bid);
		boardMapper.updateFileLevel(fileNo);
	}


}
