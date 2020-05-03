const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/><br/>";
        formattedTasks += "<b>Comments: </b><br/><br/>"
        let comments = task["comments"].split("_____");
        for (let comment of comments) {
            if (comment.length != 0) {
                formattedTasks += comment + "<br/>";
            };
        };
        formattedTasks += "<label for='comment" + task['id'] + "'>Add Comment: </label><input type='text' id='comment" + task['id'] +"'/><br/>";
        formattedTasks += "<button onclick='addComment(\"" + task['id'] + "\")'>Add Comment</button>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function addComment(taskId) {
    let comment = document.getElementById("comment" + taskId).value;
    if (comment.length != 0) {
        socket.emit("add_comment", JSON.stringify({"taskId": taskId, "comment": comment}));
        document.getElementById("comment" + taskId).value = "";
    }
}
