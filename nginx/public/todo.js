const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    console.log(tasks)
    let formattedTasks = "";
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>";
        formattedTasks += "Priority: " + task['priority'] + "<br\>"
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}

function getPriority(jsonObj) {
    return jsonObj.priority
}

function getId(jsonObj) {
    return parseInt(jsonObj.id)
}

function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let priority = parseInt(document.getElementById("priority").value);
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "priority": priority}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("priority").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
