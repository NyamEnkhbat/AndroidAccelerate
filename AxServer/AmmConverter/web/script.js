



var ws = new WebSocket('ws://' + document.location.host + '/websocket');
//showMessage('Connecting...');
console.log('Connecting...');

ws.onopen = function () {
    //showMessage('Connected!');
	console.log('Connected!');
};

ws.onclose = function () {
	//sendMessage('Lost connection');
    console.log('Lost connection');
};

ws.onmessage = function (msg) {
    //showMessage(msg.data);
    //updateRecordsDatabase(msg.data)
    //dumpResults(results);
	sendMessage(msg);
};

function sendMessage(msg) {
	
	var json = JSON.parse(msg.data);

	if (json.recordType == "Passing") {
        var t = json.transponder
        if (t == undefined) t=json.transponderCode
        if (t == undefined) return;
        if (json.RTC_Time != undefined ) {
            unit="mms"
            console.log("RTC Time:"+json.RTC_Time)
        } else if (json.timeSecs != undefined) {
            unit="sec"
            console.log("TimeSecs:" + json.timeSecs)
        } else console.log("Json error:"+JSON.stringify(json))   
    }
}

function clearData() {
    var msg = document.getElementById("data");
    msg.innerHTML = header
    results = new Array();
}

function showMessage(text) {
    var msg = document.getElementById("console");
    var org = msg.innerHTML.substr(0, 20000)
    msg.innerHTML = text + "<br/>" + org;

}

var header = "<table id=\"dataTable\"><tr><th>Position</th><th>Transponder</th>" +
    "<th>Laps</th><th>Time</th><th>Last lap</th><th>Best Lap time</th></tr>";

var results = new Array();
var lastR;
var unit;
function updateRecordsDatabase(data) {
    var json = JSON.parse(data)
    if (json.recordType == "Passing") {
        console.log("A:" + results);

        lastR= null;
        for(var index in results) {
            var t = json.transponder
            if (t == undefined) t=json.transponderCode
            if (t == undefined) next;
            console.log("Compare:"+
                " -> "+ t + ":" + results[index])
            if (t == results[index][0]) {
                results[index][1]++
                if (json.RTC_Time != undefined ) {
                    unit="mms"
                    console.log("RTC Time:"+json.RTC_Time)
                    var lastRtc = results[index][2]
                    results[index][2]=json.RTC_Time
                    results[index][3]=json.RTC_Time-lastRtc
                } else if (json.timeSecs != undefined) {
                    unit="sec"
                    console.log("TimeSecs:" + json.timeSecs)
                    var lastTimeSecs = results[index][2]
                    results[index][2]=json.timeSecs
                    results[index][3]=json.timeSecs-lastTimeSecs;
                } else console.log("Json error:"+JSON.stringify(json))

                if (results[index][4]=="") results[index][4]=results[index][3]   // o best lap time yet
                else if (results[index][3]<results[index][4])   // new best lap time
                    results[index][4]=results[index][3];
                lastR=results[index][0]
                break;
            }
        }

        if (lastR == null) {
            console.log("Adding new " + JSON.stringify(json));
            var time = "Err"
            if (json.RTC_Time != undefined )
                time = json.RTC_Time
            else if (json.timeSecs != undefined)
                time = json.timeSecs
            results.push(new Array(t, 0, time,"",""));
            lastR=t
        }

    }
}

function resultSorter(a,b) {
    return b[1]-a[1]
}

function dumpResults(a) {
    a.sort(resultSorter)
    var msg = header
    msg+="<tbody>"

    a.forEach(function dump(r,index,array) {

        var d =new Date(r[2])
        var time="Err"
        if (!isNaN(d))
            time = d.getHours()+":"+ d.getMinutes()+":"+ d.getSeconds()+"."+d.getMilliseconds()
        else
            time = r[2]

        if (unit=="secs") {
            d = new Date(r[2]*1000)
            time = d.getHours()+":"+ d.getMinutes()+":"+ d.getSeconds()+"."+d.getMilliseconds()
        }

        if (unit=="mms") {
            d = new Date(r[2]/1000)
            time = d.getHours()+":"+ d.getMinutes()+":"+ d.getSeconds()+"."+d.getMilliseconds()
        }
        var lapTime = (Math.round(r[3]*1000)/1000)
        if (unit="mms") lapTime=lapTime/(1000*1000)   // recalculate RTC a UTC time from microseconds
        var bestLapTime = (Math.round(r[4]*1000)/1000)
        if (unit="mms") bestLapTime=bestLapTime/(1000*1000)  // recalculate RTC a UTC time from microseconds

        var tr = r[0]
        if (lastR==r[0]) tr += "  <<<<"
        msg+=  "<tr id=\"row" + index%2 + "\"><td>"+(index+1)+"</td><td>" + tr + "</td><td>"
            + r[1]+"</td><td>" + time +"</td><td>"+lapTime+"</td><td>"+bestLapTime+"</td></tr>";
    });
    msg+="</tbody></table>"
    var m = document.getElementById("data");
    m.innerHTML=msg
}
