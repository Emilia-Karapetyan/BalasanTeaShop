let emailInp=document.querySelector("#InputEmail1");
let passInp=document.querySelector("#InputPassword1");
let  passConf=document.querySelector("#InputPassword2");
let  phoneInp=document.querySelector("#PhoneNumber");


if(emailInp.placeholder==="Email is busy"){
    emailInp.classList.add('errorPlaceholder')
}
if(phoneInp.placeholder==="Phone is not correct" || phoneInp.placeholder==="Phone is Busy"){
    phoneInp.classList.add('errorPlaceholder')
}
if(passInp.placeholder==="Min size 8 symbol"){
    passInp.classList.add('errorPlaceholder');
}
if (passConf.placeholder==="Password is not correct"){
    passConf.classList.add('errorPlaceholder')
}