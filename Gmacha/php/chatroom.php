<?php
// PHP 4.0.6以前の場合は$HTTP_SESSION_VARSを使用してください
session_start();
if (!isset($_SESSION['count'])) {
	$response = '不正なアクセス';
  print mb_convert_encoding($response, 'utf8', 'sjis-win');
} else {
$_SESSION['count']++;

//print $_SESSION['user_name'];
//print $_SESSION['count'];
$_POST['ID'];
$_POST['LATITUDE'];
$_POST['LONGITUDE'];
$_POST['ACCURACY'];
$_POST['ALTITUDE'];

switch ($_POST['ACTIONID']){
	case 1://入出
		gethistoricalchat();
	case 3://問い合わせ
		getnewcomechat();
		break;
	case 2://送信
		sendchat();
		break;
	case 4:
		//設定変更
		break;
	case 5:
		//ログアウト
		session_destroy();
		break;
}


function getchatmessage(){
	$LOGSTRING = "" . $_SESSION['message'];
	$_SESSION['message'] = $_POST['user'] . '<BR>' . $LOGSTRING;
}
}

function getnewcomechat(){
	
}

function gethistoricalchat(){
	//ここにMYSQLでhistoricalmessageを過去10個取得する
	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
	
	$query = sprintf("INSERT INTO chatroom values(0,'%s','0:20',0);",$_SESSION['user_name']." entered this room.");
	//$query = sprintf("INSERT INTO chatroom values(0,'AAAさんが入出しました','0:20',0);");
	mysql_query($query);
	
	
	$res = mysql_query("select user_name,message from chatroom,users where room_id = 0 and users.user_id = chatroom.user_id");
	//print $db;
	$_SESSION['historicalmes'] = '';
	$flag = 0;
	while ($item = mysql_fetch_array($res)) {
		if ($flag == 1)$_SESSION['historicalmes'] = $_SESSION['historicalmes'] . ',' . $item['user_name'] . ',' . $item['message'];
		else $_SESSION['historicalmes'] = $_SESSION['historicalmes'] . $item['user_name'] . ',' . $item['message'];
		$flag = 1;
	  }
	$_SESSION['historicalmes'] = $_SESSION['historicalmes'] . ';';

	$res = mysql_query("select user_name from users,chatmemlist where room_id = 0 and users.user_id = chatmemlist.user_id;");
	$flag = 0;
	while ($item = mysql_fetch_array($res)) {
		if ($flag == 1)$_SESSION['user_list'] = $_SESSION['user_list'] . ',' . $item['user_name'];
		else $_SESSION['user_list'] = $_SESSION['user_list'] . $item['user_name'];
		$flag = 1;
	  }
  $_SESSION['user_list'] = $_SESSION['user_list'];
	/* 1,ログ1,ログ2・・・; ユーザ名1, ユーザ名2, ・・・ 	 */

  $response = "1," . $_SESSION['historicalmes'] . $_SESSION['user_list'];
  $_SESSION['historicalmes'] = "";
	
  mysql_close();

  print $response;
  //print mb_convert_encoding($response, 'utf8', 'sjis-win');
}

function sendchat(){
	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
	
	$query = sprintf("INSERT INTO chatroom values(%d,'%s','0:20',0);",$_SESSION['user_id'],$_POST['MESSAGE']);
	mysql_query($query);
	mysql_close();
	
	//print mb_convert_encoding($response, 'utf8', 'sjis-win');
}

?>
