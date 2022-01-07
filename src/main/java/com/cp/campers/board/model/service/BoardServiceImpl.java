package com.cp.campers.board.model.service;

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
	public int deleteBoard(int bid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateBoard(Board board) {
		// TODO Auto-generated method stub
		return 0;
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
	public PrevBoard selectPrevBoard(int bid) {
		bid = bid-1;
		return boardMapper.selectPrevBoard(bid);
	}

	@Override
	public NextBoard selectNextBoard(int bid) {
		bid = bid+1;
		return boardMapper.selectNextBoard(bid);
	}

	@Override
	public List<BoardFileNo> selectBoardImgae(int bid) {
		return boardMapper.selectBoardImage(bid);
	}


}
