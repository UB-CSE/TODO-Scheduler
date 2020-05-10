
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
        formattedTasks += "<hr/>";
        formattedTasks += "<b>" + task['title'] + "</b> - " + task['description'] +"<br/>";
        formattedTasks += "<b>" + "Time Made :" + "</b> - " + task['dateMade'] +"<br/>";
        formattedTasks += "<b>" + "Time Due :" + "</b> - " + task['date_due'] +"<br/>";
        formattedTasks += "<b>" + "Priority :" + "</b> " + task['priority'] +"<br/>";



        formattedTasks += "<button onclick='completeTask(\"" + task['id'] + "\")'>Task Complete</button>";
    }
    document.getElementById("tasks").innerHTML = formattedTasks;
}
function getTime(){
    var today = new Date();
    var date = (today.getMonth()+1)+'/'+today.getDate()+"/"+today.getFullYear()+'@ ';
    var currentHour=today.getHours();
    var currentMinute=today.getMinutes();
    var time="";
    if(currentHour > 12) {
        currentHour %= 12;
        if(currentMinute<10){
            time=currentHour.toString()+"-"+"0"+currentMinute.toString()+" PM"}
        else{
            time=currentHour.toString()+"-"+currentMinute.toString()+" AM"
        }
    }
   else{
        if(currentMinute<10){
            time=currentHour.toString()+"-"+"0"+currentMinute.toString()+" AM"
        }
        else{
        time=currentHour.toString()+"-"+currentMinute.toString()+" AM"
         }
    }

        let dateTime = date+' '+time;

    return dateTime.toString();
}


function addTask() {
    let title = document.getElementById("title").value;
    let desc = document.getElementById("desc").value;
    let dateMade=getTime();
    let dateDue=document.getElementById("date_due").value;
    console.log(dateDue);
    let priority=document.getElementById("priority").value;

    socket.emit("add_task", JSON.stringify({"title": title, "description": desc,"dateMade":dateMade,"date_due":dateDue,"priority":priority.toString()}));
    document.getElementById("title").value = "";
    document.getElementById("desc").value = "";
    document.getElementById("date_due").value="";



}

function completeTask(taskId) {
    socket.emit("complete_task", taskId);
}
