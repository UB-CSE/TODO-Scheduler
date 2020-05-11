const socket = io.connect("http://localhost:9006", {transports: ['websocket']});

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
        const d = new Date(parseInt(task.t));
        if (!task.completed) {
            formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
            formattedTasks += "\nCreated at " + d.toLocaleDateString() + " " + d.toTimeString()
        } else {
            formattedTasks += "✓  " + d.toLocaleDateString() + " at " + d.toTimeString()
        }
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
