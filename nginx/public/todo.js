const socket = io.connect("http://localhost:8080", {transports: ['websocket']});
socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);



htmlSetup()

function htmlSetup() {
    socket.on('start_tasks', function(event)
    {

        let indexHTML = "<label for=\"title\">Title: </label><input type=\"text\" id=\"title\"/><br/>";
        indexHTML += "<label for=\"desc\">Description: </label><input type=\"text\" id=\"desc\"/><br/>";
        indexHTML += "<button onclick=\"addTask();\">Add Task</button>";
        indexHTML += '<br/><br/>' + "<div id=\"tasks\"></div>"
        let newMessage = 'Do it! Just...Do it!'
        document.getElementById('TODO').innerHTML = indexHTML;
        document.getElementById('message').innerHTML = newMessage;
        socket.emit("all_tasks",event)
    });
    socket.on('userState', function (event){
        let title = document.getElementById("title").value;
        let desc = document.getElementById("desc").value;
        socket.emit("add_task", JSON.stringify({"title": title, "description": desc}));
        document.getElementById("title").value = "";
        document.getElementById("desc").value = "";
    });
}
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

function submitUsername() {
    const enteredUsername = document.getElementById("username").value;
    if (enteredUsername !== "") {
        username = enteredUsername;
        socket.emit("startTask", enteredUsername);
    }
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
