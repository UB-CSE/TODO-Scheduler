const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
	  document.getElementById("tasks").innerHTML = "";

    let formattedTasks = "";
    for (const task of tasks) {

        formattedTasks += "<hr/>";
        formattedTasks += "<b id=\"" + task['id'] + "\">" + task['title'] + "</b> - " + task['description'] + " <b>by " + task['deadline'] + "</b><br/>";
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
	      document.getElementById("tasks").innerHTML += formattedTasks;

        // Compare deadline to today's late
        // We add the timezone offset to the deadline because today's date is GMT and deadline is the local timezone date
        let today = new Date();
            today.setHours(0,0,0,0);
        let deadline = new Date(task['deadline']);
            deadline.setMinutes(deadline.getMinutes() + deadline.getTimezoneOffset());


        // Mark lateness of tasks
        if(today > deadline) taskLate(task['id']);
        else if(+today === +deadline) taskAlmostLate(task['id']);

		    formattedTasks = "";
	}
}

function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let date = document.getElementById("date").value;

    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "deadline": date}));

    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("date").valueAsDate = new Date();
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function taskLate(id) {
  let elem = document.getElementById(id);
  let title = elem.innerHTML;

  elem.innerHTML = "LATE: " + title;
  elem.style = "color:red;";
}

function taskAlmostLate(id) {
  let elem = document.getElementById(id);
  let title = elem.innerHTML;

  elem.innerHTML = "DUE TODAY: " + title;
  elem.style = "color:orange;";
}
