package com.cp.campers.member.model.service;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cp.campers.member.model.dao.MemberMapper;
import com.cp.campers.member.model.vo.Authority;
import com.cp.campers.member.model.vo.Member;
import com.cp.campers.member.model.vo.MemberRole;
import com.cp.campers.member.model.vo.UserImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService{

	private MemberMapper memberMapper;
	
	@Autowired
	public MemberServiceImpl(MemberMapper memberMapper) {
		this.memberMapper = memberMapper;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Member member = memberMapper.findMemberById(username);
		
		if(member == null) member = new Member(); 
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		if(member.getMemberRoleList() != null) {
			List<MemberRole> roleList = member.getMemberRoleList();
			
			for(MemberRole role : roleList) {
				Authority authority = role.getAuthority();
				
				if(authority != null) {
					authorities.add(new SimpleGrantedAuthority(authority.getAuthorityName()));
					
				}
			}
		}
		
		UserImpl user = new UserImpl(member.getId(), member.getPwd(), authorities);
		user.setDetails(member);
		log.info("login : " + user);
		return user;
	}

	@Transactional
	@Override
	public void signUp(Member member) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		member.setPwd(passwordEncoder.encode(member.getPwd()));
		
		memberMapper.insertMember(member);
		
		MemberRole memberRole = new MemberRole();
		memberRole.setAuthorityCode(1);
		memberMapper.insertMemberRole(memberRole);
		
	}

	@Override
	public String selectUserId(Member member) {
		return memberMapper.selectUserId(member);
	}

	@Override
	public String sendEmail(Member vo, String div) throws Exception {
		String charSet = "utf-8";
		String hostSMTP = "smtp.naver.com"; //????????? ????????? smtp.naver.com
		String hostSMTPid = "";
		String hostSMTPpwd = "";

		// ????????? ?????? EMail, ??????, ??????
		String fromEmail = "chuhj1226@naver.com";
		String fromName = "Campers";
		String subject = "";
		String msg = "";
		
		if(div.equals("findpw")) {
			subject = "Campers ?????? ???????????? ?????????.";
			msg += "<div>";
			msg += "<h3>";
			msg += vo.getId() + "?????? ?????? ???????????? ?????????. ??????????????? ???????????? ???????????????.</h3>";
			msg += "<p>?????? ???????????? : ";
			msg += vo.getPwd() + "</p></div>";
		}
		String mail = vo.getEmail();
		
		try {
			HtmlEmail email = new HtmlEmail();
			email.setDebug(true);
			email.setCharset(charSet);
			email.setSSLOnConnect(true);
			email.setHostName(hostSMTP);
			email.setSmtpPort(587); //????????? ????????? 587

			email.setAuthentication(hostSMTPid, hostSMTPpwd);
			email.setStartTLSEnabled(true);
			email.addTo(mail, charSet);
			email.setFrom(fromEmail, fromName, charSet);
			email.setSubject(subject);
			email.setHtmlMsg(msg);
			email.send();
			
			return "Success";
		} catch (Exception e) {
			System.out.println("???????????? ?????? : " + e);
			return "Fail";
		}
		
	}
	
	
	@Override
	public String findPwd(Member member) throws Exception {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String result = null;
		
		Member user = memberMapper.readUser(member);
		
		if(user != null) {
			String tempPw = UUID.randomUUID().toString().replace("-", "");
			tempPw = tempPw.substring(0, 10);
			
			System.out.println("?????????????????? ??????"+tempPw);
			
			user.setPwd(tempPw);
			result = sendEmail(user,"findpw");
			
			String securePw = encoder.encode(user.getPwd());
			user.setPwd(securePw);
			
			memberMapper.modifyPw(user);
		}
		
		return result;
	}

	@Override
	public Member findUserByUserId(String userId) {
		return memberMapper.findMemberById(userId);
	}

	@Override
	public int idCheck(String id) {
		return memberMapper.idCheck(id);
	}

	@Override
	public Member loginFailure(String userId) {
		return memberMapper.findMemberById(userId);
	}

	@Override
	public void loginFailCount(String username) {
		memberMapper.loginFailCount(username);
		
	}

	@Override
	public int checkFailCount(String username) {
		return memberMapper.checkFailCount(username);
	}

	@Override
	public void disabledMember(String username) {
		memberMapper.disabledMember(username);
	}

	@Override
	public void resetFailCount(int userNo) {
		memberMapper.resetFailCount(userNo);
		
	}


}
