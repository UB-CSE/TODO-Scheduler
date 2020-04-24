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
        let priority=""; if (task['priority']==="1"){priority="Low"}else if(task[priority]==="2"){priority="Medium"}else if(task[priority]==="3"){priority="High"}else{priority="None"};
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/> Due: " + task['date'] + "  Priority: " + priority + "<br/><br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let date = document.getElementById("datepicker").value;
    let priority = document.getElementById("priority").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "date": date, "priority": priority}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("datepicker").value = "";
    document.getElementById("priority").value = "0";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
