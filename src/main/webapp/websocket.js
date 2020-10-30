var ws;

function connect() {
    var username = document.getElementById("username").value;

    var host = document.location.host;
    var pathname = document.location.pathname;
    console.log("ws://" + host + pathname + "chat/" + username)

    ws = new WebSocket("ws://" + host + pathname + "chat/" + username);

    ws.onmessage = function (event) {
        var log = document.getElementById("log");
        console.log(event.data);
        var message = JSON.parse(event.data);
        // log.innerHTML += message.from + " : " + message.content + "\n";

        $.get("http://localhost:8080/prattle/rest/user/allusers", function (allUserData, status) {
            console.log(allUserData)
            jQuery.each(allUserData, function (idx, user) {
                if (user.userID === message.fromUserId) {
                    log.innerHTML += user.username + " : " + message.content + "\n";
                }
            });
        });

    };
}

function send() {
    var from = document.getElementById("username").value;
    var content = document.getElementById("msg").value;
    var targetUser = document.getElementById("targetUser").value;
    var targetGroup = document.getElementById("targetGroup").value;
    var targetBroadcast = document.getElementById("targetBroadcast").value;
    console.log(targetUser)
    console.log(typeof targetGroup)
    console.log(typeof targetBroadcast)
    $.get("http://localhost:8080/prattle/rest/user/" + from, function (fromdata, userstatus) {
        console.log(fromdata)
        if (targetUser !== "") {
            $.get("http://localhost:8080/prattle/rest/user/" + targetUser,
                  function (todata, fromstatus) {
                      console.log(todata)
                      var userJson = JSON.stringify({
                                                        "sourceMessageId": 1,
                                                        "content": content,
                                                        "fromUserId": fromdata.userID,
                                                        "toUserId": todata.userID,
                                                        "messageStatus": "DELIVERED",
                                                        "hasAttachment": false,
                                                    });
                      console.log(userJson)

                      ws.send(userJson);

                  });
        } else if (targetGroup !== "") {
            $.get("http://localhost:8080/prattle/rest/group/" + targetGroup,
                  function (todata, status) {
                      console.log(todata)
                      var groupJson = JSON.stringify({
                                                         "sourceMessageId": 1,
                                                         "content": content,
                                                         "fromUserId": fromdata.userID,
                                                         "toUserId": todata.groupID,
                                                         "messageStatus": "DELIVERED",
                                                         "hasAttachment": false,
                                                         "isGroupMessage": true
                                                     });
                      console.log(groupJson)

                      ws.send(groupJson);
                  });
        } else if (targetBroadcast !== "") {
            var broadcastJson = JSON.stringify({
                                                   "sourceMessageId": 1,
                                                   "content": content,
                                                   "fromUserId": fromdata.userID,
                                                   "toUserId": 1,
                                                   "messageStatus": "DELIVERED",
                                                   "hasAttachment": false,
                                                   "isBroadcastMessage": true
                                               });
            console.log(broadcastJson)

            ws.send(broadcastJson);
        }

    });
}