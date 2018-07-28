$(".submit-btn").click(function signIn () {
    var data = {
        login: $(".login").val(),
        password: $(".password").val()
    };

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/admin",
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (result) {
            if (result.status === "success") {
                getAdminPanel(result.data.securityToken);
                $(".error-block").empty();
            }else {
                $(".error-block").empty();
                $(".error-block").append(result.error.errorMessage);
            }
        }
    });
});

function getAdminPanel(token) {
    $.cookie("security-token", token);
    window.location.replace("/admin_panel");
}