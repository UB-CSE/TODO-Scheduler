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
        formattedTasks += "<b>Due on: </b>" + new Date(task['dueDate']).toDateString() + "<br/>";
        formattedTasks += "<b>Comment: </b>" + task['comment'] + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let dueDate  = document.getElementById("due").value;
    let comment = document.getElementById("comment").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "dueDate": dueDate,
                                                   "comment": comment}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("due").value = "";
    document.getElementById("comment").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
