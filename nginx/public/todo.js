const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

socket.on('noTitleEntered', displayError);

function displayError(currentDesc) {
    let tenure = prompt("Please enter a title", "type here");
    if (tenure != null) {
        socket.emit("add_task", JSON.stringify({"title": tenure, "description": JSON.parse(currentDesc)}));
    }
}

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>";
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
