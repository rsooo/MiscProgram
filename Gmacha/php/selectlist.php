<?php
require_once 'commonmethod.php';
require 'opendb.php';
$SESSION_ERROR = "ERROR";

session_start();
if (!isset($_SESSION['count'])) {
  print $SESSION_ERROR;
} else {
$_SESSION['count']++;
//DB�ڑ�
/*
$db = mysql_connect("mysql120.db.sakura.ne.jp", "stress-free", "greedgreed");
mysql_select_db("stress-free", $db);
mysql_query("SET NAMES utf8");
*/
	switch ($_POST['ACTIONID']){
		case 1://���[�U���X�g�擾
			$query = "SELECT id, username, latitude, longitude, status, acceptinvite, publishlocation FROM loginuserlist";
			$res = mysql_query($query, $db) or die(mysql_error());
//			print "select 1";
			$rettext = "";
			while ($item = mysql_fetch_array($res)) {
			putlog($item['id'] . ":" . $_SESSION['user_id'] . "#");
			//id, nickname, sex, latitude, longitude # �̏��ŕԂ�
				if($item['status'] === 'WAIT' && $item['acceptinvite'] == 1 
					&& $item['publishlocation'] && $item['id'] != $_SESSION['user_id'] ){
				putlog("adddata");
				$nickName = getNickName($item['id']);
				$sex = getSex($item['id']);
				$rettext = $rettext . $item['id'] . "," . $nickName . "," . $sex . "," . $item['latitude'] . "," . $item['longitude'] . "#";
}
			}
			print $rettext;
			break;
		case 2://���[�U���X�g�I��
			print "select 2";
			break;
	}


mysql_close();

}
 	
/** ���[�UID����DB�Q�Ƃ��ăj�b�N�l�[���Ƃ�
 *  ���O��������Ȃ��Ƃ���NONAME��Ԃ�
 
function getNickName($userId){
			$query = sprintf("SELECT nickname FROM users WHERE user_id = '%s'", 		mysql_real_escape_string($userId));
	$res = mysql_query($query) or die(mysql_error());
	if ($item = mysql_fetch_array($res)) {
		return $item['nickname'];
	}
	return "NONAME";
	
}
*/
?>
