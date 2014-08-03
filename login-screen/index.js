$(document).ready(function() {

    $(".password").keypress(function(e){
        if(e.which == 13){
            $('#login').fadeOut();
            $('#loading').fadeIn();      

            $.ajax({
                type: 'GET',
                url: 'http://rtrp.io/client/AuthRotor',
                dataType: 'json',
                success: function(response) {
                    console.log(response);
                    if (response === true) {
                        $('#loading').hide();
                        $("#success").show();
                        $('body').css("background-color", "#7ed21c");
                    } else {
                        $('#loading').hide();
                        $('#failure').show();
                        $('body').css("background-color", "#cb2b2b");
                        //$('#loading').fadeOut();
                        //$('#login').fadeIn();
                    }
                },
                error: function(response){
                    console.log(response);
                    $('#loading').fadeOut();
                    $('#login').fadeIn();
                }
            });
       }

    });

});
