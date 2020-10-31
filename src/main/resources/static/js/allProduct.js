$(".accept").click(function () {
    let id=this.dataset.id;
    $.ajax({
        type:"GET",
        url:"/acceptProduct",
        data:{
            id:id
        },success:()=>{
            let i=document.createElement("i");
            i.className="fas fa-check-circle";
            i.style.fontSize="48px";
            i.style.color="#B0B435";
            this.parentElement.append(i);
            let btn=document.createElement("button");
            btn.className="btn hvr-hover ignore";
            btn.type="button";
            btn.dataset.id=id;
            btn.innerHTML="Ignore";
            btn.style.background="#FF0000";
            btn.addEventListener("click",ignore);
            this.parentElement.parentElement.children[7].children[0].remove();
            this.parentElement.parentElement.children[7].append(btn);
            this.remove();
        }
    })
})

$(".ignore").click(function () {
    let id=this.dataset.id;
    $.ajax({
        type:"GET",
        url:"/ignoreProduct",
        data:{
            id:id
        },success:()=>{
            let i=document.createElement("i");
            i.className="fa fa-remove";
            i.style.fontSize="48px";
            i.style.color="#FF0000";
            this.parentElement.append(i);
            let btn=document.createElement("button");
            btn.className="btn hvr-hover accept";
            btn.type="button";
            btn.dataset.id=id;
            btn.innerHTML="Accept";
            btn.addEventListener("click",accept);
            this.parentElement.parentElement.children[6].children[0].remove();
            this.parentElement.parentElement.children[6].append(btn);
            this.remove();
        }
    })
})
function ignore() {
    let id=this.dataset.id;
    $.ajax({
        type:"GET",
        url:"/ignoreProduct",
        data:{
            id:id
        },success:()=>{
            let i=document.createElement("i");
            i.className="fa fa-remove";
            i.style.fontSize="48px";
            i.style.color="#FF0000";
            this.parentElement.append(i);
            let btn=document.createElement("button");
            btn.className="btn hvr-hover accept";
            btn.type="button";
            btn.dataset.id=id;
            btn.innerHTML="Accept";
            btn.addEventListener("click",accept);
            this.parentElement.parentElement.children[6].children[0].remove();
            this.parentElement.parentElement.children[6].append(btn);
            this.remove();
        }
    })
}

function accept() {
    let id=this.dataset.id;
    console.log(id);
    $.ajax({
        type:"GET",
        url:"/acceptProduct",
        data:{
            id:id
        },success:()=>{
            let i=document.createElement("i");
            i.className="fas fa-check-circle";
            i.style.fontSize="48px";
            i.style.color="#B0B435";
            this.parentElement.append(i);
            let btn=document.createElement("button");
            btn.className="btn hvr-hover ignore";
            btn.type="button";
            btn.dataset.id=id;
            btn.innerHTML="Ignore";
            btn.style.background="#FF0000";
            btn.addEventListener("click",ignore);
            this.parentElement.parentElement.children[7].children[0].remove();
            this.parentElement.parentElement.children[7].append(btn);
            this.remove();
        }
    })
}