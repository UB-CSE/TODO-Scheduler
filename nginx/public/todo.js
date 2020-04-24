const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);
socket.on('closest',displayDeadline);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayDeadline(deadline, deadlineTitle){
    document.getElementById("closestDeadline").innerHTML = "Your closest assignment is: " +  deadlineTitle  + " which is due on " + deadline;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + "<b>Deadline: </b>" + task['deadline'] + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}

function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let deadline = document.getElementById("deadline").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "deadline": deadline}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("deadline").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function closestDeadline(){
    socket.emit("closest_deadline")
}


