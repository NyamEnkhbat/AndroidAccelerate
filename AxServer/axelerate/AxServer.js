var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var config = require('./config.json');
var mysql = require('mysql');

var MinLapTime = 0100;
var MaxLapTime = 1000;
//var Port = 3000;

var connection = mysql.createConnection(
    {
        host     : 'localhost',
        user     : 'awli',
        password : '',
        database : 'Accelerate'
    }
);
connection.connect();


//Redirect requests for / to index.html
app.get('/', function(req, res){
  res.sendfile('index.html');
});

//Log new Connections
io.on('connection', function(socket){
  console.log('a user connected');
});

//Listen to port
http.listen(3000, function(){
    console.log('listening on Port 3000');
});

//Add new Car (and Driver if not exists)
io.on('connection', function(socket){
    socket.on('registerNewTransponder', function(car){
        data = JSON.parse(car);
        console.log('IN : registerNewTransponder - '+ car);

        //Manufacturer
        var post  = {Name: data.model.manufacturer.name};
        var query = connection.query('INSERT INTO Manufacturer SET ?', post, function(err, result) {
            // Neat!
        });
        //Class
        var post  = {Name: data.clazz.name};
        var query = connection.query('INSERT INTO Classes SET ?', post, function(err, result) {
            // Neat!
        });
        //Model
        var post  = {Name: data.model.name, Manufacturer: data.model.manufacturer.name};
        var query = connection.query('INSERT INTO Model SET ?', post, function(err, result) {
            // Neat!
        });
        //Driver
        var post  = {Firstname: data.driver.firstname};
        var query = connection.query('INSERT INTO Drivers SET ?', post, function(err, result) {
            //
            var post  = {Driver: result, Model: data.model.name,Class: data.clazz.name,Transponder: data.transponderID};
            var query = connection.query('INSERT INTO Cars SET ?', post, function(err, result) {
                // Neat!
            });
        });
        console.log('OUT: registerTransponderSuccess - '+ car);
        socket.emit('registerTransponderSuccess', car);
    });

    socket.on('TestConnection', function(socketID) {
        console.log('IN : TestConnection - ' + socketID);
        console.log('OUT: Welcome - ' + socketID);
        socket.emit('Welcome', socketID);
    });

    socket.on('deregusterTransponder', function(car) {
        console.log('IN : deregusterTransponder - '+ car);
    });

    socket.on('getFastest', function(clazz) {
        console.log('IN : getFastest - '+ clazz);
    });

    socket.on('getRecords', function(clazz) {
        console.log('IN : getRecords - '+ clazz);
    });

    socket.on('getAll', function() {
        console.log('IN : getAll');
    });

    socket.on('getMyLaps', function(car) {
        console.log('IN : getMyLaps - '+ car);
    });
});



//Handling Shutdown###################################################################
process.stdin.resume();//so the program will not close instantly

function exitHandler(options, err) {
    console.log('Axelerate Server shuts down now!');
    connection.end();
    if (options.cleanup) console.log('clean');
    if (err) console.log(err.stack);
    if (options.exit) process.exit();
}

//do something when app is closing
process.on('exit', exitHandler.bind(null,{cleanup:true}));

//catches ctrl+c event
process.on('SIGINT', exitHandler.bind(null, {exit:true}));

//catches uncaught exceptions
process.on('uncaughtException', exitHandler.bind(null, {exit:true}));

process.on('error', function(ex) {
  console.log("handled error");
  console.log(ex);
});

//#####################################################################################


//Decoder Interaction
//'ws://' + document.location.host + '/websocket'
var WebSocket = require('ws'), ws = new WebSocket ('ws://192.168.1.7:8080/websocket');
console.log('Connecting...');

ws.onopen = function () {
    console.log('Connected!');
};

ws.onclose = function () {
    console.log('Lost connection');
};

ws.onmessage = function (msg) {
    onPassing(msg);
};

var lapBuffers = [];

 LapBuffer = {
    transponderID: 0,
    lapBegin: 0,
    lapEnd: 0
};

function onPassing(msg){
    if(msg.data == "RaspiTimingBox connected") {
        console.log("Connection Test Successful!");
        return;
    }
    var json = JSON.parse(msg.data);

    if (json.recordType != "Passing") return;

    var transponderID = getTransponderID(json);
    var passingTime = getPassingTime(json);
    //Don't do anything id we don't have the Data.
    if(passingTime == null) return;
    if (transponderID == undefined) return;

    var currentLapBuffer = null;

    //Get the Lapbuffer associated with this transponder
    currentLapBuffer = lapBuffers.forEach(function (lapBuffer) {
        if(lapBuffer.transponderID == transponderID){
            return lapBuffer;
        }
    });

    //if no LapBuffer is definded, create it and return!
    if(currentLapBuffer == null){
        currentLapBuffer = new LapBuffer;
        currentLapBuffer.lapBegin = passingTime;
        currentLapBuffer.transponderID = transponderID;
        lapBuffers.push(currentLapBuffer)
    } else if(currentLapBuffer.lapBegin != 0) {
        var lapTime = currentLapBuffer.lapBegin - passingTime;

        if (lapTime >= MinLapTime && lapTime <= MaxLapTime) {
            connection.query('Select * From Cars Where Transponder = ?', currentLapBuffer.transponderID, function (err, result) {
                var post = {Time: passingTime, Car: result.ID};
                connection.query('INSERT INTO Laps SET ?', post, function (err, result) {
                    socket.emit('Registerd Lap',result);
				});
			});
        }
        //start a new Lap
        currentLapBuffer.lapBegin = passingTime;
    }
}

function getTransponderID(json) {
    var transponder = json.transponder;
    if (transponder == undefined) transponder = json.transponderCode;
    return transponder;
}

function getPassingTime(json){
    var retVal = null;


    if (json.RTC_Time != undefined ) {
        retVal = new Date(json.RTC_Time/1000);
    } else if (json.timeSecs != undefined) {
        retVal = new Date(json.timeSecs*1000);
    } else {
        console.log("JSON error:"+JSON.stringify(json))
    }

    return retVal;
}

// Decoder Interaction End
