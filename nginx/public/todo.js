const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

let BUTTON = "<div class=\"todo\">\n" +
    "        <h1 style=\"margin: 2% 0 0;\">TITLE</h1>\n" +
    "        <p>DESCRIPTION</p>\n" +
    "        <button onclick=\"\" style=\"margin-bottom: 2%; background-color: #7158e2; color: white\">Task Complete</button>\n" +
    "    </div>"

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let l = ""
    for (const task of tasks) {
        l += "<div class=\"todo\">\n" +
            "        <h1 style=\"margin: 2% 0 0;\">"+task['title']+"</h1>\n" +
            "        <p>"+task['description']+"</p>\n" +
            "        <button onclick=\"completeTask('"+task['id']+"')\" style=\"margin-bottom: 2%; background-color: #4b4b4b; color: white\">Task Complete</button>\n" +
        "    </div>"
    }
    document.getElementById("todos").innerHTML = l
    //document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("TITLE").value;
    let desc = document.getElementById("DESC").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
