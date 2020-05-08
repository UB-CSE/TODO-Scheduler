const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    let dic = {"4" : "Extreme", "3": "High", "2": "Medium", "1": "Low" }
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        if (task["priority"] === "4") {
            formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + "<b style='color: red; background-color: #5a5a5a'>" + dic[task['priority']] + "</b>"+"<br>";
            formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
        }else if (task["priority"] === "3"){
            formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + "<b style='text-align: right; color: orange; background-color: #5a5a5a'>" + dic[task['priority']] + "</b>"+"<br>";
            formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
        }else if (task["priority"] === "2"){
            formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + "<b style='text-align: right; color: #00ffbe; background-color: #5a5a5a'>" + dic[task['priority']] + "</b>"+"<br>";
            formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
        }else if (task["priority"] === "1"){
            formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + "<b style='text-align: right; color: #00a700; background-color: #5a5a5a'>" + dic[task['priority']] + "</b>"+"<br>";
            formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
        }
    }
    //print(formattedTasks)
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let priority = document.getElementById("priorities").value
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "priorities" : priority}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}