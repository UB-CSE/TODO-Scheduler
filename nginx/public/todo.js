const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

let myName = "";
let named = false;
let all = true;

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + "<br/>" + "<i>" + "Added By - "  + task['name'] + "</i>"+"<br/>" + "<br/>" + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    if (!named){
        let title = document.getElementById("title").value;
        let desc = document.getElementById("desc").value;
        let name = document.getElementById("name").value;
        socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "added_by":name}));
        document.getElementById("title").value = "";
        document.getElementById("desc").value = "";
        document.getElementById("name").value = "";
        document.getElementById("initialName").innerHTML = "";
        named=true;
        myName = name;
    }
    else{
        let title = document.getElementById("title").value;
        let desc = document.getElementById("desc").value;
        let name = myName;
        socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "added_by":name}));
        document.getElementById("title").value = "";
        document.getElementById("desc").value = "";
        document.getElementById("name").value = "";
    }

}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function switchView() {
    if (named){
        if (all){
            socket.emit("switch",myName);
            all=false;
        }
        else{
            socket.emit("switch","");
            all=true
        }

    }
}