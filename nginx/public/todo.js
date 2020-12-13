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
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>";
        formattedTasks += "<b>" + "Priority: " + task['priority'] + " - Due Time and Date: " + task['dueTimeDate'] + "<b><br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let priority = parseInt(document.getElementById("priority").value);
    if (priority > 10 || priority < 1) {
        priority = 10
    }
    priority = priority.toString()
    let dueTimeDate = document.getElementById("dueTimeDate").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "priority": priority, "dueTimeDate": dueTimeDate}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("priority").value = "";
    document.getElementById("dueTimeDate").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
