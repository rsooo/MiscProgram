<?php

$_POST['NAME'];
$_POST['PASS'];
$_POST['ACTIONID'];
$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
mysql_select_db("stress-free", $db);
//mysql_query("insert into users values(100,'test','test')");
$query = sprintf("SELECT user_id FROM users WHERE user_name='%s' AND user_pass='%s'",
    mysql_real_escape_string($_POST['NAME']),
    mysql_real_escape_string($_POST['PASS']));
    
$res = mysql_query($query);
//print $db;
if ($item = mysql_fetch_array($res)) {
	$_SESSION['user_id'] = $item['user_id'];
	mysql_close();

// PHP 4.0.6以前の場合は$HTTP_SESSION_VARSを使用してください
	session_start();
	print $_SESSION['count'];
	if (!isset($_SESSION['count'])) {
		print '0,1,0';//MYSQLが使えないから
		$_SESSION['count'] = 1;
 		$_SESSION['user_name'] = $_POST['NAME'];
  		$_SESSION['user_pass'] = $_POST['PASS'];
  //$_SESSION['user_id'] = 0;//MYSQLが使えないから
  //print $_POST['ACTIONID'];
	} else {
		print '0,1,0';
  		$_SESSION['count']++;
}
 }
 else{
 	$response = 'パスワードが誤っています';
  print mb_convert_encoding($response, 'utf8', 'sjis-win');
 }
function getchatmessage(){
	$LOGSTRING = "" . $_SESSION['message'];
	$_SESSION['message'] = $_POST['user'] . '<BR>' . $LOGSTRING;
}


?>
