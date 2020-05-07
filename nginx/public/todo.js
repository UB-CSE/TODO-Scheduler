const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);
socket.on('clear', completeTask);
socket.on('add', reorderAdd);

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
        formattedTasks += "<select id='indicies' name='indicies'>";

        for(var value of tasks) {
            if(value['id'] === task['id']) {
                formattedTasks += "<option value='" + value['id'] + "' selected onclick='reorderTasks(\"" + task["id"] + "," + value["id"] + "\")'>" + value['id'] + "</option>";
            }
            else {
                formattedTasks += "<option value='" + value['id'] + "' onclick='reorderTasks(\"" + task["id"] + "," + value["id"] + "\")'>" + value['id'] + "</option>";
            }
        }
        formattedTasks += "</select>";
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

function reorderTasks(taskId, newId) {
    let data = Array(taskId, newId);
    socket.emit("reorder", data);
}

function reorderAdd(task) {
    let nTask = task.split('`');
    socket.emit("add_task", JSON.stringify({"title": nTask[0], "description": nTask[1]}));
}