var hoge = {x:1, y:2};
hoge.z = 3;
console.log(hoge);
      
var format = require('date-utils')
var date = new Date('2012/01/28 22:00:00');
console.log(date);
var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var TestSchema = new Schema({
	hoge : String,
	col : [String]
});

mongoose.model('Test', TestSchema);
mongoose.connect('mongodb://localhost/test')
var Test = mongoose.model('Test');
//var t = new Test();
//t.hoge = "hoge";
//t.col = ["aaa", "bbb"];
//t.col.push("ccc");
////for(var h in t.col){console.log(h)}
//t.save();
Test.findOne({col:"ddd"}, function(err, docs){console.log("hoge")})

//console.log(t);


//
//console.log(date);
//console.log(date.toFormat("HH24:MI:SS"));
//mongoose.model('Event', EventSchema);
//mongoose.connect('mongodb://localhost/greedDB');
//var Event = mongoose.model('Event');
//Event.find({}, function(err, docs){	
//	
//	docs.forEach(function(doc){
//		var test = doc.toObject();
//		test.hoge = 'hogehoge';
//		//var a = JSON.stringify(test);
//		console.log(test);
//		console.log("*******************************************")
//	});	
//});

