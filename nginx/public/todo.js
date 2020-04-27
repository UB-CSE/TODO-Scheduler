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
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + "Priority: " + task['priority'] + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let choices = document.getElementsByName("priority"); //gets all 3 choices in a collection
    let priority = "none"; //"none" if no priority selected
    let priorityId;
    for (option of choices) { //iterate through high, med, low
        if (option.checked) { //find which one was checked
            priority = option.value; //save reference to value
            priorityId = option.id //save reference to id to unselect after submitting
        }
    }
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "priority": priority})); //added priority
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById(priorityId).checked = false; //uncheck priority selection
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
