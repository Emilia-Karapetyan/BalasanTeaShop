let confInp=document.querySelector("#InputPassword2");
let codeInp=document.querySelector("#InputCode")

if(confInp.placeholder==="Password is not correct"){
    confInp.classList.add('errorPlaceholder');
}

if(codeInp.placeholder==="Code is not correct"){
    codeInp.classList.add('errorPlaceholder');
}


