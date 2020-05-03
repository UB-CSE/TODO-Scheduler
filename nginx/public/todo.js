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
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "- Due by: <i>" + task["due_date"] + "</i>" + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let due = document.getElementById("due").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc,"due_date": due}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("due").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
