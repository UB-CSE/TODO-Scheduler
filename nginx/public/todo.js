const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    let d = new Date();
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + "Need done by: " + task['deadline'] + "<br/>";
        formattedTasks += "Created on " + d.toLocaleDateString() + " at the time of " + d.toLocaleTimeString() + "<br/>";
        formattedTasks += "priority: " + task['priority'] + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}

let priority = "2";

function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let deadline = document.getElementById("deadline").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "deadline": deadline, "priority": priority}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("deadline").value = "";
    priority = 2
}

function urgency(id) {
    let obj = {"urgent": "1", "moderate": "2", "trivial": "3"};
    priority = obj[id]
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
