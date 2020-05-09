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
       //Add the priority level to display
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description']+ "- " + task['priority']+ " <br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let priority = document.getElementById("priority").value;
    //Get the priority user inputs.
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "priority": priority}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("priority").value = "1";
    //Reset the priority level to 1.
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function sortTask() {
    socket.emit("sort_task");
    //Send "sort_task" type message to the server.
}

function dark_mode(){
    const element = document.body;
    element.classList.toggle("dark_mode");
    // toggle Dark Mode on the body of website
}