$(function () {
    var token = $.cookie("security-token");
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/admin_panel/app",
        headers: {
          "Authorization" : "Bearer " + token
        },
        dataType: 'json',
        success: function (result) {
            if (result.status === "success") {
                var version = result.data.version;
                $(".app-version").val(version);
                $(".error-block").empty();
            } else {
                $(".error-block").empty();
                $(".error-block").append(result.error.errorMessage);
            }
        }
    });
});
$(".app-form").submit(function (event) {
    event.preventDefault();
    var token = $.cookie("security-token");
    var data = {
        appVersion: parseInt($(".app-version").val())
    };
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/admin_panel/app",
        headers: {
            "Authorization" : "Bearer " + token
        },
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (result) {
            if (result.status === "success") {
                var version = result.data.version;
                $(".app-version").val(version);
                $(".error-block").empty();
                $(".error-block").append("Updated successfully");
                $(".error-block").attr("style", "color: green")
            } else {
                $(".error-block").empty();
                $(".error-block").append(result.error.errorMessage);
            }
        }
    });
});