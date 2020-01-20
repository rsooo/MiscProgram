<?php
// PHP 4.0.6以前の場合は$HTTP_SESSION_VARSを使用してください

/** ユーザIDからDB参照してニックネームとる
 *  名前が見つからないときはNONAMEを返す
 */
function getNickName($userId){
	$query = sprintf("SELECT nickname FROM users WHERE user_id = '%s'", 		mysql_real_escape_string($userId));
	$res = mysql_query($query) or die(mysql_error());
	if ($item = mysql_fetch_array($res)) {
		return $item['nickname'];
	}
	putlog("getnickname exectuion error");
	return "NONAME";
	
}

function getSex($userId){
	$query = sprintf("SELECT sex FROM users WHERE user_id = '%s'",
 		mysql_real_escape_string($userId));
	$res = mysql_query($query) or die(mysql_error());
	if ($item = mysql_fetch_array($res)) {
		return $item['sex'];
	}
	putlog("getsex exectuion error");
	return "unknown"; //取得できない場合
}



function getdatetime(){
	return date("Y-m-d H-i-s");
}


function putlog($mes, $output = "debug.log"){
	$date = getdatetime();
	error_log($date . ": " . $mes . "\n" , 3, "/home/stress-free/www/log/$output");
}


?>
