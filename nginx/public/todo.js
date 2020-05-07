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
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + "Deadline:" + task['deadline'] +
            "<br/>" + "Task Added: " + task['taskAdded'] + "<br/>" + "Priority Level: " + task['priority'] + "<br/>" + "Time to complete: " + task['estimated'] + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Finished!</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let deadline = document.getElementById("deadline").value;
    let priority = document.getElementById("priority").value;
    let estimated = document.getElementById("estimated").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "deadline": deadline, "priority": priority, "estimated": estimated}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("deadline").value = "";
    document.getElementById("priority").value = "";
    document.getElementById("estimated").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
