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
        formattedTasks += "<b>"+ "Estimated Time:" + "</b> " +  task['time'] + "<br/>" ;
        formattedTasks += "<b>"+ task['priority'] + " Priority" + "</b>" + "<br/>" ;
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let time = document.getElementById("time").value ;
    let priority = document.getElementById("priority").value ;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "time":time,"priority":priority}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("time").value = "";
    document.getElementById("priority").value = "Low";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
