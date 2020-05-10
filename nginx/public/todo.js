const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);
socket.on('isValid_form', isValid_form);
socket.on('all_private_tasks', displayPrivateTasks);

function isValid_form(isValidJSON) {
    const json = JSON.parse(isValidJSON);
    if(json["isValid"]){
        document.getElementById(json["element"]).className = "form-control"
    }else{
        document.getElementById(json["element"]).classList.add("is-invalid")
    }

}

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

// Basic random color picker
// TODO color picker should be darker colors
function randomColorPicker() {
    const hex = "0123456789ABCDEF";
    let random_color = "#";
    for (let i = 0; i < 6; i++) {
        random_color += hex[(Math.floor(Math.random() * (-hex.length + 1)) + hex.length)];
    }
    return random_color
}


function toHTML(tasks, taskAction) {
    let formattedTasks = "";
    for (const task of tasks) {
        formattedTasks += "<hr/><div class='card'><a data-toggle='collapse' data-target='#a" + task['id'] + "' aria-expanded='true'>";
        formattedTasks += "<div class='card-header text-white' style='background-color: " + randomColorPicker() + "; display: flex; justify-content: space-between;'>";
        formattedTasks += "<div>" + task["title"] + "</div> ";
        formattedTasks += "<button class='btn btn-raised btn-danger fa fa-times-circle' onclick='" + taskAction + "(\"" + task['id'] + "\")'></button></div></a>";
        formattedTasks += "<div id='a" + task['id'] + "' class='collapse show'>";

        if (!(task["description"] === "")) {
            formattedTasks += "<text class='list-group-item'>" + task["description"] + "</text>";
        }

        formattedTasks += "<text class='list-group-item'>" + 'Created: ' + task["start"];
        if (!(task["deadline"] === "")) {
            formattedTasks += ' - Deadline: ' + task["deadline"]
        }
        formattedTasks += "</text>";

        if (task["tag"] === "") {
            formattedTasks += "<text class='list-group-item'>" + 'Tag: None' + "</text>";
        } else {
            formattedTasks += "<text class='list-group-item'>" + 'Tag: ' + task["tag"] + "</text>";
        }
        formattedTasks += "<text class='list-group-item'>" + 'Priority: ' + task["priority"] + "</text></div></div>";
    }
    formattedTasks += "<br/>";
    return formattedTasks
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    document.getElementById("tasks").innerHTML = toHTML(tasks, "completeTask");
}

function displayPrivateTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    document.getElementById("privateTasks").innerHTML = toHTML(tasks, "completePrivateTask");
}


function addTask() {
    // Key store (key -> default value)
    let Data = {
        "title": "",
        "description": "",
        "deadline": "",
        "tag": "",
        "priority": "None",
        "user": ""
    };
    // Set JSON values and reset display values
    for (let element of Object.keys(Data)) {
        let default_value = Data[element];
        Data[element] = document.getElementById(element).value;
        document.getElementById(element).value = default_value;
    }
    Data["private"] = !(document.getElementById("publicNav").className.includes("active"));
    socket.emit("add_task", JSON.stringify(Data));

}

function sortMessage(sortKey) {
    socket.emit("sort_message", sortKey)
}


function dropDownSetter(element, val) {
    document.getElementById(element).value = val;
    if (element === "sortBy") {
        sortMessage(document.getElementById(element).value)
    }
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function completePrivateTask(taskId) {
    socket.emit("complete_private_task", taskId);
}

