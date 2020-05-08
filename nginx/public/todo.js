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
        if(task['time10']+task['time11']+task['time12']==task['time20']+task['time21']+task['time22']){
            formattedTasks += task['time10'] + ":" + task['time11'] + " " + task['time12'] + "<br/>"
        }
        else {
            formattedTasks += task['time10'] + ":" + task['time11'] + " " + task['time12'] + " - " + task['time20'] + ":" + task['time21'] + " " + task['time22'] + "<br/>"
        }
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let time10 = document.getElementById("time10").value;
    let time11 = document.getElementById("time11").value;
    let time12 = document.getElementById("time12").value;
    let time20 = document.getElementById("time20").value;
    let time21 = document.getElementById("time21").value;
    let time22 = document.getElementById("time22").value;
    if(title.toString != "" && desc.toString != ""){
        socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "time10": time10, "time11": time11, "time12": time12, "time20": time20, "time21": time21, "time22": time22}));
        document.getElementById("title").value = "";
        document.getElementById("desc").value = "";
        document.getElementById("time10").value = "12";
        document.getElementById("time11").value = "00";
        document.getElementById("time12").value = "am";
        document.getElementById("time20").value = "12";
        document.getElementById("time21").value = "00";
        document.getElementById("time22").value = "am";
    }
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
