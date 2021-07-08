<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
        memberRepository memberRepository = MemberRepository.getInstance();
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);

        memberRepository.save(member);
%>
<html>
<head>
 <title>Title</title>
</head>
<body>

</body>
</html>