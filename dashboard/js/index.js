$(document).ready(function() {

    var numIn  = 0, numOut = 0;  // count of Direction Out
    var numPost = 0, numGet = 0, numEvent = 0;
    var direction, client, type;
    var socket = io.connect('http://rtrp.io');

    directionChart = new Highcharts.Chart({
        chart: {
            renderTo: 'direction',
            type: 'bar',
        },
        title: {
            text: 'Direction Data'
        },
        xAxis: {
            categories: ['In', 'Out'],
        },
        yAxis: {
            min: 0,
            labels: {
                overflow: 'justify'
            }
        },
        plotOptions: {
            bar: {
                dataLabels: {
                    enabled: true
                }
            }
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'top',
            x: -40,
            y: 100,
            floating: true,
            borderWidth: 1,
            backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor || '#FFFFFF'),
            shadow: true
        },
        credits: {
            enabled: false
        },
        series: [{
            data: [0, 0],
            showInLegend: false
        }]
    });


    typeChart = new Highcharts.Chart({
        chart: {
            renderTo: 'type-chart',
            type: 'bar',
        },
        title: {
            text: 'Type Data'
        },
        xAxis: {
            categories: ['Get', 'Post', 'Event'],
        },
        yAxis: {
            min: 0,
            labels: {
                overflow: 'justify'
            }
        },
        plotOptions: {
            bar: {
                dataLabels: {
                    enabled: true
                }
            }
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'top',
            x: -40,
            y: 100,
            floating: true,
            borderWidth: 1,
            backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor || '#FFFFFF'),
            shadow: true
        },
        credits: {
            enabled: false
        },
        series: [{
            data: [0, 0, 0],
            showInLegend: false
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
        $("#activityStream").append("<p>Type:" + type + ", Direction: " + direction + ", Client: " + client + "</p>");

        // update direction In and Out count
        if (direction == "in"){
            numIn += 1 ;
        }
        else if(direction == "out"){
            numOut += 1 ;
        }
        else{
            console.log("something went wrong");
        }

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
        
        typeChart.series[0].setData([numGet, numPost, numEvent]);
        directionChart.series[0].setData([numIn, numOut]);
    });

}); 