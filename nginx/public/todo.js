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
        formattedTasks +=
            "<b><p style=\"font-size:20px\">To Do: " + task['title'] + "</p></b>" +
            "<i><p style=\"font-size:15px\">Comments: " + task['description'] + "</p></i><br>";
        if(!task['id'].includes("COMPLETED")) {
            formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
            formattedTasks += "<br><button onclick='deleteTask(\"" + task['id'] + "\")'>Delete</button>";
        } else {
            formattedTasks += "<i><b>COMPLETED TASK</b></i>" ;
            formattedTasks += " <button onclick='undoTask(\"" + task['id'] + "\")'>Undo</button><br>";
            formattedTasks += "<button onclick='deleteTask(\"" + task['id'] + "\")'>Delete</button>";
        }
        formattedTasks += "<br><i><p style=\"font-size:9px\">Added on " + task['today'] + "</p></i>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}

function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let today = new Date().toString();
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "today": today}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function deleteTask(taskId) {
    socket.emit("delete_task", taskId)
}

function undoTask(taskId) {
    socket.emit("undo_task", taskId)
}