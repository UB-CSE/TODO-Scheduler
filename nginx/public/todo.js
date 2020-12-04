const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);
socket.on('priority_error', priorityError);
socket.on('clear_priority_error', clearPriorityError);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>";
        formattedTasks += "Priority: " + task['priority'] + "<br/>";
        formattedTasks += "<button class=\"btn btn-primary\" onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let priority = document.getElementById("priority").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "priority": priority}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("priority").value = "";
}


function priorityError() {
    document.getElementById("priority-error").innerHTML = "Error: Priority must be a number";
}

function clearPriorityError() {
    document.getElementById("priority-error").innerHTML = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
