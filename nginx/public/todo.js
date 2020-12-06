const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    let due;
    for (const task of tasks) {
        if(task['deadline'].length > 0) {
            due = "Due "
        }
        else {
            due = "-"
        }
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>";
        formattedTasks += "<b>" + due + "</b> - " + task['deadline']  + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let dl = document.getElementById("dl").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "deadline": dl}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("dl").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
