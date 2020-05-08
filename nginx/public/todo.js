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
        let title = task['title'];      //changes colors based on priority level
        let prior = task['priority']
        if(prior == "4"){
            title = title.fontcolor('red');
        }
        else if(prior == "3"){
            title = title.fontcolor('orange');
        }
        else if(prior == "2"){
            title = title.fontcolor('purple');
        }
        else{
            title = title.fontcolor('blue');
        }
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + title + "</b> - " + task['description'] + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let prior = document.getElementById("prior").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "priority": prior}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("prior").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function clearTasks(){
    socket.emit("clear_tasks") //sends clear task message to server
}
