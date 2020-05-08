const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);
socket.on('comment', displayComment);
socket.on('date', displayDate);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayComment(newComment){
    document.getElementById("comment").innerHTML = newComment;
}


function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " +
            task['description'] + "</b> -" +
            task['comment'] + "</b> -" +
            "Schedule for/due on " + task['date'] + "<br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}

function displayDate(date){
    document.getElementById("date").innerHTML = date
}

function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let com = document.getElementById("comment").value;
    let date = document.getElementById("date").value;
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "comment":com, "date": date}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("comment").value = "";
    document.getElementById("date").value = "";

}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
