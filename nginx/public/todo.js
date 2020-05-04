const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "<b>" + "All Tasks" + "</b>";
    let formattedYourTasks = "<b>" + "Your Tasks" + "</b>";
    let youHaveTasks = false;
    for (const task of tasks) {
        if (task['doer'] === document.getElementById("userID").innerText) {
            youHaveTasks = true;
            formattedYourTasks += "<hr/>";
            formattedYourTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + task['doer'] + "<br/>";
            formattedYourTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
            formattedTasks += "<hr/>";
            formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + task['doer'] + "<br/>";
            formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
        } else {
            formattedTasks += "<hr/>";
            formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>" + task['doer'] + "<br/>";
            formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
            formattedTasks += "<button onclick='claimTask(\"" + task['id'] + "\")'>Claim Task</button>";
        }
    }

    if (!youHaveTasks) {
        formattedYourTasks = "";
    } else {
        formattedTasks = "<br>" + "<br>" + formattedTasks;
    }

    if (formattedTasks === "<b>" + "All Tasks" + "</b>" || formattedTasks === "<br>" + "<br>" +"<b>" + "All Tasks" + "</b>"){
        formattedTasks = "";
    }

    document.getElementById("all tasks").innerHTML = formattedTasks;
    document.getElementById("your tasks").innerHTML = formattedYourTasks;
}

function submitUsername() {
    const enteredUsername = document.getElementById("username").value;
    if (enteredUsername !== "") {
        username = enteredUsername;
        socket.emit("open_list", enteredUsername);
    }
    document.getElementById("message").innerHTML = "Welcome, " + username + "!";
    document.getElementById("userID").innerHTML = enteredUsername;
    showList();
    socket.emit("sign_on", "")
}

function showList() {
    let listHTML = '<label for="title">Title: </label><input type="text" id="title"/><br/> ';
    listHTML += '<label for="desc">Description: </label><input type="text" id="desc"/><br/>';
    listHTML += '<label for="doer">Who Should Do This?: </label><input type="text" id="doer"/><br/>';
    listHTML += '<button onclick="addTask();">Add Task</button>';
    document.getElementById("list").innerHTML = listHTML;
}

function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let doer = document.getElementById("doer").value;
    if (doer === "") {
        doer = "Anyone";
    }
    if (title !== "" || desc !== ""){
        socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "doer": doer}));
        document.getElementById("title").value = "";
        document.getElementById("desc").value = "";
        document.getElementById("doer").value = "";
    }
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function claimTask(taskId) {
    let username = document.getElementById("userID").innerHTML;
    socket.emit("claim_task", JSON.stringify({"taskID": taskId, "userID": username}));
}