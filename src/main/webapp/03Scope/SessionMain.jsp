<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
/*
session 영역
: 클라이언트가 웹 브라우저를 최초로 연 후 닫을 때까지 요청되는 모든 패이지는 session영역을 공유할 수 있다.
즉, session영역을 공유할 수 있다. 즉 세션(session)이란 클라이언트가 서버에 접속해 있는 상태 혹은 단위를 말한다.
*/

//리스트를 생성한 후 2개의 문자열을 저장한다. 
ArrayList<String> lists = new ArrayList<String>();
lists.add("리스트");
lists.add("컬렉션");
//세션영역에 리스트를 저장한다.
session.setAttribute("lists", lists);
%>
<html>
<head>
<title>session 영역</title>
</head>
<body>
	<h2>페이지 이동 후 session 영역의 속성 읽기</h2>
	<a href="SessionLocation.jsp">SessionLocation.jsp 바로가기</a>
</body>
</html>