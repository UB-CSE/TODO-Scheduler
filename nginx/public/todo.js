//const socket = io.connect("http://localhost:8080", {transports: ['websocket']});
const socket = io.connect({transports: ['websocket']});

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
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
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

function dailyChoresFunction() {
    let choreCheck = document.getElementById("daily_chores");
    if (choreCheck.checked == true) {
        socket.emit("add_daily_chores", JSON.stringify({"Opt":"in"}));
    }
    else {
        socket.emit("add_daily_chores", JSON.stringify({"Opt":"out"}));
    }
}

function weeklyChoresFunction() {
    let choreCheck = document.getElementById("weekly_chores");
    if (choreCheck.checked == true) {
        socket.emit("add_weekly_chores", JSON.stringify({"Opt":"in"}));
    }
    else {
        socket.emit("add_weekly_chores", JSON.stringify({"Opt":"out"}));
    }
}