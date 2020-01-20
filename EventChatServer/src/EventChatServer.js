require('date-utils')
var Schema = require ('./Schema');

var io = require('socket.io').listen(3000);

io.sockets.on('connection', function(socket) {
	socket.json.emit('responseConnection', 'ok');
	
	// 新規ユーザの登録処理
	socket.on('registerUser', function(data) {
		var user = new Schema.User();
		user.uuid = data.uuid;
		user.nickname = data.nickname;
		user.socketID = socket.id;
		user.save(function(err) {
			if (!err) {
				console.log('register user ', data.nickname);
			}
		});
	});
	
	// 既存ユーザのsocketIDの更新処理
	socket.on('updateUser', function(data) {
		Schema.User.update({uuid : data.uuid}, {$set : {socketID : socket.id}}, {upsert : true,multi : false}, function(err) {
			if (!err) {
				console.log('update user');
			}
		});
	});
	
	// 新規イベントの登録処理
	socket.on('registerEvent', function(data) {
		console.log('CATCH RegisterEvent ok', data);
		var event = new Schema.Event();		
		event.uuid = data.uuid;
		event.eventTitle = data.eventTitle;
		event.latitude = data.latitude;
		event.longitude = data.longitude;
		event.description = data.description;
		event.count = 1;
		event.startDate = data.startDate;
		event.endDate = data.endDate;
		event.subscribeUsers = [data.uuid];
		event.lastUpdate = new Date().toFormat("YYYYMMDD HH24:MI:SS");
		event.save(function(err, e) {
			if (!err) {
				console.log('register Event', e._id);
				Schema.User.update({uuid : data.uuid}, {$push : {subscribeEvents : e._id}}, {upsert : true}, function(err){if(!err)console.log('hoge')});
			}
		});
	});
	
	// イベントリストの取得
	socket.on('getEventList', function(data) {
		console.log('CATCH GetEvent ok', data);
		Schema.Event.find({}, function(err, events) {
			if (!err) {
				events.forEach(function(_event){					
					var event = _event.toObject();
					event.eventID = event._id;
					event.status = 'none';
					if(event.uuid == data.uuid){
						event.status = 'owner';
						socket.json.emit('responseEventList', event);
						console.log('foofoo', event);
					} else {
						Schema.Event.findOne({_id : event.eventID, subscribeUsers : data.uuid}, function(err, doc){
							if(!err && doc){
								console.log('hogehoge', doc);
								event.status = 'subscribed';
							}							
							socket.json.emit('responseEventList', event);
						});
					}
				});
				
				
//				for ( var i = 0; i < events.length; i++) {
//					var event = events[i].toObject();						
//					event.eventID = events[i]._id;
//					event.status = 'none';
//					if(events[i].uuid == data.uuid){
//						event.status = 'owner';
//						socket.json.emit('responseEventList', event);
//						console.log('foofoo');
//					} else {
//						Schema.Event.findOne({subscribeUsers : data.uuid}, function(err, doc){
//							if(!err && doc){
//								event.status = 'subscribed';
//							}
//							console.log('hogehoge');
//							socket.json.emit('responseEventList', event);
//						});
//					}
//				}
			}
		});
	});
	
	// 自分がOwnerであるイベントのリストを取得
	socket.on('getOwnerEventList', function(data) {
		console.log('CATCH GetOwnerEventList ok');
		Schema.Event.find({uuid : data.uuid}, [ '_id' ], function(err, events) {
			if (!err) {
				for ( var i = 0; i < events.length; i++) {
					var ownerEvent = {};
					ownerEvent.eventID = events[i]._id;
					socket.json.emit('responseOwnerEventList', ownerEvent);
				}
			}
		});
	});
	
	
	// イベントの購読処理
	socket.on('subscribeEvent', function(data) {
		console.log('CATCH SubscribeEvent ok', data.eventID);
		Schema.Event.update({_id : data.eventID}, {$inc : {count : 1}, $push : {subscribeUsers : data.uuid}}, {upsert : true,multi : false}, function(err) {});
		Schema.User.update({uuid : data.uuid}, {$push : {subscribeEvents : data.eventID}}, {upsert : true,multi : false}, function(err){console.log(data.eventID)});
	});
	
	// 購読中のイベントリストの取得
	socket.on('getSubscribeEventList', function(data) {
		console.log('CATCH GetSubscribeList ok');
		Schema.User.findOne({uuid : data.uuid}, function(err, user){
			user.subscribeEvents.forEach(function(event){
				socket.json.emit('responseSubscribeEventList', event);
			});
		});
	});
		
	// イベントの購読解除処理
	socket.on('unsubscribeEvent', function(data) {
		console.log('CATCH removeSubscribeEvent ok');
		Schema.Event.update({_id : data.eventID}, {$inc : {count : -1}, $pull : {subscribeUsers : data.uuid}}, {upsert : true,multi : false}, function(err) {
			if (!err) {
				console.log('decrement count');
			}
		});
		Schema.User.update({uuid : data.uuid}, {$pull : {subscribeEvents : data.eventID}});
	});
	
	// イベントの削除処理
	socket.on('removeEvent', function(data) {
		console.log('CATCH removeEvent ok');
		Schema.Event.remove({_id : data.eventID, uuid : data.uuid}, function(err){
			if(!err){
				console.log('remove Event : ', data.eventID);	
			}
		});
		var event = {};
		event.eventID = data.eventID;
		socket.broadcast.emit('responseRemoveEvent', event);
	});
	
	// チャットメッセージの処理
	socket.on('eventChatMessage', function(message) {
		console.log('CATCH SendMessage ok', message.eventID);		
		message.time = new Date().toFormat("HH24:MI:SS");
		var updateTime = new Date().toFormat("YYYYMMDD HH24:MI:SS");
		Schema.Event.update({_id : message.eventID}, {lastUpdate : updateTime}, function(err){});
		Schema.User.find({subscribeEvents : message.eventID}, function(err, users){
			users.forEach(function(user){
				io.sockets.socket(user.socketID).json.emit('eventChatMessage', message);
				});
		});
	});
		
	
	// 切断時の処理
	socket.on('disconnect', function() {
	    console.log('disconnected');
	  });
});