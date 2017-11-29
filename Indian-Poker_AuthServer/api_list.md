Indian Poker 로그인/회원가입 서버 API 문서
========================================

/ (root)
--------
화면에 아주 멋진 글자가 뜹니다!<br>
**Hello JWT**

/api/auth/register (회원가입)
----------------------------
request: { username, password } (POST)
<br>
response: HTTP 응답 코드, { message, admin }
<br>
성공시 **registered successfully** (200), 실패시 **username exists** (409)가 message로 전송

/api/auth/login (로그인)
-----------------------
request: { username, password } (POST)
<br>
response: HTTP 응답 코드, { message }.tl

<br>
성공시 **logged in successfully** (200), 실패시 **login failed** (403)가 message로 전송

/api/auth/check (토큰 확인)
--------------------------
이후 작성
