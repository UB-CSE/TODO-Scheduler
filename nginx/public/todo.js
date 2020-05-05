const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);
socket.on('login_user', loginUser);
socket.on('register_user', registerUser);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    for (const task of tasks) {
        if (task['title'].substr(0,3).localeCompare('(P)')) {
            formattedTasks += "<hr/>";
            formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>";
            formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
        } else {
            formattedTasks += "<hr/>";
            formattedTasks += "<span style='color:blue;'><b>" + task['title'] + "</b> - " + task['description'] + "</span><br/>";
            formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
        }
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask(buttonId) {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;

    if (buttonId == "addTaskPersonal") {
        socket.emit("add_personal_task", JSON.stringify({"title": title, "description": desc}));
    }
    else if (buttonId == "addTaskAll") {
        socket.emit("add_task", JSON.stringify({"title": title, "description": desc}));
    }

    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function login() {
    let username = document.getElementById("usernameInput").value;
    let password = document.getElementById("passwordInput").value;
    socket.emit("login_user", JSON.stringify({"username": username, "password": password}));

    document.getElementById("errorMsg").innerHTML = ""

    console.log("Logged in");
}

function register() {
    let username = document.getElementById("usernameInput").value;
    let password = document.getElementById("passwordInput").value;

    document.getElementById("errorMsg").innerHTML = ""

    socket.emit("register_user", JSON.stringify({"username": username, "password": password}));

    console.log("Registered");
}

function loginUser(user) {
    const currUser = JSON.parse(user)
    const username = currUser["username"]

    if (username != "") {
        document.getElementById("usernameInput").value = "";
        document.getElementById("passwordInput").value = "";

        document.getElementById("login").hidden = true;
        document.getElementById("message").innerHTML = "Welcome " + username + "!";
        document.getElementById("addTaskPersonal").style.display = "inline-block";
    } else {
        document.getElementById("usernameInput").value = "";
        document.getElementById("passwordInput").value = "";

        document.getElementById("errorMsg").innerHTML = "Unable to login user."
    }
}

function registerUser(user) {
    const currUser = JSON.parse(user)
    const username = currUser["username"]

    if (username != "") {
        document.getElementById("usernameInput").value = "";
        document.getElementById("passwordInput").value = "";

        document.getElementById("login").hidden = true;
        document.getElementById("message").innerHTML = "Welcome " + username + "!";
        document.getElementById("addTaskPersonal").style.display = "inline-block";
    } else {
        document.getElementById("usernameInput").value = "";
        document.getElementById("passwordInput").value = "";

        document.getElementById("errorMsg").innerHTML = "Unable to register user."
    }
}


