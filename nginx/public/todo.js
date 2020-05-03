const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

const datepicker = $('#deadline');
datepicker.datetimepicker();

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    let formattedTasks = "";
    for (const task of tasks) {
        const deadline = moment(task['deadline']).format('LLL'); // https://momentjs.com/docs/
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] + "<br/>";
        formattedTasks += `<b>Deadline: </b>${deadline}<br/>`;
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    const deadline = new Date(datepicker.datetimepicker('getValue')).getTime();

    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, deadline}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("deadline").value = "";
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function sortTasks(sortBy) {
    socket.emit('sort_tasks', sortBy);
}
