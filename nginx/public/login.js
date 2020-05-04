function createLoginForm() {
    let loginDiv = document.createElement("DIV");
    let usernameInput = document.createElement("INPUT");
    let passwordInput = document.createElement("INPUT")
    let loginSubmit = document.createElement("INPUT")
    let registerSubmit = document.createElement("INPUT")

    usernameInput.setAttribute("type", "text");
    usernameInput.setAttribute("id", "usernameInput");

    passwordInput.setAttribute("type", "password");
    passwordInput.setAttribute("id", "passwordInput");

    loginSubmit.setAttribute("type", "button");
    loginSubmit.setAttribute("id", "loginSubmit");
    loginSubmit.setAttribute("onclick", "login()");
    loginSubmit.setAttribute("value", "Login");

    registerSubmit.setAttribute("type", "button");
    registerSubmit.setAttribute("id", "registerSubmit");
    registerSubmit.setAttribute("onclick", "register()");
    registerSubmit.setAttribute("value", "Register");

    loginDiv.innerHTML = "Username: ";
    loginDiv.appendChild(usernameInput);

    loginDiv.innerHTML += "<br> Password: ";
    loginDiv.appendChild(passwordInput);

    loginDiv.innerHTML += "<br>";
    loginDiv.appendChild(registerSubmit);
    loginDiv.appendChild(loginSubmit);

    return loginDiv
}

function login() {
    console.log("Logged in")
}

function register() {
    console.log("Registered")
}

let body = document.getElementsByTagName("body")[0]

body.prepend(createLoginForm())

