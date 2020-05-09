const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on('all_tasks', displayTasks);
socket.on('message', displayMessage);
socket.on('editTask', editTask);

function displayMessage(newMessage) {
    document.getElementById("message").innerHTML = newMessage;
}

function displayTasks(tasksJSON) {
    const tasks = JSON.parse(tasksJSON);
    const year= new Date().getFullYear();
    const month = new Date().getMonth()+1; // +1 because January returns 0.
    const day = new Date().getDate();
    const today = year+'-'+month+'-'+day; //The current date
    let formattedTasks = "";
    for (const task of tasks) {
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " +task['description'] + " <br/>";
        //Add the priority level to display
        formattedTasks += "<b>"+'Priority:'+ task['priority']+ " <br/>";
        if(dateDifference(today, task['dueDate'])>=0){
            formattedTasks += "<p class='dueDateFuture'>"+'Due in ' +dateDifference(today, task['dueDate'])+' days' + "<p/>" ;
        }
        else{
            formattedTasks += "<p class='dueDatePast'>"+'Due ' +dateDifference(task['dueDate'],today)+' days ago' + "<p/>" ;
        }
        // Assign two kinds of classes to two kinds of tasks so we can change the font color accordingly.
        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
        formattedTasks += "<button onclick='editTaskRequest(\"" + task['id'] + "\")'>Edit Task</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let priority = document.getElementById("priority").value;
    let dueDate = document.getElementById("dueDate").value;
    //Get the priority user inputs.
    socket.emit("add_task", JSON.stringify({"title": title, "description": desc, "priority": priority, "dueDate":dueDate}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("priority").value = "1";    //Reset the priority level to 1.
}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}

function sortTask(sortType) {
    socket.emit("sort_task",sortType); //Sort the tasks according to sort type users choose
    //Send "sort_task" type message to the server.
}

function dark_mode(){
    const element = document.body;
    element.classList.toggle("dark_mode");
    // toggle Dark Mode on the body of website
}


function dateDifference(date1, date2) {
    dt1 = new Date(date1);
    dt2 = new Date(date2);
    return Math.floor((Date.UTC(dt2.getFullYear(), dt2.getMonth(), dt2.getDate()) - Date.UTC(dt1.getFullYear(), dt1.getMonth(), dt1.getDate()) ) /(1000 * 60 * 60 * 24));
}// Return the number of the difference between two dates

function editTaskRequest(taskId) {
    socket.emit("editTask", taskId);
    alert('Please re-add this task in the input field above.');
    completeTask(taskId)
}

function editTask(taskJSON) {
    const task = JSON.parse(taskJSON);
    document.getElementById("title").value = task['title'];
    document.getElementById("desc").value = task['description'];
    document.getElementById("priority").value = task['priority'];
    document.getElementById("dueDate").value = task['dueDate'];
}