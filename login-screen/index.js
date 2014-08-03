$(document).ready(function() {

    $(".password").keypress(function(e){
        if(e.which == 13){
            $('#login').fadeOut();
            $('#loading').fadeIn();      
        }
    });

    $.ajax({
        type: 'GET',
        url: '',
        dataType: 'jsonp',
        success: function(response){
            console.log(response);
            $("#success").fadeIn();
            $('body').css("background-color", "#7ed21c");
        },
        error: function(response){
            console.log(response);
        }.done(function() {
            // $("#success").fadeIn();
            // $('body').css("background-color", "#7ed21c");
        })
    });

});
