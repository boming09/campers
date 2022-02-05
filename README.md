# campers
# [개요]
- 캠핑장 정보, 후기, 커뮤니티, 예약, 결제 서비스를 제공하는 사이트들이 분산되어 있어
사용자의 편의성이 떨어진다는 분석으로 캠핑장 서비스를 통합적으로 제공하는 원스톱 플랫폼입니다.

# [구현기능]
- 메인 : 로그인, 회원가입, 캠핑장검색

- 마이페이지 : -일반회원 :  회원정보 수정, 탈퇴, 찜한캠핑장 출력, 예약내역 출력(일반회원)
          - 사업자회원 : 회원정보 수정, 탈퇴, 예약내역 출력(사업자), 숙소등록, 객실등록

- 검색 : 캠핑장 조건 검색

- 숙소 : 1) 숙소 상세페이지에서 날짜와 인원 선택 시 예약 가능한 객실 조회
            2) 숙소 실이용자의 리뷰
            3) 해당 숙소의 객실 예약


- 고객센터 : FAQ

- 자유게시판 : 1)글쓰기, 글수정 및 삭제
                    2) 게시물 및 댓글 신고
           3) 내 게시글, 댓글 모아보기
           4) 댓글 대댓글 기능
                    5) 자유게시판 내 정렬 및 검색

- 관리자페이지
  1) 회원관리 : 회원의 권한과 활동상태 변경
  2) 신고관리 : 신고게시물/신고댓글 URL을 통한 삭제 및 유저 비활성화 변경
  3) 숙소관리 : 신규 숙소 등록, 등록된 숙소확인, 해지신청 숙소 해지
  4) 정산관리 : 결제 내역 확인

# [설계의 주안점]
- 다양한 카테고리를 선택하여 원하는 캠핑장을 검색할 수 있도록 한 기능과 회원의 권한에 따라서 기능도 다르게 접근할 수 있도록 개발


# [사용기술 및 개발환경]
- 운영체제 : Windows10
- 개발도구 : SpringToolSuite4
- 프레임워크 : Spring Boot, Mybatis, Maven, Bootstrap, Spring Security
- DBMS : Oracle DB - SQL Developer
- Server : Apache Tomcat 8.5
- 언어 : Java, HTML5, CSS3, Javascript, jQuery, ajax, Thymeleaf
- 협업툴 : Draw.io, ERDcloud, Figma, GitHub
- API : summernote, I’mport, kakao login/map

# [역할분담]
- 김광중 : 백엔드서버 개발, 프론트엔드 개발(마이페이지, 회원정보 수정, 탈퇴, 숙소등록)
- 김민주 : 백엔드서버 개발, 프론트엔드 개발(메인페이지, 캠핑장검색, 고객센터)
- 김보미 : 백엔드서버 개발, 프론트엔드 개발(관리자페이지, 숙소 상세 및 객실 상세페이지, 신고하기)
- 정온화 : 백엔드서버 개발, 프론트엔드 개발(예약 및 결제 페이지)
- 추현정 : 백엔드서버 개발, 프론트엔드 개발(로그인,회원가입, 자유게시판)

- [프로토타입](https://www.figma.com/proto/Sw19KPtob2xiHe3FllNFni/fianl-project-campers?node-id=0%3A1&scaling=min-zoom&page-id=0%3A1&starting-point-node-id=568%3A748)
- [ERD](https://www.erdcloud.com/d/4evHAK6mBFYoRnMDE)
