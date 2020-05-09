const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);
socket.on('task_does_not_exist', displayPopup)

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

function saveTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    socket.emit("save_task", JSON.stringify({"title": title, "description": desc}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
}

function addFromSavedTasks() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    socket.emit("add_from_saved_tasks", JSON.stringify({"title": title, "description": desc}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
}

function displayPopup(TaskTitle) {
    alert("Task title named '"+TaskTitle+"' does noct exist! Save new Task!");
}