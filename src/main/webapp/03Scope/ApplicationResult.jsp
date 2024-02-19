<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@ page import="common.Person" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<title>application 영역</title>
</head>
<body>
	<h2>application 영역의 속성 읽기</h2>
	<%
	//Object로 저장된 속성값을 Map타입으로 형변환한다.
	Map<String, Person> maps
		= (Map<String, Person>)application.getAttribute("maps");
	/*
	Map은 Key를 통해 Value를 저장하므로 keySet()으로 키값을 먼저 얻어온다. 
	*/
	Set<String> keys = maps.keySet();
	for(String key : keys) {
		Person person = maps.get(key);
		out.println(String.format
				("이름 : %s, 나이 : %d<br/>", person.getName(), person.getAge()));
	}
	/*
	웹 브라우저를 완전히 닫은 후 해당 페이지를 단독 실행하더라도 위 내용은 정상적으로 출력된다.
	톰캣을 재시작한 후  실행하면 저장된 내용은 소멸되므로 에러가 발생하게 된다. 
	*/
	%>
</body>
</html>