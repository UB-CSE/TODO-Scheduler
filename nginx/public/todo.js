const socket = io.connect("http://localhost:8090", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);
socket.on('error',displayError)


function displayError(){
    alert("The deadline you input is before current time or the format is wrong")
}



function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + "<b>Deadline is: </b>" + task['deadline'] + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let deadline = document.getElementById("deadline").value
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "deadline": deadline}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("deadline").value = ""
}


function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

