const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const sortMode = document.getElementById("sort-by").value;
    const sortFunction = (sortMode === "priority" ? comparePriority : compareId)
    var tasks = JSON.parse(tasksJSON);
    tasks.sort(sortFunction);
    let formattedTasks = "";
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>";
        formattedTasks += "Priority: " + task['priority'] + "<br\>"
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}

function refreshTasks() {
    socket.emit("get_tasks")
}

function comparePriority(jsonObj1, jsonObj2) {
    return parseInt(jsonObj2['priority']) - parseInt(jsonObj1['priority'])
}

function compareId(jsonObj1, jsonObj2) {
    return parseInt(jsonObj2['id']) - parseInt(jsonObj1['id'])
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
