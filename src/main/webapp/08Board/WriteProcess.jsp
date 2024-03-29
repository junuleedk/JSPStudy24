<%@page import="model1.board.BoardDAO"%>
<%@page import="model1.board.BoardDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- 
글쓰기 페이지에 오랫동안 머물러 세션이 삭제되는 경우가 있으므로 처리 전에도 반드시 로그인을 확인해야한다. 
 -->    
<%@ include file="./IsLoggedIn.jsp"%>

<%

//유저가 입력한 값을 받는다. 
String title = request.getParameter("title");
String content = request.getParameter("content");

//폼값을 DTO에 저장한다. 
BoardDTO dto = new BoardDTO();
dto.setTitle(title);
dto.setContent(content);

//아이디의 경우에는 로그인 후 작성페이지에 진입할 수 있으므로 세션영역에 저장된 회원아이디를 얻어와서 저장한다.
dto.setId(session.getAttribute("UserId").toString());

//DAO 인스턴스를 생성하여 DB에 연결한다. 
BoardDAO dao = new BoardDAO(application);
//입력값이 저장된 DTO를 인수로 전달하여 insert쿼리를 실행한다. 
int iResult = dao.insertWrite(dto);
//DB 연결 해제
dao.close();

if(iResult == 1) {
	//글쓰기에 성공하면 목록으로 이동
	response.sendRedirect("List.jsp");
}
else {
	//실패하면 경고창을 띄우고 뒤로 이동
	JSFunction.alertBack("글쓰기에 실패하였습니다." , out);
}
%>