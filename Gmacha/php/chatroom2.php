<?php
// PHP 4.0.6以前の場合は$HTTP_SESSION_VARSを使用してください
require_once 'commonmethod.php';
require 'opendb.php';

header('Content-Type: text/html; charset=utf-8');
ob_start();

$SESSION_ERROR = "ERROR";

//mb_language("uni"); //<--追加  
//mb_internal_encoding("utf-8"); //<--追加  
//mb_http_input("auto"); //<--追加  
//mb_http_output("utf-8"); //<--追加  

session_start();
if (!isset($_SESSION['count'])) {
//	$response = '不正なアクセスtest';
//  print mb_convert_encoding($response, 'utf8', 'sjis-win');
	print $SESSION_ERROR;
} else {
$_SESSION['count']++;

/*
$_POST['ID'];
$_POST['LATITUDE'];
$_POST['LONGITUDE'];
$_POST['ACCURACY'];
$_POST['ALTITUDE'];
*/

	//基本的にDB開く
/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8"); //ここsjisじゃだめ？？→だめ
*/
	switch ($_POST['ACTIONID']){
//	case 1://入出
//		gethistoricalchat();
//	case 3://問い合わせ
//		getnewcomechat();
//		break;
	case 2://送信
	//	sendchat();
		break;
	case 4:
		//設定変更
		break;
	case 5:
		//ログアウト
		print "logout";
		deleteLoginData();
		session_destroy();
		break;
	case 6:
		//状態変更 -> NOINVITE
		changeNoinvite();
		break;
	case 7:
		//状態変更 -> WAIT
		changeWait();
		break;
	case 8:
		//状態変更 -> INVITED
		break;
	case 9:
		//状態変更 -> INVITING
		break;
	case 10:
		//状態変更 -> CHAT
		break;
	case 11:
		changeNopublishLocation();
		break;
	case 12:
		changeAdmitpublishLocation();
		break;
	case 20:
		//WAITポーリング
		checkInvite();
		break;
	case 21:
		//INVITEポーリング
		checkAccept();
		break;

	case 22:
		//CHATポーリング
		checkChat();
		break;
	case 29:
		//全体ポーリング
		polling();
		break;
	case 30:
		//招待クエリ
		inviteUser();
		break;
	
	case 31:
		//招待キャンセル
		inviteCancel();
		break;
	case 32:
		//近くの人を招待
		inviteNearUser();
		break;
	case 40:
		//チャットルーム入室・番号割り当て
		enterChatroom();
		break;
	case 41:
		//招待ACCEPTED。チャット入室
		enterChatroomAccepted();
		break;

	case 50:
		//メッセージ送信
		sendMessage();
		break;
	case 51:
		//退出メッセージ送信
		sendFinish();
		break;

	case 100:
		//デバッグ用
		test();
		break;
	case 101:
		enterChatroomAcceptedForTest();
		break;
	}
	mysql_close();

}




function getchatmessage(){
	$LOGSTRING = "" . $_SESSION['message'];
	$_SESSION['message'] = $_POST['user'] . '<BR>' . $LOGSTRING;
}


function changeNoinvite(){
	//print $query;
	$query = sprintf("UPDATE loginuserlist SET acceptinvite = 0 WHERE id = '%d'", $_SESSION['user_id']);
	mysql_query($query);
	print mb_convert_encoding("チャット招待を拒否に設定します", 'utf8', 'sjis-win');
}

function changeWait(){
/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
*/
	$query = sprintf("UPDATE loginuserlist SET acceptinvite = 1 WHERE id = '%d'", $_SESSION['user_id']);

	mysql_query($query);
	print mb_convert_encoding("チャット招待を許可に設定します", 'utf8', 'sjis-win');
//	mysql_close();	
}

/* 位置情報公開を禁止する
 * 
*/
function changeNopublishLocation(){
/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
*/
	$query = sprintf("UPDATE loginuserlist SET publishlocation = 0 WHERE id = '%d'", $_SESSION['user_id']);
	//print $query;

	mysql_query($query);
	print mb_convert_encoding("位置情報を非公開に設定します", 'utf8', 'sjis-win');
//	mysql_close();
}

//位置情報公開に設定する
function changeAdmitpublishLocation(){
/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
*/
	$query = sprintf("UPDATE loginuserlist SET publishlocation = 1 WHERE id = '%d'", $_SESSION['user_id']);
	mysql_query($query);
	print mb_convert_encoding("位置情報を公開に設定します", 'utf8', 'sjis-win');
//	mysql_close();
}



function checkChat(){
	$sequence = $_SESSION['sequence'];
	$chatroomNo = $_SESSION['chatroom'];

/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES sjis");
*/

///	mysql_query("SET NAMES sjis");


	$query = sprintf("SELECT message, name, sequence FROM chatroom WHERE sequence > %s AND room_id = %d ORDER BY sequence", $sequence, $chatroomNo);
//	print $query;
	$res = mysql_query($query) or die(mysql_error());
	
	$lastsequence = $sequence;
	while ($item = mysql_fetch_array($res)) {
		//name,message#の順に出力
		///print (mb_convert_encoding($item['name'] . "," .  $item['message'] . "#", 'utf8', 'sjis-win'));
		print ($item['name'] . "," .  $item['message'] . "#");
		//putlog($item['name'] . "," .  $item['message'] . "#");
		$lastsequence = $item['sequence'];
	}
	//最後に受信したシーケンス番号を更新
	$_SESSION['sequence'] = $lastsequence;
//	print "item!!!" . $lastsequence;
//	mysql_close();

}

/*ユーザを招待
 *招待するユーザの状態がWAITなら招待
 *戻り値
 * 0:WAIT(招待実行)
 * 1:CHAT
 * 2:INVITING
 * 3:INVITED
 * -1: NOT FOUND
 */
function inviteUser(){
	$invitingId = $_POST['INVITINGID'];
	$_SESSION['inviting_id'] = $invitingId;

	$ownId = $_SESSION['user_id'];
/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
*/	
	$preQuery = sprintf("SELECT status FROM loginuserlist WHERE id = '%d'", $invitingId);
	$res = mysql_query($preQuery) or die(myaql_error());
	if ($item = mysql_fetch_array($res)) {
		if($item['status'] === "CHAT"){
			print "1";
			return;
		}else if($item['status'] === "INVITING"){
			print "2";
			return;
		}else if($item['status'] === "INVITED"){
			print "3";
			return;
		}
		//WAITなら続行
		print "0";
	}else{
		print "-1";
		return;
	}

	$query = sprintf("UPDATE loginuserlist SET status = 'INVITED' WHERE id = '%d'", $invitingId);
	$query2 = sprintf("UPDATE loginuserlist SET status = 'INVITING' WHERE id = '%d'", $ownId);
	mysql_query($query) or die(myaql_error());
	mysql_query($query2) or die(myaql_error());
//   mysql_close();
}
	
//近くにいるユーザを検索
//使用POSTデータ
//LATITUDE:緯度
//LONGITUDE:経度
//戻り値：近かったユーザのID
//　　　　-1は近くにおらず
function inviteNearUser(){
	$userId = $_SESSION['user_id'];
	$my_latitude = $_POST['LATITUDE'];
	$my_longitude = $_POST['LONGITUDE'];
	$selectedSex = $_POST['SELECTEDSEX']; //性別指定
	$selectedRange = $_POST['SELECTEDRANGE']; //距離指定

	putlog($selectedSex);
	//putlog($selectedRange);
	
	global $db;	

	$selectedSexQuery = "";
	if($selectedSex === "male"){
		$selectedSexQuery = "AND sex = 'male'";
	}
	else if($selectedSex === "female"){
		$selectedSexQuery = "AND sex = 'female'";
	}


	//putlog($my_latitude . ":" . $my_longitude);
	
/* $db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
*/
	//周囲の招待可能な人を絞るクエリ
	$query = sprintf("SELECT id, username, latitude, longitude FROM loginuserlist, users WHERE abs(latitude - %d) * 0.111 < %d 
    AND abs(longitude - %d) * 0.091 < %d
    AND status = 'WAIT'
    AND acceptinvite = 1
	AND NOT id = %d
    AND users.user_id = loginuserlist.id 
    %s
    ", $my_latitude, $selectedRange, $my_longitude, $selectedRange, $userId, $selectedSexQuery);
	putlog($query);
	
	$res = execSQL($query, $db);
	$nearestUserId = -1; //一番近かったユーザ
	$nearestUsername = "None"; //一番近かったユーザの名前
	$minDistance = PHP_INT_SIZE; //最大の整数値(ほんとはfloatがいい)
	while ($item = mysql_fetch_array($res)) {
		$distance = calcDistance($item['latitude'], $my_latitude, $item['longitude'], $my_longitude);
		if($distance < $minDistance){
			$minDistance = $distance;
			$nearestUserId = $item['id'];
			$nearestUsername = $item['username'];
		}
	}	
	
//	mysql_close();
	print $nearestUserId . "#" . $nearestUsername;
}

//2点の距離を求める
function calcDistance($lati1, $lati2, $longi1, $longi2){
	return sqrt(($lati1 - $lati2) * ($lati1 - $lati2) + 
    ($longi1 - $longi2) * ($longi1 - $longi2)
	);
}


//新しいチャットルームNoを返す
//このアルゴリズム後でなんとかする
function getNewChatroomNo(){
	//$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
/*	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
*/
	$query = sprintf("SELECT chatroom FROM loginuserlist ORDER BY chatroom DESC");

	$res = mysql_query($query) or die(myaql_error());
	if ($item = mysql_fetch_array($res)) {
		$val= $item['chatroom'];
//		print $val + 1;
		return ($val + 1);
	}	
	return -1;
}


//招待をキャンセル
function inviteCancel(){
	$invitingId = $_POST['INVITINGID'];
	$ownId = $_SESSION['user_id'];
/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
*/
	$query = sprintf("UPDATE loginuserlist SET status = 'WAIT' WHERE id = '%d'", $invitingId);
	$query2 = sprintf("UPDATE loginuserlist SET status = 'WAIT' WHERE id = '%d'", $ownId);
	mysql_query($query) or die(myaql_error());
	mysql_query($query2) or die(myaql_error());
//    mysql_close();		
}

function enterChatroom(){
	$userId = $_SESSION['user_id'];
	$username = $_SESSION['user_name'];
	//error_log($username, 3, '/home/stress-free/www/log/debug.log');
	
/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
*/
	mysql_query("SET NAMES utf8");

	$checkQuery = sprintf("SELECT status FROM loginuserlist WHERE id = '%s'", mysql_real_escape_string($userId)); 	
	$res = mysql_query($checkQuery) or die(mysql_error());
	if ($item = mysql_fetch_array($res)) {
		if($item['status'] !== 'INVITED'){
			print("-1"); //招待キャンセルされている
			return;
		}		
	}else{
		//ユーザが存在していなかった
		die("status error");
	}

	$chatroomNo = getNewChatroomNo();
	if(chatroomNo == -1){
		die("chatroomNo error");
	}
	
	//過去のチャットはここで消す
	$_query = sprintf("DELETE FROM chatroom WHERE room_id = %d", $chatroomNo);
	//error_log($_query, 3, '/home/stress-free/www/log/debug.log');
	
	mysql_query($_query) or die(mysql_error());

	$date = getdatetime();
	$nickName = getNickName($userId);	
	//今は最初シーケンス番号1に固定している
	$enter_mes = $nickName . mb_convert_encoding(" が入室しました。", 'utf8', 'sjis-win');
//	$enter_mes = "hogehoge";
	//putlog($enter_mes);
	$enter_query = sprintf("INSERT INTO chatroom VALUES(%s, '%s', '%s',  %s, 1, 'SYSTEM')", 
	mysql_real_escape_string($userId),
	mysql_real_escape_string($enter_mes),
 	$date, $chatroomNo);

	//putlog($enter_query);
	

	mysql_query($enter_query) or die(mysql_error());
	$query = sprintf("UPDATE loginuserlist SET status = 'CHAT', chatroom = %d WHERE id = '%s'", $chatroomNo, $userId);	
	mysql_query($query) or die(mysql_error());
//	mysql_close();
	$_SESSION['chatroom'] = $chatroomNo;
	$_SESSION['sequence'] = 0;
	print("0"); //入室成功
}

//招待した側の入室
function enterChatroomAccepted(){
	$userId = $_SESSION['user_id'];
	$username = $_SESSION['user_name'];
	$invitingId = $_POST['INVITINGID'];
/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
*/
	$query = sprintf("SELECT chatroom FROM loginuserlist WHERE id = '%s'",  $invitingId);	
	$res = mysql_query($query) or die(myaql_error());
	if ($item = mysql_fetch_array($res)) {
		$chatroomNo = $item['chatroom'];

		$nickName = getNickName($userId);	
		$enter_mes = $nickName . mb_convert_encoding(" が入室しました。", 'utf8', 'sjis-win');
	$enter_query = sprintf("INSERT INTO chatroom VALUES(%s, '%s', '%s',  %s, 1, 'SYSTEM')", 
	mysql_real_escape_string($userId),
	mysql_real_escape_string($enter_mes),
 	$date, $chatroomNo);
	mysql_query($enter_query) or die(mysql_error());

		$_SESSION['sequence'] = 0;
		$query2 = sprintf("UPDATE loginuserlist SET status = 'CHAT', chatroom = $chatroomNo WHERE id = '%s'",  $userId);			
		mysql_query($query2) or die(myaql_error());
		$_SESSION['chatroom'] = $chatroomNo;
	}else{
		die("inviting id error");
	}
//	mysql_close();

}

//招待した側の入室(test用)
function enterChatroomAcceptedForTest(){
	$userId = $_SESSION['user_id'];
	$username = $_SESSION['user_name'];
	//error_log($userId . ":" . $username , 3, '/home/stress-free/www/log/debug.log');
	
	//$invitingId = $_POST['INVITINGID'];
/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
*/
	global $db ;
/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
*/
//	$query = sprintf("SELECT chatroom FROM loginuserlist WHERE id = '%s'",  $invitingId);	
	$chatroomNo = 100; //テスト用チャットルーム100番
	//$res = mysql_query($query) or die(myaql_error());
//	if ($item = mysql_fetch_array($res)) {
//		$chatroomNo = $item['chatroom'];
		$enter_mes = mb_convert_encoding($username . "が入室しました。", 'utf8', 'sjis-win');
	$enter_query = sprintf("INSERT INTO chatroom VALUES(%s, '%s', '%s',  %s, 1, 'SYSTEM')", 
	mysql_real_escape_string($userId),
	mysql_real_escape_string($enter_mes),
 	$date, $chatroomNo);
	mysql_query($enter_query) or die(mysql_error());

		$_SESSION['sequence'] = 0;
		$query2 = sprintf("UPDATE loginuserlist SET status = 'CHAT', chatroom = $chatroomNo WHERE id = '%s'",  $userId);			
		mysql_query($query2) or die(myaql_error());
		$_SESSION['chatroom'] = $chatroomNo;
//	}else{
//		die("inviting id error");
//	}
//	mysql_close();

}


//ポーリング
/**
 * POST: UPDATETIME "1"なら最終アクセス時刻更新
 */
function polling(){
	$userid = $_SESSION['user_id'];
	$updateTimeFlag = $_POST['UPDATETIME'];

//	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
//	mysql_select_db("stress-free", $db);
//	mysql_query("SET NAMES utf8");
	
	$query = sprintf("SELECT status FROM loginuserlist WHERE id = '%s'", $userid);
	$res = mysql_query($query) or die(mysql_error());
	if ($item = mysql_fetch_array($res)) {
		$val= $item['status'];
//		print $val;
	}
//	else{
//		print "ERROR#status error";
//	}
//	mysql_close();
	
	//valの値に応じてポーリング
	if($val === "WAIT"){
		//ステータス出力
		print "WAIT#";
//		checkInvite();
	}else if($val === "INVITED"){
		print "INVITED#";
	}else if($val === "INVITING"){
		print "INVITING#";
		checkAccept();
	}else if($val === "CHAT"){
		print "CHAT#";

	}else{
		print "ERROR#";
	}
	
	if($updateTimeFlag === "1"){
		updateAccessTime();
	}
			
}

//InviteしたユーザがAcceptしたかどうかをチェック
//もしくは拒否されたか？？
function checkAccept(){
	//$invitingId = $_POST['INVITINGID'];
	$invitingId = $_SESSION['inviting_id'];
	/*$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
	*/
	$query = sprintf("SELECT status FROM loginuserlist WHERE id = '%s'", $invitingId);	
	$res = mysql_query($query) or die(mysql_error());
	if ($item = mysql_fetch_array($res)) {
		$val= $item['status'];
		if($val === "CHAT"){
			//招待は完了
			unset($_SESSION['inviting_id']);
			print "CHAT";
		}else{
			print $val;
		}
	}else{
		print "inviting error";
	}	
	//mysql_close();
	
}



function sendMessage(){
	$userid = $_SESSION['user_id'];
	$mes = mb_convert_encoding($_POST['MESSAGE'] ,'utf8', 'sjis-win');
//	$mes = $_POST['MESSAGE'];
	$name = $_POST['NAME'];
	$chatroomNo = $_SESSION['chatroom'];
	$username = $_SESSION['user_name'];
	$nickName = getNickName($userid);	
/*$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
	*/
	$query = sprintf("SELECT MAX(sequence) FROM chatroom where room_id = %d", $chatroomNo);
//	error_log($query, 3, '/home/stress-free/www/log/debug.log');
	
	$res = mysql_query($query) or die(mysql_error());
//	error_log($res, 3, '/home/stress-free/www/log/debug.log');

	
	$sequence = 1;
	//シーケンス番号の最大値を取得
	if ($item = mysql_fetch_array($res)) {
		$sequence= $item['MAX(sequence)'] + 1;
	}
	$date = getdatetime();	
	$query2 = sprintf("INSERT INTO chatroom values(%s,'%s','%s',%s, %s, '%s')", mysql_real_escape_string($userid),
      mysql_real_escape_string($mes),
      mysql_real_escape_string($date),
      mysql_real_escape_string($chatroomNo),
      mysql_real_escape_string($sequence),
      mysql_real_escape_string($nickName));

	//print $query2;
	$res = mysql_query($query2) or die(mysql_error());


	//mysql_close();
	checkChat();
	//print mb_convert_encoding($response, );
}

function sendFinish(){
	if(!isset($_SESSION['user_id']) || !isset($_SESSION['chatroom']) || !isset($_SESSION['user_name']) ){
	print "no session data";
	return -1;
	}
	$userid = $_SESSION['user_id'];
	$chatroomNo = $_SESSION['chatroom'];
	$username = $_SESSION['user_name'];

/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
	mysql_query("SET NAMES utf8");
*/
//	$query2 = sprintf("INSERT INTO chatroom values(%s,'%s','0:20',%s, %s, '%s')", $userid, $mes, $chatroomNo, $sequence, $username);

	//シーケンス番号の最大値取得
	$res = mysql_query("SELECT MAX(sequence) FROM chatroom") or die(mysql_error());
	$sequence = 1;
	//シーケンス番号の最大値を取得
	if ($item = mysql_fetch_array($res)) {
		$sequence= $item['MAX(sequence)'] + 1;
	}	
	$date = getdatetime();
	$nickName = getNickName($userid);	
	$exit_mes = $nickName . mb_convert_encoding("が退室しました。", 'utf8', 'sjis-win');	
	$exit_mes_query = sprintf("INSERT INTO chatroom VALUES(%s, '%s', '%s',  %s, %s, 'SYSTEM')", $userid, $exit_mes, $date, $chatroomNo, $sequence);
	print $exit_mes_query;
	mysql_query($exit_mes_query) or die(mysql_error());
	
	$query = sprintf("UPDATE loginuserlist SET status = 'WAIT', chatroom = 0 WHERE id = '%s'", $userid);
	$res = mysql_query($query) or die(mysql_error());
//	mysql_close();
	unset( $_SESSION['chatroom'] );
}

function deleteLoginData(){
	$userName =	$_SESSION['user_name'];
	$userId = $_SESSION['user_id'];

/*	$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
	mysql_select_db("stress-free", $db);
*/
	
	$query = sprintf("DELETE FROM loginuserlist WHERE id = %d;", 
	        $userId
		);
	$res = mysql_query($query) or die(mysql_error());
	

	//guestの場合はusersテーブルからも消す
	putlog($userName);
	if($userName == "guest"){
		$del_query = sprintf("DELETE FROM users WHERE user_id = %d;", 
	        $userId
		);
		putlog($del_query);
		$rett = mysql_query($del_query) or die(mysql_error());
		putlog($rett);
		//ugokann

	}

	//print $query;
//	print mysql_errno($db). ": " .mysql_error($db);    			
//	mysql_close();

}

function test(){
//	$e = 'ほげ';
	$e = $_POST['test'];
	//print $_POST['test'];
	$fp = fopen("test.txt", "w");
	fwrite($fp, $e);
	fclose($fp);
	//error_log($e, 3, '/home/stress-free/www/log/test.log');
	//error_log(mb_convert_encoding($e, 'utf-8', 'sjis-win'), 3, '/home/stress-free/www/log/phptest.log');
	
	//echo mb_detect_encoding($e) . ":" . $e;	
	//$en = mb_convert_encoding($e,  'utf8' ,'sjis-win');
	$d = date("Y-m-d H-i-s");
	echo ($d);
	//echo mb_detect_encoding($en); 
	//print mb_internal_encoding();
	//print mb_convert_encoding($en, 'utf8', 'sjis-win');
//	$mojicode = mb_detect_encoding($e);
//	$ee =  "$textdata の文字コードは $mojicode です。";
//	print (mb_convert_encoding("テスト",  'utf-8','sjis-win'));
	
}

//以下削除候補


//いらない？？
function checkInvite(){
	$query = sprintf("SELECT status FROM loginuserlist WHERE id = '%d'", $_SESSION['user_id']);
	
	$res = mysql_query($query);
	if($item = mysql_fetch_array($res)){
		if($item['status'] == 'INVITED'){
        		print "invite";
		}else{
        		print "noinvite";
		}
	}else{
		print "login error : id:" . $_SESSION['user_id'];
	}
}

/*
function getdatetime(){
	return date("Y-m-d H-i-s");
}

function putlog($mes, $output = "debug.log"){
	$date = getdatetime();
	error_log($date . ": " . $mes . "\n" , 3, "/home/stress-free/www/log/$output");
}
*/

function execSQL($query, $db){
    $res = mysql_query($query, $db) or die(mysql_error());
	return $res;
}

/** loginuserlistテーブルの最終アクセス時刻を更新する 
 *
 */
function updateAccessTime(){
	$datetime = getdatetime();
	$userId = $_SESSION['user_id'];
	$query = sprintf("UPDATE loginuserlist SET accesstime = '%s' WHERE id = '%d'", $datetime, $userId);
	mysql_query($query) or die(mysql_error());

}
?>
