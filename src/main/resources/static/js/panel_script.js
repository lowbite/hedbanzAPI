$("#notification-btn").click(function () {
    $(".active").removeClass("active");
    $(this).addClass("active");
    $.ajax({
        url: "/admin/global-notification",
        type: "GET",
        success: function (data) {
            $("#info-panel").html(data);
        }
    });
});

$("#version-btn").click(function () {
    $(".active").removeClass("active");
    $(this).addClass("active");
    $.ajax({
        url: "/admin/application-version",
        type: "GET",
        success: function (data) {
            $("#info-panel").html(data);
        }
    });
});

$("#feedback-btn").click(function () {
    $(".active").removeClass("active");
    $(this).addClass("active");
    $.ajax({
        url: "/admin/feedback-panel",
        type: "GET",
        success: function (data) {
            $("#info-panel").html(data);
            loadPageNumber();
            loadFeedback(0);
        }
    });
});

$("#game-stats-btn").click(function () {
    $(".active").removeClass("active");
    $(this).addClass("active");
    $.ajax({
        url: "/admin/game-stats",
        type: "GET",
        success: function (data) {
            $("#info-panel").html(data);
        }
    });
});

$("#advertise-btn").click(function () {
    $(".active").removeClass("active");
    $(this).addClass("active");
    $.ajax({
        url: "/admin/advertise",
        type: "GET",
        success: function (data) {
            $("#info-panel").html(data);
        }
    });
});

function loadFeedback(page) {
    $.ajax({
        url: "/admin/feedback?page=" + page,
        type: "GET",
        success: function (result) {
            $(".feedback-table > tbody").empty();
            var feedback;
            var rowData;
            var dateTime;
            var registrationDate;
            var i = page * 10;
            for (var key in result.data) {
                i++;
                feedback = result.data[key];
                dateTime = feedback.user.registrationDate !== undefined ? new Date(feedback.user.registrationDate) : "";
                if (dateTime !== "") {
                    dateTime.setTime(dateTime.getTime() - dateTime.getTimezoneOffset() * 60 * 1000);
                    registrationDate = dateTime.toUTCString();
                } else {
                    registrationDate = "";
                }
                rowData = {
                    feedbackText: feedback.feedbackText !== undefined ? feedback.feedbackText : "",
                    login: feedback.user.login !== undefined ? feedback.user.login : "",
                    email: feedback.user.email !== undefined ? feedback.user.email : "",
                    registrationDate: registrationDate,
                    money: feedback.user.money !== undefined ? feedback.user.money : "",
                    deviceVersion: feedback.deviceVersion !== undefined ? feedback.deviceVersion : "",
                    deviceName: feedback.deviceName !== undefined ? feedback.deviceName : "",
                    deviceModel: feedback.deviceModel !== undefined ? feedback.deviceModel : "",
                    deviceManufacturer: feedback.deviceManufacturer !== undefined ? feedback.deviceManufacturer : "",
                    product: feedback.product !== undefined ? feedback.product : "",
                    createdAt: feedback.createdAt !== undefined ? feedback.createdAt : ""
                };
                var table = "\n" +
                    "    <tr>\n" +
                    "      <th scope=\"row\">" + i + "</th>\n" +
                    "      <td>" + rowData.feedbackText + "</td>\n" +
                    "      <td>" + rowData.login + "</td>\n" +
                    "      <td>" + rowData.email + "</td>\n" +
                    "      <td>" + rowData.registrationDate + "</td>\n" +
                    "      <td>" + rowData.money + "</td>\n" +
                    "      <td>" + rowData.deviceVersion + "</td>\n" +
                    "      <td>" + rowData.deviceName + "</td>\n" +
                    "      <td>" + rowData.deviceModel + "</td>\n" +
                    "      <td>" + rowData.deviceManufacturer + "</td>\n" +
                    "      <td>" + rowData.product + "</td>\n" +
                    "      <td>" + rowData.createdAt + "</td>\n" +
                    "    </tr>";
                $(".feedback-table > tbody").append(table)
            }
        }
    });
}

function loadPageNumber() {
    $.ajax({
        url: "/admin/feedback/pages-number",
        type: "GET",
        success: function (result) {
            if (result.data > 1) {
                if (result.data = 2) {
                    $(".pagination > #next").before("<li class=\"page-item\" id=\"2\"><a class=\"page-link\"  href=\"#\">2</a></li>");
                } else {
                    $(".pagination > #next").before("<li class=\"page-item\" id=\"2\"><a class=\"page-link\"  href=\"#\">2</a></li>");
                    $(".pagination > #next").before("<li class=\"page-item\" id=\"3\"><a class=\"page-link\"  href=\"#\">3</a></li>");
                }
            }
        }
    });
}

$("#info-panel").on("click", ".page-item", function () {
    if (!$(this).hasClass(".active")) {
        var id = $(this).attr("id");
        if (id !== "next" && id !== "prev") {
            id = parseInt(id);
            generateNewPage(id);
        } else if (id === "next") {
            id = $(".page-item.active").attr("id");
            id = parseInt(id);
            if (!$(".page-item#next").hasClass("disabled")) {
                generateNewPage(id + 1);
            }
        } else if (id === "prev") {
            id = $(".page-item.active").attr("id");
            id = parseInt(id);
            if (!$(".page-item#prev").hasClass("disabled")) {
                generateNewPage(id - 1);
            }
        }
    }
});

function updateAdv() {
    var delay = parseInt($(".adv-delay").val());
    var type = parseInt($("#advertiseTypeSelect").find(':selected').attr('id'));
    $.ajax({
        url: "/admin/advertise",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            delay: delay,
            type: type
        }),
        dataType: 'json',
        success: function (result) {
            if (result.status === "success") {
                $(".success").removeClass("collapse");
                $(".error").addClass("collapse");
            } else {
                $(".error").removeClass("collapse");
                $(".success").addClass("collapse");
            }
        },
        error: function () {
            $(".error").removeClass("collapse");
            $(".success").addClass("collapse");
        }
    });
}

function updateVersion() {
    var version = parseInt($(".app-version").val());
    $.ajax({
        url: "/admin/panel/app",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            appVersion: version
        }),
        dataType: 'json',
        success: function (result) {
            if (result.status === "success") {
                $(".success").removeClass("collapse");
                $(".error").addClass("collapse");
            } else {
                $(".error").removeClass("collapse");
                $(".success").addClass("collapse");
            }
        },
        error: function () {
            $(".error").removeClass("collapse");
            $(".success").addClass("collapse");
        }
    });

}

function generateNewPage(id) {
    var pagination = $(".pagination");
    pagination.empty();
    pagination.append("        <li class=\"page-item\" id=\"prev\"><a class=\"page-link\"  href=\"#\">Previous</a></li>\n");
    if (id - 1 > 0) {
        pagination.append("        <li class=\"page-item\" id=\"" + (id - 1) + "\"><a class=\"page-link\"  href=\"#\">" + (id - 1) + "</a></li>\n");
    }
    pagination.append("        <li class=\"page-item active\" id=\"" + id + "\"><a class=\"page-link\"  href=\"#\">" + id + "</a></li>\n" +
        "        <li class=\"page-item\" id=\"next\"><a class=\"page-link\"  href=\"#\">Next</a></li>");
    $.ajax({
        url: "/admin/feedback/pages-number",
        type: "GET",
        success: function (result) {
            if (id < result.data && id >= 1) {
                $(".pagination > #next").before("<li class=\"page-item\" id=\"" + (id + 1) + "\"><a class=\"page-link\"  href=\"#\">" + (id + 1) + "</a></li>");
                $(".pagination > #next").removeClass("disabled");
                $(".pagination > #prev").removeClass("disabled");
                if(id === 1){
                    $(".pagination > #prev").addClass("disabled");
                }
            } else if (id >= result.data) {
                $(".pagination > #next").addClass("disabled")
            }
        }
    });
    loadFeedback(id - 1);
}

$(".send-notification").click(function () {
    var text = $(".notification-text").val();
    $.ajax({
        url: "/admin/notification/send",
        type: "POST",
        data: {
            text: text
        },
        success: function (result) {
            if (result.status === "success") {
                $(".success").removeClass("collapse");
                $(".error").addClass("collapse");
            } else {
                $(".error").removeClass("collapse");
                $(".success").addClass("collapse");
            }
        },
        error: function () {
            $(".error").removeClass("collapse");
            $(".success").addClass("collapse");
        }
    });
});

function sendNotification() {
    var text = $(".notification-text").val();
    var data = {
        text: text
    };
    $.ajax({
        url: "/admin/notification/send",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (result) {
            if (result.status === "success") {
                $(".success").removeClass("collapse");
                $(".error").addClass("collapse");
            } else {
                $(".error").removeClass("collapse");
                $(".success").addClass("collapse");
            }
        },
        error: function () {
            $(".error").removeClass("collapse");
            $(".success").addClass("collapse");
        }
    });
}