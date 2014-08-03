$(document).ready(function() {

    var numIn  = 0, numOut = 0, numDisconnect = 0; 
    var numPost = 0, numGet = 0, numEvent = 0, numID = 0, numDisconnect2 = 0;
    var initialTime = new Date().getTime();
    var direction, client, type;
    var socket = io.connect('http://rtrp.io');

    directionChart = new Highcharts.Chart({
        chart: {
            type: 'area',
            renderTo: 'direction',
        },
        title: {
            text: 'Direction Data'
        },
        xAxis: {
            // categories: ["In", "Out"],
            tickmarkPlacement: 'on',
            title: {
                text: 'Milliseconds'
            }
        },
        yAxis: {
            title: {
                text: 'Data Points'
            },
        },
        plotOptions: {
            area: {
                // stacking: 'normal',
                marker: {
                    enabled: false,
                    symbol: 'circle',
                    radius: 2,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                }
            }
        },
        series: [{
            name: 'In',
            data: [0]
        }, {
            name: 'Out',
            data: [0]
        
        }, {
            name: 'Disconnect',
            data: [0]
        
        }]
    });


    typeChart = new Highcharts.Chart({
        chart: {
            renderTo: 'type-chart',
            type: 'area',
        },
        title: {
            text: 'Type Data'
        },
        xAxis: {
            tickmarkPlacement: 'on',
            title: 'Milliseconds',
        },
        yAxis: {
            title: "Points"
        },
        plotOptions: {
            area: {
                stacking: 'normal',
                marker: {
                    enabled: false,
                    symbol: 'circle',
                    radius: 2,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                }
            }
        },
        series: [{
                name: 'Get',
                data: [0]
            }, {
                name: 'Post',
                data: [0]
            },
            {
                name: 'Event',
                data: [0]
            },{
                name: 'ID',
                data: [0]
            },{
                name: 'Disconnect',
                data: [0]
            }]
    });

    socket.on('connect', function() {
      socket.emit('alias', 'dashboard');
    });

    socket.on('log', function(event) {
        // process the event here
        // event.client, event.type, event.direction
        console.log(event);
        direction = event.direction;
        client = event.client;
        eventType = event.type;
        currentTime = event.time;

        var milliseconds = (currentTime - initialTime);

        // update direction In and Out count
        if (direction == "in"){
            numIn += 1 ;
        }
        else if(direction == "out"){
            numOut += 1 ;
        }
        else{
            numDisconnect += 1;
        }

        var numGet = 1;
        // Update type GET and POST
        if (eventType == "GET"){
            numGet += 1;
        }
        else if (eventType == "POST"){
            numPost += 1;
        }
        else if (eventType == "event"){
            numEvent += 1;
        }
        else if (eventType == "id"){
            numID += 1;
        }
        else{
            console.log("disconnected!");
        }

        directionChart.series[0].addPoint([milliseconds, numIn]);
        directionChart.series[1].addPoint([milliseconds, numOut]);
        directionChart.series[2].addPoint([milliseconds, numDisconnect]);


        typeChart.series[0].addPoint([milliseconds, numGet]);
        typeChart.series[1].addPoint([milliseconds, numPost]);
        typeChart.series[2].addPoint([milliseconds, numEvent]);
        typeChart.series[3].addPoint([milliseconds, numID]);
        typeChart.series[4].addPoint([milliseconds, numDisconnect2]);

        $("#activityStream").append("<p>Type:" + eventType + ", Direction: " + direction + ", Client: " + client + "</p>");

    });

}); 