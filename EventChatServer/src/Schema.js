var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var EventSchema = new Schema({
	uuid : String,
	latitude : Number,
	longitude : Number,
	eventTitle : String,
	description : String,
	startDate : String,
	endDate : String,
	count : Number,
	lastUpdate : String,
	subscribeUsers : [String]
});

var SubscribeListSchema = new Schema({
	eventID : String,
	uuid : String
});

var UserSchema = new Schema({
	uuid : String,
	nickname : String,
	socketID : String,
	subscribeEvents : [String]
});

mongoose.model('Event', EventSchema);
mongoose.model('SubscribeList', SubscribeListSchema);
mongoose.model('User', UserSchema);
mongoose.connect('mongodb://localhost/greedDB');
exports.Event = mongoose.model('Event');
exports.SubscribeList = mongoose.model('SubscribeList');
exports.User = mongoose.model('User');