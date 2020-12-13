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
        formattedTasks += "<b>" + "Notes : </b>" + task['subTasks'] + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
        formattedTasks += "<button onclick='subTask(\"" + task['id'] + "\")'>Add Note</button>";
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

function subTask(taskId) {
    let desc = document.getElementById("desc").value;
    socket.emit("sub_task", JSON.stringify({"id":taskId,"desc":desc}))
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
}
