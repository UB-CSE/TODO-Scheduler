const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}
function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    let mytask ="";
    for (const task of tasks) {
        mytask += "<hr/>";
        mytask += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>";
        mytask += "<b>" + "Due on: " + task['due'] + "<br/>";
        mytask += "<b>" + "Date Created: " + task['date'] + "<br/>";
        mytask += "<b>" + " TASK COMPLETED " + "<br/>";

        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>";
        formattedTasks += "<b>" + "Due on: " + task['due'] + "<br/>";
        formattedTasks += "<b>" + "Date Created: " + task['date'] + "<br/>";
        formattedTasks += "</b><button onclick='completeTask(\"" + task['id'] + "\")'>Remove Task</button> <br/>";
        formattedTasks += "<b></b><button onclick='history(\"" + mytask + "\")'>Task Complete </button>";
        mytask = "";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let due = document.getElementById("due").value;
    let date = document.getElementById("date").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "due": due, "date": date}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("due").value = "";
    document.getElementById("date").value = "";
}

function history(m){
    document.getElementById("history").innerHTML += m;
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
