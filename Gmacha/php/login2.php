<?php

require_once 'commonmethod.php';
require 'opendb.php';

//$_POST['NAME'];
//$_POST['PASS'];
//$_POST['ACTIONID'];
//$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
//mysql_select_db("stress-free", $db);
//mysql_query("SET NAMES utf8");
//mysql_query("insert into users values(100,'test','test')");

session_start();
switch ($_POST['ACTIONID']){
	case 0:
	login();
	break;
	case 1:
		createAccount();
	break;
	case 2:
		createAccountUsingSendmail();
		break;
	case 10:
		changePassWord();
		break;
	case 20:
//		session_start();
		printGeoLocation();
		break;
	case 21:
		printNickName();
		break;
	case 30:
		guestlogin();
		putlog($testMes);
		break;
}
mysql_close();

/* �Q�X�g���O�C��
 * POST
 * nickname �j�b�N�l�[��
 * sex ����
 *     �e��ʒu���A���
 * �߂�l 0 ���O�C������
 *       -1 ���O�C�����s
 * �Z�b�V�����F
 * �X�V�Fuser_id, user_name
 */
function guestlogin(){
global $db;

	$newNickName = $_POST['NAME'];
	$newSex = $_POST['SEX'];

	$newId_query = "SELECT MAX(user_id) from users";
	$res = mysql_query($newId_query, $db) or die(mysql_error());
	//�V�������[�UID�̎擾
	if ($item = mysql_fetch_array($res)) {
		$newUserId = $item['MAX(user_id)'] + 1;
	}else{
		print "-1";	//�G���[
		return;
	}
	$insert_query = sprintf("INSERT INTO users VALUES(%d, 'guest', 'none', 'none' ,'%s', '%s')", 
	$newUserId,
	mysql_real_escape_string(mb_convert_encoding($newNickName, "utf8", "sjis")),	mysql_real_escape_string($newSex)
	);
	//putlog($insert_query);

	mysql_query($insert_query, $db) or die(mysql_error());

	

	$insert_query2 = sprintf("INSERT INTO loginuserlist(id, username, latitude, longitude, altitude, accuracy, chatroom, status, accesstime ) VALUES (%s, 'guest', %s, %s, %s, %s, 0 , '%s', '%s')", 
    mysql_real_escape_string($newUserId),
    mysql_real_escape_string($_POST['LATITUDE']),
    mysql_real_escape_string($_POST['LONGITUDE']),
    mysql_real_escape_string($_POST['ALTITUDE']),
    mysql_real_escape_string($_POST['ACCURACY']),
    mysql_real_escape_string($_POST['STATUS']),
	getdatetime()
  	);
	$res = mysql_query($insert_query2, $db) or die(mysql_error());
	
//	putlog($insert_query2);


//	putlog("session create");
	//�Z�b�V�����ɕۑ�
	$_SESSION['user_id'] = $newUserId;
	$_SESSION['user_name'] = "guest";

	if (!isset($_SESSION['count'])) {

		$_SESSION['count'] = 1;
	} else {
  		$_SESSION['count']++;
	}

	print "0";

}


function login(){
global $db;

	$userName = $_POST['NAME'];
	$userPass = $_POST['PASS'];

/*
	if($userName === "guest"){
		//�Q�X�g�p����
		
	}else{

	}
*/
$query = sprintf("SELECT user_id, nickname FROM users WHERE user_name='%s' AND user_pass= PASSWORD('%s')",
    mysql_real_escape_string($userName),
    mysql_real_escape_string($userPass));
    
$res = mysql_query($query, $db);
//	putlog($query);


if ($item = mysql_fetch_array($res)) {
//	session_start();
	$_SESSION['user_id'] = $item['user_id'];
	$_SESSION['user_name'] = $_POST['NAME'];
		//error_log("LOGIN:" . $_SESSION['user_name'] . "\n" , 3, "/home/stress-free/www/log/test.log");
	
	if (!isset($_SESSION['count'])) {

		print "0#" . $item['nickname'];//ACCEPT�T�C��
		$_SESSION['count'] = 1;
 		$_SESSION['user_name'] = $_POST['NAME'];
  		$_SESSION['user_pass'] = $_POST['PASS'];
	} else {
		print '0#' . $item['nickname']; //ACCEPT�T�C��
  		$_SESSION['count']++;
 		$_SESSION['user_name'] = $_POST['NAME'];
  		$_SESSION['user_pass'] = $_POST['PASS'];
	}
	insertdata();
 }
 else{
 	$response = '�p�X���[�h������Ă��܂�';
  print mb_convert_encoding($response, 'utf8', 'sjis-win');
 }
}

//�A�J�E���g�쐬
//�ȉ��̖߂�l��Ԃ�
//0:�쐬����
//1:�A�J�E���g�d��
//-1:���̑��̃G���[
function createAccount(){
global $db;
$newUserId = -1;
$query = sprintf("SELECT user_id FROM users WHERE user_name='%s'" ,
    mysql_real_escape_string($_POST['NAME']));
	$res = mysql_query($query, $db) or die(mysql_error());
	//�d���������O�����邩�ǂ������ׂ�
	if ($item = mysql_fetch_array($res)) {
		print "1";
		return;
	}
	$query2 = "SELECT MAX(user_id) from users";
	$res = mysql_query($query2, $db) or die(mysql_error());
	//�V�������[�UID�̎擾
	if ($item = mysql_fetch_array($res)) {
		$newUserId = $item['MAX(user_id)'] + 1;
	}else{
		print "-1";	//�G���[
		return;
	}

	$query3 = sprintf("INSERT INTO users VALUES(%d, '%s', '%s')", 
	$newUserId,
	mysql_real_escape_string($_POST['NAME']),
	mysql_real_escape_string($_POST['PASS'])
	);
	$res = mysql_query($query3, $db) or die(mysql_error());
	print "0";	

}

function createAccountUsingSendmail(){
	global $db;
	$newName = $_POST['NAME'];
	$newMail = $_POST['MAIL'];
	$newNickName = $_POST['NICKNAME'];
	$newSex = $_POST['SEX'];
	$newUserId = -1;
//	putlog($newNickName);

	$query = sprintf("SELECT user_id FROM users WHERE user_name='%s'" ,
    mysql_real_escape_string($newName));
	$res = mysql_query($query, $db) or die(mysql_error());
	//�d���������O�����邩�ǂ������ׂ�
	if ($item = mysql_fetch_array($res)) {
		print "1"; //�d������
		return;
	}
	//���łɃ����A�h���g���Ă��邩�ǂ������`�F�b�N
	$query2 = sprintf("SELECT mail FROM users WHERE mail ='%s'" ,
    mysql_real_escape_string($newMail));
	$res = mysql_query($query2, $db) or die(mysql_error());
	//�d�����������A�h�����邩�ǂ������ׂ�
	if ($item = mysql_fetch_array($res)) {
		print "2"; //�d������
		return;
	}
	//�V�������[�UID�̎擾
	$query3 = "SELECT MAX(user_id) from users";
	$res = mysql_query($query3, $db) or die(mysql_error());
	if ($item = mysql_fetch_array($res)) {
		$newUserId = $item['MAX(user_id)'] + 1;
	}else{
		print "-1";	//�G���[
		return;
	}

	//print "$newMail";
	$randPass = mt_rand(9999,65536);
	$query4 = sprintf("INSERT INTO users VALUES(%d, '%s', PASSWORD('%s'), '%s' ,'%s', '%s')", 
	$newUserId,
	mysql_real_escape_string($newName),
	mysql_real_escape_string($randPass),
	mysql_real_escape_string($newMail),
	mysql_real_escape_string(mb_convert_encoding($newNickName, "utf8", "sjis")),	mysql_real_escape_string($newSex)
	);
//	putlog($query4);
	$res = mysql_query($query4, $db) or die(mysql_error());
//	putlog($query4);	
//	putlog($res);
$mailtext = <<< END
$newNickName �l

G-macha�^�c�`�[���ł��B
���̓x��G-macha�ɂ��o�^�����������肪�Ƃ��������܂��B

�A�J�E���g�쐬�������������܂����B�������܂���G-macha�������p���������܂��B

�ȉ��̃A�J�E���g��p���ă��O�C�����s���Ă��������B
ID:$newName
Pass:$randPass

�܂��A���񃍃O�C����͐ݒ胁�j���[�ɂăp�X���[�h�ύX���s���Ă��������B


������A�s��Ȃǂ������܂����牺�L���[���A�h���X�܂ł��A�����肢�v���܂��B
admin@stress-free.jpn.org


END;

$header = "From : register@stress-free.jpn.org";
	mb_send_mail($newMail,
		mb_convert_encoding("[G-macha]�A�J�E���g�쐬�̂��m�点", 'utf8', 'sjis-win'),
		mb_convert_encoding($mailtext, 'utf8', 'sjis-win'), $header
	);

	mb_send_mail("akira.saito1986@gmail.com",
		mb_convert_encoding("[G-macha]�A�J�E���g�쐬���쐬����܂���", 'utf8', 'sjis-win'),
		mb_convert_encoding($newName . "���A�J�E���g�쐬���܂���", 'utf8', 'sjis-win'), $header
	);
	mb_send_mail("kabashima.seiichiro@gmail.com",
		mb_convert_encoding("[G-macha]�A�J�E���g�쐬���쐬����܂���", 'utf8', 'sjis-win'),
		mb_convert_encoding($newName . "���A�J�E���g�쐬���܂���", 'utf8', 'sjis-win'), $header
	);

	print "0";	

	
}

/* �ʒu�����擾���ďo�͂���
 * �Z�b�V�������؂�Ă���Ƃ���-1���o��
 * �o�� "latitude#longitude"
 *      -1 : �G���[   
 */
function printGeoLocation(){
	global $db;
	$uname = $_SESSION['user_name'];
	//print "uname:" . $uname;
	if(empty($uname)){
		print "-1"; //�Z�b�V�����؂�
		return;
	}
	
	$query = sprintf("SELECT latitude, longitude FROM loginuserlist WHERE username='%s'",
    mysql_real_escape_string($uname));
	$res = mysql_query($query, $db) or die(mysql_error());
	if ($item = mysql_fetch_array($res)) {
		print $item['latitude'] . "#" . $item['longitude'];
		return;
	}
	print "-1";
	return;
	
}

/** �j�b�N�l�[�����擾���郁�\�b�h
 * �Z�b�V�������؂�Ă���Ƃ���-1���o��
 * �o�� "�j�b�N�l�[��"
 * �G���[ -1
 *
 */
function printNickName(){
	global $db;
	$userId = $_SESSION['user_id'];
	if(empty($userId)){
		print "-1"; //�Z�b�V�����؂�
		return;
	}
	$query = sprintf("SELECT nickname FROM users WHERE user_id='%d'", $userId);
	$res = mysql_query($query, $db) or die(mysql_error());
	if ($item = mysql_fetch_array($res)) {
		print $item['nickname'];
		return;
	}
	print "-1";
	
	
}

//�p�X���[�h�ύX���s�����\�b�h
//POST: CURRENTPASS, NEWPASS
//�o�� 0:�ύX����
//     1:CurrentPassWord��v����
//     
function changePassWord(){
	global $db;
	$uname = $_SESSION['user_name'];
	$currentPass = $_POST['CURRENTPASS'];
	$newPass = $_POST['NEWPASS'];
	//error_log($uname . ": " . $currentPass . $newPass . "\n" , 3, "/home/stress-free/www/log/test.log");

	$query = sprintf("SELECT user_id FROM users WHERE user_name = '%s' AND user_pass = PASSWORD('%s')",  
	mysql_real_escape_string($uname),
	mysql_real_escape_string($currentPass)
	);
	$item = execSQL($query, $db);
	if($item){
		$userId = $item['user_id'];
		$query2 = sprintf("UPDATE users SET user_pass = PASSWORD('%s') WHERE user_id = '%d' ",
 	mysql_real_escape_string($newPass),
	mysql_real_escape_string($userId)
	);
		//error_log($query2, 3, "/home/stress-free/www/log/test.log");

		//execSQL($query2, $db);
		mysql_query($query2, $db) or die(mysql_error());
		print "0";
	}else{
		print "1";
	}

	//$query = sprintf("SELECT user_id FROM users WHERE user_name='%s' AND user_pass='
}

function getchatmessage(){
	$LOGSTRING = "" . $_SESSION['message'];
	$_SESSION['message'] = $_POST['user'] . '<BR>' . $LOGSTRING;
}

function insertdata(){
//$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
//mysql_select_db("stress-free", $db);

global $db;
//���Ƀ��O�C�����Ă���f�[�^�͏�����
$dquery = sprintf("DELETE FROM loginuserlist WHERE id = '%s'", $_SESSION['user_id']);
mysql_query($dquery, $db) or die(mysql_error());

$query = sprintf("INSERT INTO loginuserlist(id, username, latitude, longitude, altitude, accuracy, chatroom, status, accesstime ) VALUES (%s, '%s', %s, %s, %s, %s, 0 , '%s', '%s')", 
    mysql_real_escape_string($_SESSION['user_id']),
    mysql_real_escape_string($_POST['NAME']),
    mysql_real_escape_string($_POST['LATITUDE']),
    mysql_real_escape_string($_POST['LONGITUDE']),
    mysql_real_escape_string($_POST['ALTITUDE']),
    mysql_real_escape_string($_POST['ACCURACY']),
    mysql_real_escape_string($_POST['STATUS']),
	getdatetime()

);
//print ",query#" . $query;
$res = mysql_query($query, $db) or die(mysql_error());

//print ",errno#" . mysql_errno($db). ",errorContent#" .mysql_error($db);       // 	
}

//SQL���s����֐�
//���܂�ς��Ȃ�
function execSQL($query, $db){
    $res = mysql_query($query, $db) or die(mysql_error());
	return mysql_fetch_array($res);
}

/*
function putlog($mes, $output = "debug.log"){
	$date = getdatetime();
	error_log($date . ": " . $mes . "\n" , 3, "/home/stress-free/www/log/$output");
}

function getdatetime(){
	return date("Y-m-d H-i-s");
}
*/


?>
